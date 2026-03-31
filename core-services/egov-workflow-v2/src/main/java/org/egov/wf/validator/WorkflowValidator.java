package org.egov.wf.validator;

import static org.egov.wf.util.WorkflowConstants.CITIZEN_TYPE;
import static org.egov.wf.util.WorkflowConstants.MDMS_MODULE_TENANT;
import static org.egov.wf.util.WorkflowConstants.MDMS_MODULE_PARENT_TENANT_ID;
import static org.egov.wf.util.WorkflowConstants.MDMS_MODULE_PARENT_TENANT_CODE;
import static org.egov.wf.util.WorkflowConstants.MDMS_TENANTS;
import static org.egov.wf.util.WorkflowConstants.RATE_ACTION;
import static org.egov.wf.util.WorkflowConstants.SENDBACKTOCITIZEN;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import org.egov.wf.service.MDMSService;
import org.egov.wf.util.BusinessUtil;
import org.egov.wf.util.WorkflowUtil;
import org.egov.wf.web.models.Action;
import org.egov.wf.web.models.BusinessService;
import org.egov.wf.web.models.ProcessInstance;
import org.egov.wf.web.models.ProcessStateAndAction;
import org.egov.wf.web.models.State;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import net.minidev.json.JSONArray;


@Component
public class WorkflowValidator {


    private WorkflowUtil util;

    private BusinessUtil businessUtil;
    
    @Autowired
    private MDMSService mdmsService;
    
    @Autowired
    private WorkflowConfig workflowConfig;


    @Autowired
    public WorkflowValidator(WorkflowUtil util, BusinessUtil businessUtil) {
        this.util = util;
        this.businessUtil = businessUtil;
    }



    /**
     * Validates the request
     * @param requestInfo RequestInfo of the request
     * @param processStateAndActions The processStateAndActions containing processInstances to be validated
     */
    public void validateRequest(RequestInfo requestInfo, List<ProcessStateAndAction> processStateAndActions){
        String tenantId = processStateAndActions.get(0).getProcessInstanceFromRequest().getTenantId();
        String businessServiceCode = processStateAndActions.get(0).getProcessInstanceFromRequest().getBusinessService();
        BusinessService businessService = businessUtil.getBusinessService(tenantId,businessServiceCode);
        validateAction(requestInfo,processStateAndActions,businessService);
        validateDocuments(processStateAndActions);
        validateAssignes(requestInfo, processStateAndActions);
    }


    /**
     * Validates if the search functionality is available for the role of the user
     * @param requestInfo The RequestInfo of the search request
     * @param processStateAndActions The ProcessStateAndAction object of the search result
     */
/*    public void validateSearch(RequestInfo requestInfo, List<ProcessStateAndAction> processStateAndActions){
        Map<String,String> errorMap = new HashMap<>();
        Set<String> businessIds = util.getBusinessIds(processStateAndActions);
        businessIds.forEach(businessId -> {
            ProcessStateAndAction processStateAndAction = util.getLatestProcessStateAndAction(businessId,processStateAndActions);
            List<String> rolesInState = util.getAllRolesFromState(processStateAndAction.getCurrentState());
            Boolean isAssignedToMe = false;
            if(processStateAndAction.getProcessInstanceFromRequest().getAssignee()!=null)
                isAssignedToMe = (processStateAndAction.getProcessInstanceFromRequest().getAssignee().getUuid().equalsIgnoreCase(requestInfo.getUserInfo().getUuid())) ? true : false;
            if(!util.isRoleAvailable(requestInfo.getUserInfo().getRoles(),rolesInState) && !isAssignedToMe)
                errorMap.put("INVALID SEARCH","Access denied for processInstance: "+processStateAndAction.getProcessInstanceFromRequest().getId());
        });
        if(!errorMap.isEmpty())
            throw new CustomException(errorMap);
    }*/



    /**
     * Validates if documents are required to perform currentState change
     * @param processStateAndActions ProcessStateAndAction to be validated
     */
    private void validateDocuments(List<ProcessStateAndAction> processStateAndActions){
        Map<String,String> errorMap = new HashMap<>();
        for (ProcessStateAndAction processStateAndAction : processStateAndActions){
            if(processStateAndAction.getResultantState().getDocUploadRequired()){
                if(CollectionUtils.isEmpty(processStateAndAction.getProcessInstanceFromRequest().getDocuments()))
                    errorMap.put("INVALID DOCUMENT","Documents cannot be null for status: "+processStateAndAction.getResultantState().getState());
            }
        }
        if(!errorMap.isEmpty())
            throw new CustomException(errorMap);
    }


    /**
     * Validates if the action can be performed
     * @param requestInfo The RequestInfo of the incoming request
     * @param processStateAndActions The processStateAndActions containing processInstances to be validated
     */
    private void validateAction(RequestInfo requestInfo,List<ProcessStateAndAction> processStateAndActions
            ,BusinessService businessService){
    	String parentTenantId = null;
    	
    	//Fetching mdms details for tenantId
    	Map<String, Map<String, JSONArray>> response = fetchMdmsResponseForTenantId(requestInfo);

        Map<String,List<String>> tenantIdToRoles = util.getTenantIdToUserRolesMap(requestInfo);

        for(ProcessStateAndAction processStateAndAction : processStateAndActions){
            String tenantId= processStateAndAction.getProcessInstanceFromRequest().getTenantId();
            List<String> roles = new LinkedList<>();
            
            //fetch Parent Tenant By Application TenantId
            if(response != null) {
            	 parentTenantId = fetchParentTenantByApplicationTenantId(response,tenantId);
            }

            // Adding tenant level roles
            if(!CollectionUtils.isEmpty(tenantIdToRoles.get(tenantId))) {
                roles.addAll(tenantIdToRoles.get(tenantId));
            }else if(!CollectionUtils.isEmpty(tenantIdToRoles.get(parentTenantId))) {
            	roles.addAll(tenantIdToRoles.get(parentTenantId));
            }

            // Adding the state level roles
            if(!CollectionUtils.isEmpty(tenantIdToRoles.get(tenantId.split("\\.")[0]))){
                String stateLevelTenant = tenantId.split("\\.")[0];
                List<String> stateLevelRoles = tenantIdToRoles.get(stateLevelTenant);
                roles.addAll(stateLevelRoles);
            }

            Action action = processStateAndAction.getAction();
            if(action==null && !processStateAndAction.getCurrentState().getIsTerminateState())
                throw new CustomException("INVALID ACTION","Action not found for businessIds: "+
                        processStateAndAction.getCurrentState().getBusinessServiceId());

            Integer rating = null;

            if(!ObjectUtils.isEmpty(processStateAndAction.getProcessInstanceFromRequest()))
                rating = processStateAndAction.getProcessInstanceFromRequest().getRating();

            if(rating != null && !action.getAction().equalsIgnoreCase(RATE_ACTION)){
                throw new CustomException("INVALID_ACTION", "Rating can be given only upon taking RATE action.");
            }

            Boolean isRoleAvailable = util.isRoleAvailable(roles,action.getRoles());
            Boolean isStateChanging = (action.getCurrentState().equalsIgnoreCase( action.getNextState())) ? false : true;
            List<String> transitionRoles = getRolesFromState(processStateAndAction.getCurrentState());
            Boolean isRoleAvailableForTransition = util.isRoleAvailable(roles,transitionRoles);
            Boolean isAssigneeUserInfo = false;

            /*Checks if the user has role to take action*/
            if(action!=null && isStateChanging && !isRoleAvailable)
                throw new CustomException("INVALID ROLE","User is not authorized to perform action");
            if(action!=null && !isStateChanging && !util.isRoleAvailable(roles,util.rolesAllowedInService(businessService)))
                throw new CustomException("INVALID ROLE","User is not authorized to perform action");




            /*
             * Checks in case of non-transition action the assigner is one having transition role in current state
             * or is the one to whom it was assigned
             * */
            if(processStateAndAction.getProcessInstanceFromDb()!=null && !CollectionUtils.isEmpty(processStateAndAction.getProcessInstanceFromDb().getAssignes())){
                isAssigneeUserInfo = processStateAndAction.getProcessInstanceFromDb().getAssignes().stream().map(User::getUuid).collect(Collectors.toList())
                        .contains(requestInfo.getUserInfo().getUuid());
            }



            /**
             * Checks if in case of action causing transition the assignee has role that can take some action
             * in the resultant state
             */
            List<String> nextStateRoles = getRolesFromState(processStateAndAction.getResultantState());

            if(isStateChanging && !CollectionUtils.isEmpty(processStateAndAction.getProcessInstanceFromRequest().getAssignes())){
                processStateAndAction.getProcessInstanceFromRequest().getAssignes().forEach(assignee -> {
                    List<Role> assigneeRoles = assignee.getRoles();
                    Boolean isRoleAvailableInNextState = util.isRoleAvailable(tenantId,assigneeRoles,nextStateRoles);
                    if(!isRoleAvailableInNextState)
                        throw new CustomException("INVALID_ASSIGNEE","Cannot assign to the user: "+ assignee.getUuid());

                });
            }

            /*
            *  Validates if the application is sendback to citizen, only the citizen to whom the
            *  application is sent back is able to take the action
            * */
            if(requestInfo.getUserInfo().getType().equalsIgnoreCase(CITIZEN_TYPE)){
                ProcessInstance processInstanceFromDB = processStateAndAction.getProcessInstanceFromDb();
                if(processInstanceFromDB!=null && processInstanceFromDB.getAction().equalsIgnoreCase(SENDBACKTOCITIZEN)){
                    List<String> assignes = processInstanceFromDB.getAssignes().stream().map(User::getUuid).collect(Collectors.toList());
                    if(!assignes.contains(requestInfo.getUserInfo().getUuid()))
                        throw new CustomException("INVALID_USER","The user: "+requestInfo.getUserInfo().getUuid()+" is not authorized to take action");
                }
            }


        }
    }
    
	private String fetchParentTenantByApplicationTenantId(Map<String, Map<String, JSONArray>> response,
			String tenantId) {

		if (response == null || response.get(MDMS_MODULE_TENANT) == null
				|| response.get(MDMS_MODULE_TENANT).get(MDMS_TENANTS) == null) {
			return null;
		}

		JSONArray tenants = response.get(MDMS_MODULE_TENANT).get(MDMS_TENANTS);

		for (int i = 0; i < tenants.size(); i++) {
			JSONObject tenant = (JSONObject) tenants.get(i);

			if (tenantId.equalsIgnoreCase((String) tenant.get(MDMS_MODULE_PARENT_TENANT_CODE))) {
				return (String) tenant.get(MDMS_MODULE_PARENT_TENANT_ID); 
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

	private List<String> getRolesFromState(State state){
        List<String> transitionRoles = new LinkedList<>();
        if(!CollectionUtils.isEmpty(state.getActions())){
            state.getActions().forEach(action -> {
                if(!action.getCurrentState().equalsIgnoreCase(action.getNextState()))
                    transitionRoles.addAll(action.getRoles());
            });
        }
        return transitionRoles;
    }

    /**
     * Validates the reassignment request
     * Ensures that:
     * 1. Module name is 'bpa-services' (reassign is only allowed for bpa-services module)
     * 2. The user has permission to reassign (has role to take action on current state)
     * 3. The new assignees have roles allowed for the current state
     * 
     * @param requestInfo The RequestInfo of the incoming request
     * @param processStateAndActions The processStateAndActions containing processInstances to be validated
     */
    public void validateReassignRequest(RequestInfo requestInfo, List<ProcessStateAndAction> processStateAndActions) {
        String tenantId = processStateAndActions.get(0).getProcessInstanceFromRequest().getTenantId();
        String businessServiceCode = processStateAndActions.get(0).getProcessInstanceFromRequest().getBusinessService();
        BusinessService businessService = businessUtil.getBusinessService(tenantId,businessServiceCode);
        
        Map<String,List<String>> tenantIdToRoles = util.getTenantIdToUserRolesMap(requestInfo);
        Map<String,String> errorMap = new HashMap<>();
        
        for(ProcessStateAndAction processStateAndAction : processStateAndActions) {
            ProcessInstance processInstanceFromDb = processStateAndAction.getProcessInstanceFromDb();
            ProcessInstance processInstanceFromRequest = processStateAndAction.getProcessInstanceFromRequest();
            
            // Validate that process instance exists in DB
            if(processInstanceFromDb == null) {
                errorMap.put("INVALID_BUSINESS_ID", 
                    "Process instance not found in database for businessId: " 
                    + processInstanceFromRequest.getBusinessId());
                continue;
            }
            
            // Validate module name - reassign is only allowed for bpa-services module
            String moduleName = processInstanceFromDb.getModuleName();
            if(moduleName == null || !moduleName.equals("bpa-services")) {
                errorMap.put("INVALID_MODULE_NAME", 
                    "Reassign is only allowed for 'bpa-services' module. Found module: " + moduleName 
                    + " for businessId: " + processInstanceFromDb.getBusinessId());
            }
            String currentTenantId = processStateAndAction.getProcessInstanceFromRequest().getTenantId();
            List<String> roles = new LinkedList<>();
            
            // Adding tenant level roles
            if(!CollectionUtils.isEmpty(tenantIdToRoles.get(currentTenantId)))
                roles.addAll(tenantIdToRoles.get(currentTenantId));
            
            // Adding the state level roles
            if(!CollectionUtils.isEmpty(tenantIdToRoles.get(currentTenantId.split("\\.")[0]))) {
                String stateLevelTenant = currentTenantId.split("\\.")[0];
                List<String> stateLevelRoles = tenantIdToRoles.get(stateLevelTenant);
                roles.addAll(stateLevelRoles);
            }
            
            State currentState = processStateAndAction.getCurrentState();
            if(currentState == null) {
                errorMap.put("INVALID_STATE", "Process instance does not have a valid state for businessId: " 
                    + processStateAndAction.getProcessInstanceFromRequest().getBusinessId());
                continue;
            }
            
            // Check if user has role to reassign (has role to take action on current state)
            List<String> transitionRoles = getRolesFromState(currentState);
            Boolean isRoleAvailableForReassign = util.isRoleAvailable(roles, transitionRoles) 
                    || util.isRoleAvailable(roles, util.rolesAllowedInService(businessService));
            
            if(!isRoleAvailableForReassign) {
                errorMap.put("INVALID_ROLE", "User is not authorized to reassign application with businessId: " 
                    + processStateAndAction.getProcessInstanceFromRequest().getBusinessId());
            }
            
            // Validate that new assignees have roles allowed for current state
            if(!CollectionUtils.isEmpty(processStateAndAction.getProcessInstanceFromRequest().getAssignes())) {
                List<String> currentStateRoles = getRolesFromState(currentState);
                
                processStateAndAction.getProcessInstanceFromRequest().getAssignes().forEach(assignee -> {
                    List<Role> assigneeRoles = assignee.getRoles();
                    Boolean isRoleAvailableInCurrentState = util.isRoleAvailable(currentTenantId, assigneeRoles, currentStateRoles);
                    
                    if(!isRoleAvailableInCurrentState) {
                        errorMap.put("INVALID_ASSIGNEE", "Cannot assign to user: " + assignee.getUuid() 
                            + " - user does not have required roles for current state");
                    }
                });
            }
        }
        
        if(!errorMap.isEmpty())
            throw new CustomException(errorMap);
    }

    /**
     * Validates if the citizen is in list of assignes
     * @param requestInfo
     * @param processStateAndActions
     */
    private void validateAssignes(RequestInfo requestInfo, List<ProcessStateAndAction> processStateAndActions){

        if(requestInfo.getUserInfo().getType().equalsIgnoreCase(CITIZEN_TYPE)){

            String userUUID = requestInfo.getUserInfo().getUuid();
            Map<String, String> errorMap = new HashMap<>();

            for(ProcessStateAndAction processStateAndAction : processStateAndActions){

                ProcessInstance processInstanceFromDb = processStateAndAction.getProcessInstanceFromDb();

                if(processInstanceFromDb!=null){
                    if(!CollectionUtils.isEmpty(processInstanceFromDb.getAssignes())){

                        List<String> assignes = new LinkedList<>();

                        for(User assignee : processInstanceFromDb.getAssignes()){

                            if(assignee.getType().equalsIgnoreCase(CITIZEN_TYPE))
                                assignes.add(assignee.getUuid());

                        }

                        if(!CollectionUtils.isEmpty(assignes) && !assignes.contains(userUUID))
                            errorMap.put("INVALID_USER","Citizen not authorized to perform action on application: "+processInstanceFromDb.getBusinessId());
                    }
                }

            }

            if(!errorMap.isEmpty())
                throw new CustomException(errorMap);

        }

    }





}
