package org.egov.wf.service;

import static org.egov.wf.util.WorkflowConstants.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.common.contract.request.User;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.MdmsResponse;
import org.egov.mdms.model.ModuleDetail;
import org.egov.tracer.model.CustomException;
import org.egov.wf.config.WorkflowConfig;
import org.egov.wf.repository.BusinessServiceRepository;
import org.egov.wf.repository.WorKflowRepository;
import org.egov.wf.util.WorkflowUtil;
import org.egov.wf.web.models.Action;
import org.egov.wf.web.models.AuditDetails;
import org.egov.wf.web.models.BusinessService;
import org.egov.wf.web.models.BusinessServiceRequest;
import org.egov.wf.web.models.BusinessServiceSearchCriteria;
import org.egov.wf.web.models.ProcessInstance;
import org.egov.wf.web.models.ProcessInstanceRequest;
import org.egov.wf.web.models.ProcessInstanceSearchCriteria;
import org.egov.wf.web.models.ProcessStateAndAction;
import org.egov.wf.web.models.State;
import org.egov.wf.web.models.user.UserSearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;


@Service
@Slf4j
public class EnrichmentService {


    private WorkflowUtil util;

    private UserService userService;

    private TransitionService transitionService;

    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private WorkflowConfig workflowConfig;
    
    @Autowired
    private BusinessServiceRepository businessServiceRepository;
    
    @Autowired
    private WorKflowRepository worKflowRepository;

    @Value("${egov.mdms.host}")
    private String mdmsHost;

    @Value("${egov.mdms.search.endpoint}")
    private String mdmsUrl;
    
    @Autowired
    private MDMSService mdmsService;

    @Autowired
    public EnrichmentService(WorkflowUtil util, UserService userService,TransitionService transitionService) {
        this.util = util;
        this.userService = userService;
        this.transitionService = transitionService;
    }




    /**
     * Enriches incoming request
     * @param requestInfo The RequestInfo of the request
     * @param processStateAndActions List of ProcessStateAndAction containing ProcessInstance to be created
     */
    public void enrichProcessRequest(RequestInfo requestInfo,List<ProcessStateAndAction> processStateAndActions){
        AuditDetails auditDetails = util.getAuditDetails(requestInfo.getUserInfo().getUuid(),true);
        processStateAndActions.forEach(processStateAndAction -> {
            String tenantId = processStateAndAction.getProcessInstanceFromRequest().getTenantId();
            processStateAndAction.getProcessInstanceFromRequest().setId(UUID.randomUUID().toString());
            if(processStateAndAction.getAction().getNextState().equalsIgnoreCase(processStateAndAction.getAction().getCurrentState())){
                auditDetails.setCreatedBy(processStateAndAction.getProcessInstanceFromDb().getAuditDetails().getCreatedBy());
                auditDetails.setCreatedTime(processStateAndAction.getProcessInstanceFromDb().getAuditDetails().getCreatedTime());
            }
            processStateAndAction.getProcessInstanceFromRequest().setAuditDetails(auditDetails);
            processStateAndAction.getProcessInstanceFromRequest().setAssigner(requestInfo.getUserInfo());
            if(!CollectionUtils.isEmpty(processStateAndAction.getProcessInstanceFromRequest().getDocuments())){
                processStateAndAction.getProcessInstanceFromRequest().getDocuments().forEach(document -> {
                    document.setAuditDetails(auditDetails);
                    document.setTenantId(tenantId);
                    document.setId(UUID.randomUUID().toString());
                });
            }
            Action action = processStateAndAction.getAction();
            Boolean isStateChanging = (action.getCurrentState().equalsIgnoreCase( action.getNextState())) ? false : true;
            if(isStateChanging)
                processStateAndAction.getProcessInstanceFromRequest().setStateSla(processStateAndAction.getResultantState().getSla());
            enrichAndUpdateSlaForTransition(processStateAndAction,isStateChanging);
            setNextActions(requestInfo,processStateAndActions,true);
            
            // Set Assignees to the Process
            assigningProcessInstance(requestInfo, processStateAndAction, tenantId);
        });
        enrichUsers(requestInfo,processStateAndActions);
    }
    
    /**
	 * Assigning the valid assignee to the Process Instance.
	 * 
	 * @param requestInfo
	 * @param processStateAndAction
	 * @param tenantId
	 */
	private void assigningProcessInstance(RequestInfo requestInfo, ProcessStateAndAction processStateAndAction,
			String tenantId) {
		if (BooleanUtils.isTrue(processStateAndAction.getResultantState().getIsTerminateState())
				|| !CollectionUtils.isEmpty(processStateAndAction.getProcessInstanceFromRequest().getAssignes())) {
			return;
		} else {
			processStateAndAction.getProcessInstanceFromRequest();

			List<User> assigneeUsers = getAssignes(requestInfo, tenantId, processStateAndAction.getResultantState(),
					processStateAndAction.getProcessInstanceFromRequest(), null, true);
			processStateAndAction.getProcessInstanceFromRequest().setAssignes(assigneeUsers);
		}
	}
	/**
	 * Get the valid assignee user.
	 * 
	 * @param requestInfo
	 * @param tenantId
	 * @param resultantState
	 * @param processInstanceFromRequest
	 * @param notAllowedAssigneeUuids
	 * @return
	 */
	public List<User> getAssignes(RequestInfo requestInfo, String tenantId, State resultantState,
			ProcessInstance processInstanceFromRequest, Set<String> notAllowedAssigneeUuids,
			Boolean isCurrentAssignmentCheck) {

		String businessService = processInstanceFromRequest.getBusinessService();
		
		List<String> allTenantIds = Arrays.asList(tenantId);
		
		Map<String, User> idToUserMap = new HashMap<>();
				
		String businessId = processInstanceFromRequest.getBusinessId();
		
		String currentAssigner = processInstanceFromRequest.getAssigner().getUuid();
		
		List<User> finalAssigneeUsers = new ArrayList<>();

		if (CollectionUtils.isEmpty(notAllowedAssigneeUuids)) {
			notAllowedAssigneeUuids = new HashSet<>();
		}	
		
		Set<String> notEligibleAssigneeUUIDs = notAllowedAssigneeUuids;

		List<String> rolesInState = util.getAllRolesFromState(resultantState);
		String assigneeRole = rolesInState.get(0);
		
		//Fetching mdms details for tenantId
    	Map<String, Map<String, JSONArray>> response = fetchMdmsResponseForTenantId(requestInfo);
    	
		// fetch Parent Tenant By Application TenantId
		if (response != null) {
			String parentTenantId = fetchParentTenantByApplicationTenantId(response, tenantId);
			allTenantIds.add(parentTenantId);
		}

		for(String validTenantId : allTenantIds) {
		 idToUserMap = getAllowedAssigneeUsers(requestInfo, assigneeRole, validTenantId, businessService,
				businessId, processInstanceFromRequest.getAllowedAssignees());
		}

		logRequests(businessId, businessService, assigneeRole, idToUserMap,
				processInstanceFromRequest.getAllowedAssignees());
		
		if (idToUserMap.size() == 1) {
			User user = idToUserMap.values().stream().findAny().get();
			finalAssigneeUsers.add(user);
		} else {
			
			excludeSystemUser(idToUserMap, rolesInState);
			
			List<String> allowedUserUUIDs = idToUserMap.keySet().stream().collect(Collectors.toList());

			List<Action> validActions = getValidActionsByRole(processInstanceFromRequest.getBusinessService(), tenantId,
					assigneeRole);

			/*
			 * Checking if Assignee has already worked in same BusinessId and assigned to
			 * the same user.
			 */
			if (isCurrentAssignmentCheck) {
				checkAndDetermineFinalAssigneeUserForCurrentBussinessId(finalAssigneeUsers, tenantId, allowedUserUUIDs,
						validActions, businessId, notEligibleAssigneeUUIDs);
			}

			if (CollectionUtils.isEmpty(finalAssigneeUsers)) {
				notEligibleAssigneeUUIDs.add(currentAssigner);
				
				/*
				 * Checking if Assignee has already worked in some other Process and assigned
				 * the next user from the list in Round-Robin format.
				 */
				determineFinalAssigneeFromAllProcess(allowedUserUUIDs, idToUserMap, finalAssigneeUsers, tenantId,
						notEligibleAssigneeUUIDs, businessService, businessId, isCurrentAssignmentCheck, assigneeRole);
			}

		}

		// If still the finalAssigneeUsers is empty, then add the very first user as
		// Assignee
		if (CollectionUtils.isEmpty(finalAssigneeUsers)) {
			finalAssigneeUsers.add(
					idToUserMap.values().stream().filter(user -> !notEligibleAssigneeUUIDs.contains(user.getUuid())).findFirst().get());
		}

		return finalAssigneeUsers;
	}
	
	@SuppressWarnings("unchecked")
	private String fetchParentTenantByApplicationTenantId(Map<String, Map<String, JSONArray>> response,
			String tenantId) {

		if (response == null || tenantId == null) {
			return null;
		}

		Map<String, JSONArray> tenantModule = response.get(MDMS_MODULE_TENANT);
		if (tenantModule == null) {
			return null;
		}

		JSONArray tenants = tenantModule.get(MDMS_TENANTS);
		if (tenants == null || tenants.isEmpty()) {
			return null;
		}

		for (Object obj : tenants) {

			if (obj == null || !(obj instanceof Map)) {
				continue;
			}

			Map<String, Object> tenant = (Map<String, Object>) obj;

			Object codeObj = tenant.get(MDMS_MODULE_TENANT_CODE);
			if (codeObj == null) {
				continue;
			}

			String code = String.valueOf(codeObj);

			if (tenantId.equalsIgnoreCase(code)) {
				Object parentTenant = tenant.get(MDMS_MODULE_PARENT_TENANT_ID);
				return parentTenant != null ? String.valueOf(parentTenant) : null;
			}
		}

		return null;
	}

	private Map<String, Map<String, JSONArray>> fetchMdmsResponseForTenantId(RequestInfo requestInfo) {

		ModuleDetail tenantDetail = getTenants();
		if (tenantDetail == null) {
			return Collections.emptyMap();
		}

		List<ModuleDetail> moduleDetails = Collections.singletonList(tenantDetail);

		String tenantId = workflowConfig != null ? workflowConfig.getStateLevelTenantId() : null;

		MdmsCriteria mdmsCriteria = MdmsCriteria.builder().moduleDetails(moduleDetails).tenantId(tenantId).build();

		MdmsCriteriaReq mdmsCriteriaReq = MdmsCriteriaReq.builder().mdmsCriteria(mdmsCriteria).requestInfo(requestInfo)
				.build();

		MdmsResponse response = mdmsService != null ? mdmsService.searchMaster(mdmsCriteriaReq) : null;

		return response != null && response.getMdmsRes() != null ? response.getMdmsRes() : Collections.emptyMap();
	}

    /**
     * Creates MDMS ModuleDetail object for tenants
     * @return ModuleDetail for tenants
     */
    private ModuleDetail getTenants() {

        // master details for WF module
        List<MasterDetail> masterDetails = new ArrayList<>();

        masterDetails.add(MasterDetail.builder().name(MDMS_TENANTS).build());

        ModuleDetail wfModuleDtls = ModuleDetail.builder().masterDetails(masterDetails)
                .moduleName(MDMS_MODULE_TENANT).build();

        return wfModuleDtls;
    }
	
	/**
	 * 
	 * Determining the Assignee from All Process based on allowed users and assign
	 * user in Round-Robin format.
	 * 
	 * @param allowedUserUUIDs
	 * @param idToUserMap
	 * @param finalAssigneeUsers
	 * @param tenantId
	 * @param notEligibleAssigneeUUIDs
	 * @param businessService 
	 * @param isCurrentAssignmentCheck 
	 * @param businessId 
	 * @param assigneeRole 
	 */
	private void determineFinalAssigneeFromAllProcess(List<String> allowedUserUUIDs, Map<String, User> idToUserMap,
			List<User> finalAssigneeUsers, String tenantId, Set<String> notEligibleAssigneeUUIDs,
			String businessService, String businessId, Boolean isCurrentAssignmentCheck, String assigneeRole) {
		
		String lastAssignedUserUUID = StringUtils.EMPTY;

		ProcessInstanceSearchCriteria criteria = new ProcessInstanceSearchCriteria();
		criteria.setTenantId(tenantId);
		populateRoleStatusBasedCriteria(criteria, tenantId, assigneeRole);
		criteria.setLimit(1);

		List<ProcessInstance> allProcessInstances = worKflowRepository.getProcessInstances(criteria);
		
		// Filter out current business-Ids from allProcessInstances
		if (!isCurrentAssignmentCheck) {
			allProcessInstances = allProcessInstances.stream()
					.filter(process -> !process.getBusinessId().equals(businessId)).collect(Collectors.toList());
		}

		// Sorting all Process with LastModifiedTime & BusinessId
		Collections.sort(allProcessInstances, new Comparator<ProcessInstance>() {

			@Override
			public int compare(ProcessInstance p1, ProcessInstance p2) {
				int lastModifiedCompare = p2.getAuditDetails().getLastModifiedTime()
						.compareTo(p1.getAuditDetails().getLastModifiedTime());
				if (lastModifiedCompare != 0) {
					return lastModifiedCompare;
				}
				return p1.getBusinessId().compareTo(p2.getBusinessId());
			}
		});
		
		if (!CollectionUtils.isEmpty(allProcessInstances)) {

			populateFinalAssigneeUsersFromAllProcess(allProcessInstances, finalAssigneeUsers, notEligibleAssigneeUUIDs,
					allowedUserUUIDs);

			if (!CollectionUtils.isEmpty(finalAssigneeUsers)) {
				lastAssignedUserUUID = finalAssigneeUsers.get(0).getUuid();
				finalAssigneeUsers.clear();
			}
		}

		if (StringUtils.isNotEmpty(lastAssignedUserUUID)) {
			boolean lastAssignedUserFound = false;
			for (Entry<String, User> userMapEntry : idToUserMap.entrySet()) {

				if (lastAssignedUserFound && !notEligibleAssigneeUUIDs.contains(userMapEntry.getValue().getUuid())) {
					finalAssigneeUsers.add(userMapEntry.getValue());
					break;
				}

				if (StringUtils.equals(userMapEntry.getKey(), lastAssignedUserUUID)) {
					lastAssignedUserFound = true;
				}
			}

			/*
			 * If final Assignee not found yet, then assign the very first user from the
			 * user-list.
			 */
			if (lastAssignedUserFound && CollectionUtils.isEmpty(finalAssigneeUsers)) {

				String userUuid = idToUserMap.keySet().stream().filter(uuid -> !notEligibleAssigneeUUIDs.contains(uuid))
						.findFirst().orElse(null);

				if (StringUtils.isNotBlank(userUuid)) {
					finalAssigneeUsers.add(idToUserMap.get(userUuid));
				}
			}
		}
	}
	
	/**
	 * Finalizing the Assignee for the Process Instances from comparing all Process.
	 * 
	 * @param businessIdProcessInstances
	 * @param finalAssigneeUsers
	 * @param notEligibleAssigneeUUIDs
	 * @param allowedUserUUIDs
	 */
	private void populateFinalAssigneeUsersFromAllProcess(List<ProcessInstance> businessIdProcessInstances,
			List<User> finalAssigneeUsers, Set<String> notEligibleAssigneeUUIDs, List<String> allowedUserUUIDs) {

		for (ProcessInstance process : businessIdProcessInstances) {
			String assigneeUuid = Optional.ofNullable(process.getAssignes()).orElse(Collections.emptyList()).stream()
					.findFirst().map(User::getUuid).orElse(StringUtils.EMPTY);

			if (allowedUserUUIDs.contains(assigneeUuid) && !notEligibleAssigneeUUIDs.contains(assigneeUuid)) {
				finalAssigneeUsers.addAll(process.getAssignes());
				break;
			} else {
				notEligibleAssigneeUUIDs.add(assigneeUuid);
			}
		}
	}
	
	private void populateRoleStatusBasedCriteria(ProcessInstanceSearchCriteria criteria, String tenantId,
			String assigneeRole) {
		Map<String, Map<String, List<String>>> roleTenantAndStatusMapping = businessServiceRepository
				.getRoleTenantAndStatusMapping();

		if (!CollectionUtils.isEmpty(roleTenantAndStatusMapping)) {
			Map<String, List<String>> tenantToStatuses = roleTenantAndStatusMapping.get(assigneeRole);

			if (!CollectionUtils.isEmpty(tenantToStatuses)) {

				List<String> allStatuses = tenantToStatuses.get(tenantId);

				if (!CollectionUtils.isEmpty(allStatuses)) {
					criteria.setStatus(allStatuses);
					criteria.setStatusesIrrespectiveOfTenant(allStatuses);
				}
			}
		}
	}
	
	/**
	 * Determine the assignee if it has already worked on the same
	 * business-id(Employee Registration-Number), then assign the same user
	 * 
	 * @param finalAssigneeUsers
	 * @param tenantId
	 * @param allowedUserUUIDs
	 * @param validActions
	 * @param businessId
	 * @param notEligibleAssigneeUUIDs
	 */
	private void checkAndDetermineFinalAssigneeUserForCurrentBussinessId(List<User> finalAssigneeUsers, String tenantId,
			List<String> allowedUserUUIDs, List<Action> validActions, String businessId,
			Set<String> notEligibleAssigneeUUIDs) {

		ProcessInstanceSearchCriteria criteria = new ProcessInstanceSearchCriteria();
		criteria.setTenantId(tenantId);
		criteria.setBusinessIds(Arrays.asList(businessId));
		criteria.setHistory(true);
		criteria.setLimit(100);
		
		List<ProcessInstance> businessIdProcessInstances = worKflowRepository.getProcessInstances(criteria);

		if (!CollectionUtils.isEmpty(businessIdProcessInstances)) {

			populateFinalAssigneeUsers(businessIdProcessInstances, validActions, finalAssigneeUsers,
					notEligibleAssigneeUUIDs, allowedUserUUIDs);

		}
	}
	
	/**
	 * Finalizing the Assignee for the Process Instances.
	 * 
	 * @param businessIdProcessInstances
	 * @param validActions
	 * @param finalAssigneeUsers
	 * @param notEligibleAssigneeUUIDs
	 * @param allowedUserUUIDs
	 */
	private void populateFinalAssigneeUsers(List<ProcessInstance> businessIdProcessInstances, List<Action> validActions,
			List<User> finalAssigneeUsers, Set<String> notEligibleAssigneeUUIDs, List<String> allowedUserUUIDs) {

		int counter = 0;
		for (ProcessInstance process : businessIdProcessInstances) {
			String assigner = process.getAssigner().getUuid();
			
			String previousState = StringUtils.EMPTY;
			if (counter < businessIdProcessInstances.size() - 1) {
				previousState = businessIdProcessInstances.get(++counter).getState().getUuid();
			}

			for (Action action : validActions) {
				if (allowedUserUUIDs.contains(assigner)
						&& !notEligibleAssigneeUUIDs.contains(assigner)
						&& (previousState.equals(action.getCurrentState()) || StringUtils.isEmpty(previousState))
						&& action.getNextState().equals(process.getState().getUuid())
						&& action.getAction().equals(process.getAction())) {
					finalAssigneeUsers.add(process.getAssigner());
					break;
				}
			}
			
			if (finalAssigneeUsers.stream().allMatch(user -> !StringUtils.equals(user.getUuid(), assigner))) {
				notEligibleAssigneeUUIDs.add(assigner);
			}
			
			if (!CollectionUtils.isEmpty(finalAssigneeUsers)) {
				break;
			}
		}
	}
	
	/**
	 * Get Valid actions by the role
	 * 
	 * @param processInstanceFromRequest
	 * @param tenantId
	 * @param role
	 * @return
	 */
	private List<Action> getValidActionsByRole(String businessServiceCode, String tenantId,
			String role) {
		BusinessServiceSearchCriteria searchCriteria = BusinessServiceSearchCriteria.builder().tenantId(tenantId)
				.businessServices(Arrays.asList(businessServiceCode)).build();

		List<BusinessService> businessServices = businessServiceRepository.getBusinessServices(searchCriteria);
		enrichTenantIdForStateLevel(tenantId, businessServices);

		return businessServices.stream()
				.flatMap(businessService -> Optional.ofNullable(businessService.getStates())
						.orElse(Collections.emptyList()).stream())
				.flatMap(state -> Optional.ofNullable(state.getActions()).orElse(Collections.emptyList()).stream())
				.collect(Collectors.toList()).stream().filter(action -> action.getRoles().contains(role))
				.collect(Collectors.toList());
	}
	
	private void logRequests(String businessId, String businessService, String assigneeRole,
			Map<String, User> idToUserMap, List<User> allowedAssignees) {
		log.info("getAssignes -->> logRequests -->> START");

		log.info("businessId = [" + businessId + "]");
		log.info("businessService = [" + businessService + "]");
		log.info("assigneeRole = [" + assigneeRole + "]");
		log.info("idToUserMap user uuids = [" + Optional.ofNullable(idToUserMap).orElse(Collections.emptyMap()).keySet()
				+ "]");
		log.info("allowedAssignees user uuids = [" + Optional.ofNullable(allowedAssignees)
				.orElse(Collections.emptyList()).stream().map(User::getUuid).collect(Collectors.toSet()) + "]");

		log.info("getAssignes -->> logRequests -->> END");
	}
	
	private void excludeSystemUser(Map<String, User> idToUserMap, List<String> rolesInState) {

		if (rolesInState.stream()
				.noneMatch(roleCode -> StringUtils.equals(roleCode, INTERNALMICROSERVICEROLE_CODE))) {
			idToUserMap.entrySet().removeIf(entry -> entry.getValue().getRoles().stream()
					.anyMatch(role -> StringUtils.equals(INTERNALMICROSERVICEROLE_CODE, role.getCode())));
		}
	}
	private Map<String, User> getAllowedAssigneeUsers(RequestInfo requestInfo, String assigneeRole, String tenantId,
			String businessService, String businessId, List<User> allowedAssigneesFromRequest) {

		Map<String, User> finalUuidToUserMap = new HashMap<String, User>();

		if (!CollectionUtils.isEmpty(allowedAssigneesFromRequest)) {

			List<String> userUuids = allowedAssigneesFromRequest.stream().map(User::getUuid).collect(Collectors.toSet())
					.stream().collect(Collectors.toList());

			UserSearchRequest request = new UserSearchRequest();
//			request.setTenantId(workflowConfig.getStateLevelTenantId());
			request.setTenantId(tenantId);
			request.setRoleCodes(Arrays.asList(assigneeRole));
			request.setActive(true);
			request.setUuid(userUuids);

			Map<String, User> uuidToUserMap = userService.searchUserDetails(requestInfo, request);

			if (uuidToUserMap != null && !uuidToUserMap.isEmpty()) {
				uuidToUserMap.forEach((key, value) -> {
					finalUuidToUserMap.put(key, value);
				});
			}
		}

		if (CollectionUtils.isEmpty(finalUuidToUserMap)) {

////			if (StringUtils.equals(ENTERPRISE, assigneeRole)
//					&& StringUtils.equals(businessService, ENTERPRISE_BUSINESS_SERVICE)) {
//
//				Map<String, User> idToUser = userService.searchUserByRegistrtaionNumber(requestInfo, businessId);
//
//				idToUser.forEach((key, value) -> {
//					finalUuidToUserMap.put(key, value);
//				});
//			} else {
				UserSearchRequest request = new UserSearchRequest();
//				request.setTenantId(workflowConfig.getStateLevelTenantId());
				request.setTenantId(tenantId);
				request.setRoleCodes(Arrays.asList(assigneeRole));
				request.setActive(true);

				Map<String, User> uuidToUserMap = userService.searchUserDetails(requestInfo, request);

				if (uuidToUserMap != null && !uuidToUserMap.isEmpty()) {
					uuidToUserMap.forEach((key, value) -> {
//					if (StringUtils.equals(assigneeRole, GOA_IDC_MD_ROLE_CODE) || value.getRoles().stream()
//							.noneMatch(role -> StringUtils.equals(GOA_IDC_MD_ROLE_CODE, role.getCode()))) {
						finalUuidToUserMap.put(key, value);
//					}
					});
				}
//			}
		}

		return finalUuidToUserMap;
	}





    /**
     * Enriches the processInstanceFromRequest with next possible action depending on current currentState
     * @param requestInfo The RequestInfo of the request
     * @param processStateAndActions
     */
    private void setNextActions(RequestInfo requestInfo,List<ProcessStateAndAction> processStateAndActions,Boolean isTransition){
        List<Role> roles = requestInfo.getUserInfo().getRoles();

        processStateAndActions.forEach(processStateAndAction -> {
            State state;
            String tenantId = processStateAndAction.getProcessInstanceFromRequest().getTenantId();
            if(isTransition)
             state = processStateAndAction.getResultantState();
            else state = processStateAndAction.getCurrentState();
            List<Action> nextAction = new ArrayList<>();
            if(!CollectionUtils.isEmpty( state.getActions())){
                state.getActions().forEach(action -> {
                    if(util.isRoleAvailable(tenantId,roles,action.getRoles()) && !nextAction.contains(action))
                        nextAction.add(action);
                });
            }
            if(!CollectionUtils.isEmpty(nextAction))
                nextAction.sort(Comparator.comparing(Action::getAction));
            processStateAndAction.getProcessInstanceFromRequest().setNextActions(nextAction);
        });
    }

    /**
     * Enriches the assignee and assigner user object from user search response
     * @param requestInfo The RequestInfo of the request
     * @param processStateAndActions The List of ProcessStateAndAction containing processInstanceFromRequest to be enriched
     */
    public void enrichUsers(RequestInfo requestInfo,List<ProcessStateAndAction> processStateAndActions){
        List<String> uuids = new LinkedList<>();

        processStateAndActions.forEach(processStateAndAction -> {

            if(!CollectionUtils.isEmpty(processStateAndAction.getProcessInstanceFromRequest().getAssignes()))
                uuids.addAll(processStateAndAction.getProcessInstanceFromRequest().getAssignes().stream().map(User::getUuid).collect(Collectors.toSet()));
//          Added null check for assigner to avoid null pointer exception while fetching uuid
            if(processStateAndAction.getProcessInstanceFromRequest().getAssigner() != null)
                uuids.add(processStateAndAction.getProcessInstanceFromRequest().getAssigner().getUuid());

            if(processStateAndAction.getProcessInstanceFromDb() != null){
                if(!CollectionUtils.isEmpty(processStateAndAction.getProcessInstanceFromDb().getAssignes())){
                    uuids.addAll(processStateAndAction.getProcessInstanceFromDb().getAssignes().stream().map(User::getUuid).collect(Collectors.toSet()));
                }
            }

        });


        Map<String,User> idToUserMap = userService.searchUser(requestInfo,uuids);
        Map<String,String> errorMap = new HashMap<>();
        processStateAndActions.forEach(processStateAndAction -> {

            // Setting Assignes
            if(!CollectionUtils.isEmpty(processStateAndAction.getProcessInstanceFromRequest().getAssignes())){
                enrichAssignes(processStateAndAction.getProcessInstanceFromRequest(), idToUserMap, errorMap);
            }

            // Setting Assigner
            if(processStateAndAction.getProcessInstanceFromRequest().getAssigner()!=null)
                enrichAssigner(processStateAndAction.getProcessInstanceFromRequest(), idToUserMap, errorMap);

            // Setting Assignes for previous processInstance
            if(processStateAndAction.getProcessInstanceFromDb()!=null && !CollectionUtils.isEmpty(processStateAndAction.getProcessInstanceFromDb().getAssignes())){
                enrichAssignes(processStateAndAction.getProcessInstanceFromDb(), idToUserMap, errorMap);
            }

        });
        if(!errorMap.isEmpty())
            throw new CustomException(errorMap);
    }


    /**
     * Enriches processInstanceFromRequest from the search response
     * @param processInstances The list of processInstances from search
     */
    public void enrichUsersFromSearch(RequestInfo requestInfo,List<ProcessInstance> processInstances){
        List<String> uuids = new LinkedList<>();
        processInstances.forEach(processInstance -> {

            if(!CollectionUtils.isEmpty(processInstance.getAssignes()))
                uuids.addAll(processInstance.getAssignes().stream().map(User::getUuid).collect(Collectors.toList()));

//            Added null check for assigner to avoid null pointer exception while fetching uuid
            if(processInstance.getAssigner() != null)
                uuids.add(processInstance.getAssigner().getUuid());
        });
        Map<String,User> idToUserMap = userService.searchUser(requestInfo,uuids);
        Map<String,String> errorMap = new HashMap<>();
        processInstances.forEach(processInstance -> {

            // Enriching assignes if present
            if(!CollectionUtils.isEmpty(processInstance.getAssignes()))
                enrichAssignes(processInstance, idToUserMap, errorMap);

            // Enriching assigner if present
            if(processInstance.getAssigner()!=null)
                enrichAssigner(processInstance, idToUserMap, errorMap);

        });
        if(!errorMap.isEmpty())
            throw new CustomException(errorMap);
    }


    public List<ProcessStateAndAction> enrichNextActionForSearch(RequestInfo requestInfo,List<ProcessInstance> processInstances){
        List<ProcessStateAndAction> processStateAndActions = new LinkedList<>();
        Map<String, List<ProcessInstance>> businessServiceToProcessInstance = getRequestByBusinessService(new ProcessInstanceRequest(requestInfo,processInstances));

        for(Map.Entry<String, List<ProcessInstance>> entry : businessServiceToProcessInstance.entrySet()){
            try{
             processStateAndActions.addAll(transitionService.getProcessStateAndActions(entry.getValue(),false));}
            catch (Exception e){
                log.error("Error while creating processStateAndActions",e);
            }
        }

        setNextActions(requestInfo,processStateAndActions,false);
        return processStateAndActions;
    }


    /**
     * Enriches the incoming list of businessServices
     * @param request The BusinessService request to be enriched
     */
    public void enrichCreateBusinessService(BusinessServiceRequest request){
        RequestInfo requestInfo = request.getRequestInfo();
        List<BusinessService> businessServices = request.getBusinessServices();
        AuditDetails auditDetails = util.getAuditDetails(requestInfo.getUserInfo().getUuid(),true);
        businessServices.forEach(businessService -> {
        	
        	String tenantId = businessService.getTenantId();
            businessService.setUuid(UUID.randomUUID().toString());
            businessService.setAuditDetails(auditDetails);
            businessService.getStates().forEach(state -> {
                state.setAuditDetails(auditDetails);
                state.setUuid(UUID.randomUUID().toString());
                state.setTenantId(tenantId);
                if(!CollectionUtils.isEmpty(state.getActions()))
                    state.getActions().forEach(action -> {
                        action.setAuditDetails(auditDetails);
                        action.setUuid(UUID.randomUUID().toString());
                        action.setCurrentState(state.getUuid());
                        action.setTenantId(tenantId);
                        action.setActive(true);
                    });
            });
            enrichNextState(businessService);
        });
    }

    /**
     * Enriches update request
     * @param request The update request
     */
    public void enrichUpdateBusinessService(BusinessServiceRequest request){
        RequestInfo requestInfo = request.getRequestInfo();
        List<BusinessService> businessServices = request.getBusinessServices();
        AuditDetails audit = util.getAuditDetails(requestInfo.getUserInfo().getUuid(),true);
        /*
        * Loop over all states and if any new state is encountered enrich it
        * */

        businessServices.forEach(businessService -> {
            businessService.setAuditDetails(audit);
            businessService.getStates().forEach(state -> {
                if (state.getUuid() == null) {
                    state.setAuditDetails(audit);
                    state.setUuid(UUID.randomUUID().toString());
                    state.setTenantId(businessService.getTenantId());
                }
                else state.setAuditDetails(audit);
                });
            });

       /*
       * Extra loop is used as top loop needs to be completed first so that all new
       * states are assigned uuid which are required in the nextState map to assign
       * state uuid in the field nextState
       * */
        businessServices.forEach(businessService -> {
            businessService.getStates().forEach(state -> {
                if(!CollectionUtils.isEmpty(state.getActions()))
                    state.getActions().forEach(action -> {
                        if(action.getUuid()==null){
                            action.setAuditDetails(audit);
                            action.setUuid(UUID.randomUUID().toString());
                            action.setCurrentState(state.getUuid());
                            action.setTenantId(state.getTenantId());
                        }
                        else action.setAuditDetails(audit);
                    });
            });
            enrichNextState(businessService);
        });
    }

    /**
     * Enriches the nextState varibale in BusinessService
     * @param businessService The businessService whose action objects are to be enriched
     */
    private void enrichNextState(BusinessService businessService){
        Map<String,String> statusToUuidMap = new HashMap<>();
        businessService.getStates().forEach(state -> {
            statusToUuidMap.put(state.getState(),state.getUuid());
        });
        HashMap<String,String> errorMap = new HashMap<>();
        businessService.getStates().forEach(state -> {
            if(!CollectionUtils.isEmpty(state.getActions())){
                state.getActions().forEach(action -> {
                    if (!action.getNextState().matches(UUID_REGEX) && statusToUuidMap.containsKey(action.getNextState()))
                        action.setNextState(statusToUuidMap.get(action.getNextState()));
                    else if (!action.getNextState().matches(UUID_REGEX) && !statusToUuidMap.containsKey(action.getNextState()))
                        errorMap.put("INVALID NEXTSTATE","The state with name: "+action.getNextState()+ " does not exist in config");
                });
            }
        });
        if(!errorMap.isEmpty())
            throw new CustomException(errorMap);
    }


    /**
     * Sets the businessServiceSla when the _transition api is called
     * @param processStateAndAction The processStateAndAction object of the transition request
     */
    private void enrichAndUpdateSlaForTransition(ProcessStateAndAction processStateAndAction,Boolean isStateChanging){
        if(processStateAndAction.getProcessInstanceFromDb()!=null){
            Long businesssServiceSlaRemaining = processStateAndAction.getProcessInstanceFromDb().getBusinesssServiceSla();
            Long stateSlaRemaining = processStateAndAction.getProcessInstanceFromDb().getStateSla();
            Long timeSpent = processStateAndAction.getProcessInstanceFromRequest().getAuditDetails().getLastModifiedTime()
                           - processStateAndAction.getProcessInstanceFromDb().getAuditDetails().getLastModifiedTime();
            processStateAndAction.getProcessInstanceFromRequest().setBusinesssServiceSla(businesssServiceSlaRemaining-timeSpent);
            if(!isStateChanging && stateSlaRemaining!=null)
                processStateAndAction.getProcessInstanceFromRequest().setStateSla(stateSlaRemaining-timeSpent);
        }
    }


    /**
     * Sets the businessServiceSla for search output
     * @param processInstances The list of processInstance
     */
    public void enrichAndUpdateSlaForSearch(List<ProcessInstance> processInstances){
        processInstances.forEach(processInstance -> {
            Long businessServiceSlaInDb = processInstance.getBusinesssServiceSla();
            Long stateSlaInDB = processInstance.getStateSla();
            Long timeSinceLastAction = System.currentTimeMillis() - processInstance.getAuditDetails().getLastModifiedTime();
            processInstance.setBusinesssServiceSla(businessServiceSlaInDb-timeSinceLastAction);
            if(stateSlaInDB!=null)
                processInstance.setStateSla(stateSlaInDB-timeSinceLastAction);
        });
    }


    /**
     * Groups request by businessServices and creates a list of ProcessInstanceRequest one for each businessService
     * @param request The ProcessInstanceRequest containing processInstances across multiple BusinessServices
     * @return List of ProcessInstanceRequest
     */
    private Map<String,List<ProcessInstance>> getRequestByBusinessService(ProcessInstanceRequest request){
        List<ProcessInstance> processInstances = request.getProcessInstances();

        Map<String,List<ProcessInstance>> businessServiceToProcessInstance = new HashMap<>();
        if(!CollectionUtils.isEmpty(processInstances)){
            processInstances.forEach(processInstance -> {
                if(businessServiceToProcessInstance.containsKey(processInstance.getBusinessService()))
                    businessServiceToProcessInstance.get(processInstance.getBusinessService()).add(processInstance);
                else{
                    List<ProcessInstance> list = new ArrayList<>();
                    list.add(processInstance);
                    businessServiceToProcessInstance.put(processInstance.getBusinessService(),list);
                }
            });
        }

        return businessServiceToProcessInstance;
    }


    /**
     * Sets tenantId when stateLevel flag is on
     * @param tenantId The tenantId of the request
     * @param businessServices The businessService returned for stateLevel
     */
    public void enrichTenantIdForStateLevel(String tenantId,List<BusinessService> businessServices){
        businessServices.forEach(businessService -> {
            businessService.setTenantId(tenantId);
            businessService.getStates().forEach(state -> {
                state.setTenantId(tenantId);
                if(!CollectionUtils.isEmpty(state.getActions())){
                    state.getActions().forEach(action -> {
                        action.setTenantId(tenantId);
                    });
                }
            });
        });
    }


    /**
     * Enriches the processInstance's assignes from the search response map of uuid to User
     * @param processInstance The processInstance to be enriched
     * @param idToUserMap Search response as a map of UUID to user
     */
    private void enrichAssignes(ProcessInstance processInstance, Map<String,User> idToUserMap, Map<String , String> errorMap){
        List<User> assignes = new LinkedList<>();
        processInstance.getAssignes().forEach(assigne -> {
            if(idToUserMap.containsKey(assigne.getUuid()))
                assignes.add(idToUserMap.get(assigne.getUuid()));
            else
                errorMap.put("INVALID UUID","User not found for uuid: "+assigne.getUuid());
        });
        processInstance.setAssignes(assignes);
    }

    /**
     * Enriches the processInstance's assigner from the search response map of uuid to User
     * @param processInstance The processInstance to be enriched
     * @param idToUserMap Search response as a map of UUID to user
     */
    private void enrichAssigner(ProcessInstance processInstance, Map<String,User> idToUserMap, Map<String , String> errorMap){
        User assigner = idToUserMap.get(processInstance.getAssigner().getUuid());
        if(assigner==null)
            errorMap.put("INVALID UUID","User not found for uuid: "+processInstance.getAssigner().getUuid());
        processInstance.setAssigner(assigner);
    }


    public Set<String> enrichUuidsOfAutoEscalationEmployees(RequestInfo requestInfo, ProcessInstanceSearchCriteria criteria) {
        List<String> roleCodes = new ArrayList<>();
        Set<String> autoEscalationEmployeesUuids = new HashSet<>();
        // ######## CHANGE THE VALUE OF THE ROLE CODE CONSTANT WITH THE VALUE DEFINED IN SYSTEM
        roleCodes.add(AUTO_ESC_EMPLOYEE_ROLE_CODE);
        // ####################################################################################
        UserSearchRequest userSearchRequest = new UserSearchRequest();
        userSearchRequest.setRequestInfo(requestInfo);
        userSearchRequest.setTenantId(criteria.getTenantId());
        userSearchRequest.setRoleCodes(roleCodes);

        List<String> uuidsOfAutoEscalationEmployees = userService.searchUserUuidsBasedOnRoleCodes(userSearchRequest);
        criteria.setMultipleAssignees(uuidsOfAutoEscalationEmployees);
        uuidsOfAutoEscalationEmployees.forEach(uuid -> {
            autoEscalationEmployeesUuids.add(uuid);
        });
        return autoEscalationEmployeesUuids;
    }

    public Set<String> fetchStatesToIgnoreFromMdms(RequestInfo requestInfo, String tenantId) {
        Set<String> masterData = new HashSet<>();
        StringBuilder uri = new StringBuilder();
        uri.append(mdmsHost).append(mdmsUrl);
        if(StringUtils.isEmpty(tenantId))
            return masterData;
        MdmsCriteriaReq mdmsCriteriaReq = getMdmsRequestForStatesToIgnore(requestInfo, tenantId.split("\\.")[0]);

        try {
            //Object response = restTemplate.postForObject(uri.toString(), mdmsCriteriaReq, Map.class);
            //masterData = JsonPath.read(response, "$.MdmsRes.Workflow.AutoEscalationStatesToIgnore.*.state");
        }catch(Exception e) {
            log.error("Exception while fetching workflow states to ignore: ",e);
        }

        return masterData;
    }

    private MdmsCriteriaReq getMdmsRequestForStatesToIgnore(RequestInfo requestInfo, String tenantId) {
        MasterDetail masterDetail = new MasterDetail();
        masterDetail.setName("AutoEscalationStatesToIgnore");
        List<MasterDetail> masterDetailList = new ArrayList<>();
        masterDetailList.add(masterDetail);

        ModuleDetail moduleDetail = new ModuleDetail();
        moduleDetail.setMasterDetails(masterDetailList);
        moduleDetail.setModuleName("Workflow");
        List<ModuleDetail> moduleDetailList = new ArrayList<>();
        moduleDetailList.add(moduleDetail);

        MdmsCriteria mdmsCriteria = new MdmsCriteria();
        mdmsCriteria.setTenantId(tenantId);
        mdmsCriteria.setModuleDetails(moduleDetailList);

        MdmsCriteriaReq mdmsCriteriaReq = new MdmsCriteriaReq();
        mdmsCriteriaReq.setMdmsCriteria(mdmsCriteria);
        mdmsCriteriaReq.setRequestInfo(requestInfo);

        return mdmsCriteriaReq;
    }

    /**
     * Enriches reassignment request with assignee user details
     * Validates and enriches the new assignees from the user service
     * 
     * @param requestInfo The RequestInfo of the request
     * @param processStateAndActions List of ProcessStateAndAction containing ProcessInstance to be reassigned
     */
    public void enrichProcessRequestForReassign(RequestInfo requestInfo, List<ProcessStateAndAction> processStateAndActions) {
        processStateAndActions.forEach(processStateAndAction -> {
            ProcessInstance processInstanceFromRequest = processStateAndAction.getProcessInstanceFromRequest();
            ProcessInstance processInstanceFromDb = processStateAndAction.getProcessInstanceFromDb();
            
            if(processInstanceFromDb == null) {
                throw new CustomException("INVALID_BUSINESS_ID", 
                    "Process instance not found for businessId: " + processInstanceFromRequest.getBusinessId());
            }
            
            // Validate that assignees are provided
            if(CollectionUtils.isEmpty(processInstanceFromRequest.getAssignes())) {
                throw new CustomException("INVALID_ASSIGNEE", 
                    "At least one assignee must be provided for reassignment");
            }
        });
        
        // Enrich assignees with user details
        enrichUsers(requestInfo, processStateAndActions);
    }
}
