
package org.egov.bpa.web.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

	private String id;

	private String mobileNumber;

	private String applicationNo;

	private String bpaApplicationNo;

	private String name;
	
	private String occupancyCertificateNo;

	@JsonIgnore
	private List<String> landId;
}
