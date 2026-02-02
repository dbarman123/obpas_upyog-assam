package org.egov.bpa.web.model.workflow;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WfDocumentSearchCriteria {

	private List<String> uuids;
	private List<String> businessIds;
	private List<String> processInstanceIds;
	private List<String> docRefIds;
}
