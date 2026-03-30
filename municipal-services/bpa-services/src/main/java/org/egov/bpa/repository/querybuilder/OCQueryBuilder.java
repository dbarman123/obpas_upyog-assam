package org.egov.bpa.repository.querybuilder;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class OCQueryBuilder {
	
	public String buildUgOcDetailsQuery(
	        String tenantId,
	        String id,
	        String applicationNo,
	        String nocNo,
	        String phoneNumber,
	        String email) {

	    StringBuilder query = new StringBuilder(
	            "SELECT * FROM public.ug_oc_details WHERE 1=1 ");

	    if (tenantId != null && !tenantId.isEmpty()) {
	        query.append(" AND tenant_id = ? ");
	    }
	    if (id != null && !id.isEmpty()) {
	    	query.append(" AND id = ? ");
	    }
	    if (applicationNo != null && !applicationNo.isEmpty()) {
	        query.append(" AND application_no = ? ");
	    }
	    if (nocNo != null && !nocNo.isEmpty()) {
	        query.append(" AND noc_no = ? ");
	    }
	    if (phoneNumber != null && !phoneNumber.isEmpty()) {
	        query.append(" AND phone_number = ? ");
	    }
	    if (email != null && !email.isEmpty()) {
	    	query.append(" AND phone_number = ? ");
	    }

	    return query.toString();
	}

}
