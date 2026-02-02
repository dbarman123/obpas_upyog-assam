package org.egov.bpa.web.model;

import java.util.List;

import org.egov.bpa.web.model.workflow.ProcessInstance;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkflowResponse {

	@JsonProperty("processInstances")
	private List<ProcessInstance> processInstances;

}
