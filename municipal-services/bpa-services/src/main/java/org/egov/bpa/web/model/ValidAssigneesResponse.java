package org.egov.bpa.web.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ValidAssigneesResponse {

	@Builder.Default
	private List<ValidAssigneeActionDetails> assigneeActionDetails = new ArrayList<>();
	
	@JsonProperty("isUpdatable")
	@Builder.Default
	private Boolean isUpdatable = false;

	@JsonProperty("businessService")
	private String businessService;

	@JsonProperty("moduleName")
	private String moduleName;

}