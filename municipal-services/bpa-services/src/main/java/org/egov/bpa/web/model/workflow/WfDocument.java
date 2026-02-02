package org.egov.bpa.web.model.workflow;

import org.egov.bpa.web.model.NOC.AuditDetails;
import org.egov.bpa.web.model.NOC.Document;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WfDocument {

	private String uuid;
	private String businessId;
	private String processInstanceId;
	private String docRefId;
	private String uploadedUserRole;
	private Document document;
	private ProcessInstance processInstance;
	private AuditDetails auditDetails;
}
