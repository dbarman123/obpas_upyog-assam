package org.egov.bpa.web.controller;

import javax.validation.Valid;

import org.egov.bpa.service.WorkflowV2Service;
import org.egov.bpa.web.model.RequestInfoWrapper;
import org.egov.bpa.web.model.ValidActionResponse;
import org.egov.bpa.web.model.ValidAssigneeRequest;
import org.egov.bpa.web.model.ValidAssigneesResponse;
import org.egov.bpa.web.model.WorkflowRequest;
import org.egov.bpa.web.model.WorkflowResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/workFlow")
public class WorkFlowController {

	@Autowired
	private WorkflowV2Service workflowService;

	@PostMapping("/_action")
	public ResponseEntity<ValidActionResponse> fetchValidAction(
			@Valid @RequestBody RequestInfoWrapper requestInfoWrapper, @RequestParam String businessId,
			@RequestParam String tenantId) {
		ValidActionResponse actionResponse = workflowService.getValidAction(requestInfoWrapper.getRequestInfo(),
				businessId, tenantId);
		return new ResponseEntity<>(actionResponse, HttpStatus.OK);
	}

	@PostMapping("/valid-assignees/_search")
	public ResponseEntity<ValidAssigneesResponse> getValidActionAssignees(
			@Valid @RequestBody ValidAssigneeRequest request) {
		ValidAssigneesResponse response = workflowService.getValidActionAssignees(request.getRequestInfo(),
				request.getBusinessId(), request.getTenantId());
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/_initiate")
	public ResponseEntity<?> initiateWorkflow(@RequestBody @Valid WorkflowRequest request) {

		WorkflowResponse response = workflowService.initiateWorkflow(request);

		return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
	}

}
