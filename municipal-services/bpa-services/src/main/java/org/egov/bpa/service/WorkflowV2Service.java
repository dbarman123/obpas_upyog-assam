package org.egov.bpa.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.BooleanUtils;
import org.egov.bpa.config.BPAConfiguration;
import org.egov.bpa.util.BPAErrorConstants;
import org.egov.bpa.util.BPAUtil;
import org.egov.bpa.web.model.OC;
import org.egov.bpa.web.model.OCRequest;
import org.egov.bpa.web.model.RequestInfoWrapper;
import org.egov.bpa.web.model.ValidActionResponse;
import org.egov.bpa.web.model.ValidAssigneeActionDetails;
import org.egov.bpa.web.model.ValidAssigneeRequest;
import org.egov.bpa.web.model.ValidAssigneesResponse;
import org.egov.bpa.web.model.WorkflowRequest;
import org.egov.bpa.web.model.WorkflowResponse;
import org.egov.bpa.web.model.NOC.enums.ApplicationActions;
import org.egov.bpa.web.model.NOC.enums.ApplicationStatus;
import org.egov.bpa.web.model.NOC.enums.CurrentStates;
import org.egov.bpa.web.model.workflow.Action;
import org.egov.bpa.web.model.workflow.ProcessInstance;
import org.egov.bpa.web.model.workflow.ProcessInstanceRequest;
import org.egov.bpa.web.model.workflow.ProcessInstanceResponse;
import org.egov.bpa.web.model.workflow.WfDocument;
import org.egov.bpa.web.model.workflow.WfDocumentRequest;
import org.egov.bpa.web.model.workflow.WfDocumentResponse;
import org.egov.bpa.web.model.workflow.WfDocumentSearchCriteria;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WorkflowV2Service {

	@Autowired
	private OCServiceV2 ocServiceV2;

	@Autowired
	private BPAConfiguration applicationProperties;

	@Autowired
	private RestTemplate restTemplate;

	public ValidActionResponse getValidAction(RequestInfo requestInfo, String businessId, String tenantId) {

		ValidActionResponse response = null;

		try {

			response = getValidActions(requestInfo, businessId, tenantId);

			if (!CollectionUtils.isEmpty(response.getNextValidAction())) {
				List<Action> nextValidAction = response.getNextValidAction();
				List<Action> updatedValidAction = new ArrayList<>();

				for (Action action : nextValidAction) {
					if (CurrentStates.MB_DRAFT.getDescription().equalsIgnoreCase(action.getCurrentState())) {
						if (action.getAction().equalsIgnoreCase(ApplicationActions.FORWARD_TO_EE.getDescription())) {
							updatedValidAction.add(action);
						}
					} else if (CurrentStates.BILL_DRAFT.getDescription().equalsIgnoreCase(action.getCurrentState())) {
						if (action.getAction().equalsIgnoreCase(ApplicationActions.FORWARD_TO_EE.getDescription())) {
							updatedValidAction.add(action);
						}
					} else {
						updatedValidAction.add(action);
					}
				}
				response.setNextValidAction(updatedValidAction);
			}
		} catch (Exception e) {
			throw new CustomException(BPAErrorConstants.ERR_TECHNICAL, BPAErrorConstants.ERR_TECHNICAL_MSG);
		}
		return response;
	}

	private ValidActionResponse getValidActions(RequestInfo requestInfo, String businessId, String tenantId) {

		ValidActionResponse response;
		StringBuilder uri = new StringBuilder();
		uri.append(applicationProperties.getWfHost()).append(applicationProperties.getWorkFlowValidActionEndpoint());
		String url = UriComponentsBuilder.fromHttpUrl(uri.toString()).queryParam("businessId", businessId)
				.queryParam("tenantId", tenantId).toUriString();

		RequestInfoWrapper request = RequestInfoWrapper.builder().requestInfo(requestInfo).build();

		try {

			response = restTemplate.postForObject(url, request, ValidActionResponse.class);

		} catch (Exception e) {
			throw new CustomException(BPAErrorConstants.ERR_TECHNICAL, BPAErrorConstants.ERR_TECHNICAL_MSG);
		}
		return response;
	}

	public ValidAssigneesResponse getValidActionAssignees(@NotNull RequestInfo requestInfo, String businessId,
			String tenantId) {

		List<ValidAssigneeActionDetails> updatedActionDetails = new ArrayList<>();
		ValidAssigneesResponse response = null;
		ValidActionResponse actionResponse = null;
		Map<String, Action> validActionMap = null;

		try {

			response = fetchValidAssignees(requestInfo, businessId, tenantId);

			if (!CollectionUtils.isEmpty(response.getAssigneeActionDetails())) {

				actionResponse = getValidAction(requestInfo, businessId, tenantId);
				if (null != actionResponse) {
					if (!CollectionUtils.isEmpty(actionResponse.getNextValidAction())) {
						validActionMap = actionResponse.getNextValidAction().stream()
								.collect(Collectors.toMap(Action::getAction, action -> action));

						for (ValidAssigneeActionDetails assignee : response.getAssigneeActionDetails()) {

							if (null != validActionMap.get(assignee.getAction())) {
								updatedActionDetails.add(assignee);
							}
						}

					}
				}

				response.setAssigneeActionDetails(updatedActionDetails);

			}
		} catch (Exception e) {
			throw new CustomException(BPAErrorConstants.ERR_TECHNICAL, BPAErrorConstants.ERR_TECHNICAL_MSG);
		}

		return response;
	}

	private ValidAssigneesResponse fetchValidAssignees(@NotNull RequestInfo requestInfo, String businessId,
			String tenantId) {
		ValidAssigneesResponse response;
		StringBuilder uri = new StringBuilder();
		uri.append(applicationProperties.getWfHost()).append(applicationProperties.getWorkFlowValidAssigneeEndpoint());
		String url = UriComponentsBuilder.fromHttpUrl(uri.toString()).toUriString();

		ValidAssigneeRequest request = ValidAssigneeRequest.builder().requestInfo(requestInfo).businessId(businessId)
				.tenantId(tenantId).build();
		try {
			response = restTemplate.postForObject(url, request, ValidAssigneesResponse.class);
		} catch (Exception e) {
			throw new CustomException(BPAErrorConstants.ERR_TECHNICAL, BPAErrorConstants.ERR_TECHNICAL_MSG);
		}
		return response;
	}

	public WorkflowResponse initiateWorkflow(WorkflowRequest mbWorkflowRequest) {

		if (mbWorkflowRequest.getOCRequest() != null) {
			initiateOC(mbWorkflowRequest);
		}

		populateAdditionalDetails(mbWorkflowRequest);

		ProcessInstanceRequest processInstanceRequest = ProcessInstanceRequest.builder()
				.requestInfo(mbWorkflowRequest.getRequestInfo())
				.processInstances(mbWorkflowRequest.getProcessInstances()).build();

		ProcessInstanceResponse processInstanceResponse = transitionWorkFlow(processInstanceRequest);

//		triggerNotifications(request, processInstanceResponse.getProcessInstances());

		Optional.ofNullable(processInstanceResponse.getProcessInstances()).orElse(Collections.emptyList())
				.forEach(process -> {
					if (!CollectionUtils.isEmpty(mbWorkflowRequest.getWorkFlowDocumentIds())) {
						updateWorkflowDocument(mbWorkflowRequest.getRequestInfo(),
								mbWorkflowRequest.getWorkFlowDocumentIds(), process.getId());
					}
				});

		return WorkflowResponse.builder().processInstances(processInstanceResponse.getProcessInstances()).build();

	}

	private void updateWorkflowDocument(RequestInfo requestInfo, List<String> workFlowDocumentIds,
			final String processInstanceId) {
		WfDocumentResponse wfDocumentResponse = searchWfDocuments(requestInfo,
				WfDocumentSearchCriteria.builder().uuids(workFlowDocumentIds).build());

		List<WfDocument> existingWfDocuments = wfDocumentResponse.getWfDocuments();

		if (!CollectionUtils.isEmpty(existingWfDocuments)) {

			existingWfDocuments.forEach(wfDocument -> {
				wfDocument.setProcessInstanceId(processInstanceId);

				updateWorkflowDocumentService(
						WfDocumentRequest.builder().requestInfo(requestInfo).wfDocument(wfDocument).build());
			});
		}
	}

	private WfDocument updateWorkflowDocumentService(WfDocumentRequest wfDocumentRequest) {
		StringBuilder uriBuilder = new StringBuilder();
		uriBuilder.append(applicationProperties.getWfHost())
				.append(applicationProperties.getWorkflowUpdateDocumentEndpoint());
		try {
			WfDocument response = restTemplate.postForObject(uriBuilder.toString(), wfDocumentRequest,
					WfDocument.class);

			return response;
		} catch (Exception e) {
			log.error("Error occurred while calling updateWorkflowDocumentService endpoint.", e);
			throw new CustomException(BPAErrorConstants.ERR_TECHNICAL,
					"Error occurred while calling updateWorkflowDocumentService endpoint. Message: " + e.getMessage());
		}
	}

	private ProcessInstanceResponse transitionWorkFlow(ProcessInstanceRequest request) {

		StringBuilder url = new StringBuilder(applicationProperties.getWfHost());
		url.append(applicationProperties.getWfTransitionPath());

		try {
			ResponseEntity<ProcessInstanceResponse> resource = restTemplate.exchange(url.toString(), HttpMethod.POST,
					new HttpEntity<ProcessInstanceRequest>(request), ProcessInstanceResponse.class);
			return resource.getBody();
		} catch (Exception e) {
			log.error("Error calling workflow transition Service.", e);
			throw new CustomException(BPAErrorConstants.ERR_WORKFLOW_SERVICE,
					"Error calling workflow transition Service. Message: " + e.getMessage());
		}

	}

	private void populateAdditionalDetails(WorkflowRequest mbWorkflowRequest) {
		Object additionalDetails = null;

		if (mbWorkflowRequest.getOCRequest() != null) {
			additionalDetails = BPAUtil.mapUuidToAdditionalDetails(mbWorkflowRequest.getOCRequest().getId());

			for (ProcessInstance processInstance : mbWorkflowRequest.getProcessInstances()) {
				processInstance.setAdditionalDetails(additionalDetails);
			}
		}
	}

	private void initiateOC(WorkflowRequest mbWorkflowRequest) {
		OC ocDetailRequest = mbWorkflowRequest.getOCRequest();
		OC ocResponse = null;

		String action = mbWorkflowRequest.getProcessInstances().get(0).getAction();

		if (BooleanUtils.isTrue(mbWorkflowRequest.getIsUpdatable())
				|| BooleanUtils.isTrue(ocDetailRequest.getIsFinalSubmit())) {
			if (BooleanUtils.isTrue(ocDetailRequest.getIsFinalSubmit())) {
				ocDetailRequest.setStatus(ApplicationStatus.PENDING.name());
				mbWorkflowRequest.setOCRequest(ocDetailRequest);
			}

			ocResponse = saveOCWfRequest(mbWorkflowRequest, action);

			if (null == ocResponse) {
				throw new CustomException(BPAErrorConstants.ERR_TECHNICAL, "error in updating MB request");
			}
			mbWorkflowRequest.setOCRequest(ocResponse);
		}

		if (ApplicationActions.APPROVE.name().equalsIgnoreCase(action)) {
			ocDetailRequest.setStatus(ApplicationStatus.APPROVED.name());
//			ocDetailRequest.setApprovalDate(Timestamp.valueOf(LocalDateTime.now()));
			mbWorkflowRequest.setOCRequest(ocDetailRequest);
			saveOCWfRequest(mbWorkflowRequest, action);
		}

		if (ApplicationActions.REJECT.name().equalsIgnoreCase(action)) {
			ocDetailRequest.setStatus(ApplicationStatus.REJECTED.name());
			mbWorkflowRequest.setOCRequest(ocDetailRequest);
			saveOCWfRequest(mbWorkflowRequest, action);
		}
	}

	private OC saveOCWfRequest(WorkflowRequest request, String action) {
		OC approvedLatestModel = null;

		OCRequest mbRequest = OCRequest.builder().requestInfo(request.getRequestInfo()).oc(request.getOCRequest())
				.build();

		return ocServiceV2.createOC(mbRequest);
	}

	public WfDocumentResponse searchWfDocuments(@NotNull RequestInfo requestInfo,
			WfDocumentSearchCriteria searchCriteria) {

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(applicationProperties.getWfHost())
				.path(applicationProperties.getWorkflowDocumentSearchEndpoint());

		if (CollectionUtils.isEmpty(searchCriteria.getUuids())) {
			uriBuilder.queryParam("uuids", searchCriteria.getUuids());
		}

		if (CollectionUtils.isEmpty(searchCriteria.getBusinessIds())) {
			uriBuilder.queryParam("businessIds", searchCriteria.getBusinessIds());
		}

		if (CollectionUtils.isEmpty(searchCriteria.getProcessInstanceIds())) {
			uriBuilder.queryParam("processInstanceIds", searchCriteria.getProcessInstanceIds());
		}

		if (CollectionUtils.isEmpty(searchCriteria.getDocRefIds())) {
			uriBuilder.queryParam("docRefIds", searchCriteria.getDocRefIds());
		}

		WfDocumentResponse documentResponse = restTemplate.postForObject(uriBuilder.toUriString(),
				RequestInfoWrapper.builder().requestInfo(requestInfo).build(), WfDocumentResponse.class);

		if (documentResponse == null || documentResponse.getWfDocuments() == null) {
			throw new CustomException(BPAErrorConstants.ERR_TECHNICAL, "Invalid response format from external API");
		}

		return documentResponse;
	}

}
