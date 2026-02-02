package org.egov.bpa.web.model;

import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ValidAssigneeActionDetails {

	private String action;

	private Action actionDetails;

	@Builder.Default
	private Set<ValidAssigneeUserDetails> assigneeUserDetailsList = new HashSet<>();

}
