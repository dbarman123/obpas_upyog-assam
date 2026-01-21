
package org.egov.bpa.web.model;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OCSearchCriteria {

	private String tenantId;

	private List<String> ids;

	private String phoneNumber;

	private String nocNo;

	private String email;
	
	private String applicationNo;

}
