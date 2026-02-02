package org.egov.bpa.web.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(of = "uuid")
public class ValidAssigneeUserDetails {

	private String uuid;

	private String assigneeDesc;

}
