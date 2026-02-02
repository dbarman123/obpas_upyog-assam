package org.egov.bpa.web.model;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.egov.bpa.web.model.workflow.ProcessInstance;
import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorkflowRequest {
	
	@NotNull
	@JsonProperty(value = "RequestInfo")
	private RequestInfo requestInfo;

	@JsonProperty(value = "processInstances")
	@NotNull
	private List<ProcessInstance> processInstances;

	private List<String> workFlowDocumentIds;
	
	@JsonProperty("oCRequest")
	private OC oCRequest;

	@Builder.Default
	private Boolean isUpdatable = false;
	@Builder.Default
	private Boolean isFinalSubmit = false;

}
