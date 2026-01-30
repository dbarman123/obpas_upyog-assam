package org.egov.bpa.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.egov.bpa.web.model.AuditDetails;
import org.egov.bpa.web.model.OC;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OCDetailsRowMapper implements RowMapper<OC> {

	@Override
	public OC mapRow(ResultSet rs, int rowNum) throws SQLException {
		ObjectMapper mapper = new ObjectMapper();

		AuditDetails auditDetails = AuditDetails.builder()
				.createdBy(rs.getString("created_by"))
				.lastModifiedBy(rs.getString("last_modified_by"))
				.createdTime(rs.getLong("created_time"))
				.lastModifiedTime(rs.getLong("last_modified_time"))
				.build();

		JsonNode additionalDetails = null;
		Object additionalDetailsObj = rs.getObject("additional_details");
		if (additionalDetailsObj != null) {
			try {
				additionalDetails = mapper.readTree(additionalDetailsObj.toString());
			} catch (JsonProcessingException e) {
				log.error("Json processing exception occured...", e.getMessage());
			}
		}

		return OC.builder()
				.id(rs.getString("id"))
				.tenantId(rs.getString("tenant_id"))
				.applicationNo(rs.getString("application_no"))
				.applicationDate(rs.getLong("application_date"))
				.landId(rs.getString("land_id"))
				.bpaApplicationNo(rs.getString("bpa_application_no"))
				.nameOfMasterPlan(rs.getString("name_of_master_plan"))
				.nameOfUlbPanchayat(rs.getString("name_of_ulb_panchayat"))
				.nameOfApplicant(rs.getString("name_of_applicant"))	
				.status(rs.getString("status"))
				.approvalNo(rs.getString("approval_no"))
				.approvalDate(rs.getLong("approval_date"))
				.occupancyCertificateNo(rs.getString("occupancy_certificate_no"))
				.nocNo(rs.getString("noc_no"))
				.nocDate(rs.getString("noc_date")) //need to verify whether dataType long/varchar
				.proposedUseOfBuilding(rs.getString("proposed_use_of_building"))
				.noOfFloors(rs.getString("no_of_floors"))
				.isPaymentDone(rs.getBoolean("is_payment_done"))
				.additionalDetails(additionalDetails)
				.ocFileStoreId(rs.getString("oc_file_store_id"))
				.businessService(rs.getString("business_service"))
				.auditDetails(auditDetails)
				.signedOcFileStoreId(rs.getString("signed_oc_filestore_id"))
				.isPanalityApplicable(rs.getBoolean("is_panality_applicable"))
				.phoneNumber(rs.getString("phone_number"))
				.email(rs.getString("email"))
				.build();
	}

}
