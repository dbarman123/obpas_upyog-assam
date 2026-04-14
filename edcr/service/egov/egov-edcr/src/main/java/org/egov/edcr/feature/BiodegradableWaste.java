/*
 * UPYOG  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 * accountability and the service delivery of the government  organizations.
 *
 *  Copyright (C) <2019>  eGovernments Foundation
 *
 *  The updated version of eGov suite of products as by eGovernments Foundation
 *  is available at http://www.egovernments.org
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see http://www.gnu.org/licenses/ or
 *  http://www.gnu.org/licenses/gpl.html .
 *
 *  In addition to the terms of the GPL license to be adhered to in using this
 *  program, the following additional terms are to be complied with:
 *
 *      1) All versions of this program, verbatim or modified must carry this
 *         Legal Notice.
 *      Further, all user interfaces, including but not limited to citizen facing interfaces,
 *         Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *         derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *      For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *      For any further queries on attribution, including queries on brand guidelines,
 *         please contact contact@egovernments.org
 *
 *      2) Any misrepresentation of the origin of the material is prohibited. It
 *         is required that all modified versions of this material be marked in
 *         reasonable ways as different from the original version.
 *
 *      3) This license does not grant any rights to any user of the program
 *         with regards to rights under trademark law for use of the trade names
 *         or trademarks of eGovernments Foundation.
 *
 *  In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.edcr.feature;

import static org.egov.edcr.constants.DxfFileConstants.A;
import static org.egov.edcr.constants.DxfFileConstants.A_AF;
import static org.egov.edcr.constants.DxfFileConstants.C;
import static org.egov.edcr.constants.DxfFileConstants.F;
import static org.egov.edcr.constants.DxfFileConstants.G;
import static org.egov.edcr.constants.EdcrReportConstants.MSG_COMMERCIAL;
import static org.egov.edcr.constants.EdcrReportConstants.MSG_GROUP_HOUSING;
import static org.egov.edcr.constants.EdcrReportConstants.MSG_HOSPITAL;
import static org.egov.edcr.constants.EdcrReportConstants.MSG_PLOT_AREA;
import static org.egov.edcr.constants.EdcrReportConstants.RULE_45_E;
import static org.egov.edcr.constants.EdcrReportConstants.VALIDATION_MANDATORY_STP_MISSING;
import static org.egov.edcr.service.FeatureUtil.addScrutinyDetailtoPlan;
import static org.egov.edcr.service.FeatureUtil.mapReportDetails;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.entity.edcr.BiodegradableWasteTreatment;
import org.egov.common.entity.edcr.OccupancyTypeHelper;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.ReportScrutinyDetail;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.springframework.stereotype.Service;

@Service
public class BiodegradableWaste extends FeatureProcess {
    private static final String SUBRULE_54_4_DESC = "Biodegradable Waste Treatment";
    private static final String SUBRULE_54_4 = "54-4";
    private static final Logger LOG = LogManager.getLogger(BiodegradableWaste.class);

	@Override
	public Plan validate(Plan pl) {
		HashMap<String, String> errors = new HashMap<>();
			
		return pl;

	}

	@Override
	public Plan process(Plan pl) {
		validate(pl);
		scrutinyDetail = new ScrutinyDetail();
		scrutinyDetail.addColumnHeading(1, RULE_NO);
		scrutinyDetail.addColumnHeading(2, DESCRIPTION);
		scrutinyDetail.addColumnHeading(3, REQUIRED);
		scrutinyDetail.addColumnHeading(4, PROVIDED);
		scrutinyDetail.addColumnHeading(5, STATUS);
		scrutinyDetail.setKey("Common_Biodegradable Waste Treatment");
		String subRule = SUBRULE_54_4;
		
		 List<BiodegradableWasteTreatment> waste = pl.getUtility().getBiodegradableWasteTreatment();
		 
		 if (waste == null || waste.isEmpty()) {
		        checkMandatoryWasteConditions(pl, waste);
		        return pl;
		    }

		 
		 addBioWasteInfoToReport(pl, scrutinyDetail, waste);
				
		return pl;
	}
	
	private void checkMandatoryWasteConditions(Plan pl, List<BiodegradableWasteTreatment> biodegradableWaste) {
	    boolean hasWaste = biodegradableWaste != null && !biodegradableWaste.isEmpty();
	    LOG.info("Checking mandatory STP conditions. hasWaste={}", hasWaste);

	    BigDecimal plotArea = pl.getPlot() != null ? pl.getPlot().getArea() : BigDecimal.ZERO;
	    BigDecimal builtUpArea = pl.getVirtualBuilding() != null ? pl.getVirtualBuilding().getTotalBuitUpArea() : BigDecimal.ZERO;
	    BigDecimal hospitalBeds = pl.getPlanInformation().getNoOfBeds();

	    OccupancyTypeHelper mostRestrictiveOccupancy = pl.getVirtualBuilding() != null
	            ? pl.getVirtualBuilding().getMostRestrictiveFarHelper()
	            : null;

	    String typeCode = mostRestrictiveOccupancy != null && mostRestrictiveOccupancy.getType() != null
	            ? mostRestrictiveOccupancy.getType().getCode()
	            : null;
	    String subtypeCode = mostRestrictiveOccupancy != null && mostRestrictiveOccupancy.getSubtype() != null
	            ? mostRestrictiveOccupancy.getSubtype().getCode()
	            : null;

	    LOG.info("Occupancy check: typeCode={}, subtypeCode={}, plotArea={}, builtUpArea={}, hospitalBeds={}",
	            typeCode, subtypeCode, plotArea, builtUpArea, hospitalBeds);

	    // (ia) Residential layouts with plot area >= 4000 sq.m.
	    if (A.equals(typeCode)) {
	        if (plotArea != null && plotArea.compareTo(BigDecimal.valueOf(4000)) >= 0 && !hasWaste) {
	            LOG.info("Error: {}", MSG_PLOT_AREA);
	            pl.addError(VALIDATION_MANDATORY_STP_MISSING, MSG_PLOT_AREA);
	        }
	    }

	    if (A_AF.equals(subtypeCode)) {
	        if (builtUpArea != null && builtUpArea.compareTo(BigDecimal.valueOf(3000)) > 0 && !hasWaste) {
	            LOG.info("Error: {}", MSG_GROUP_HOUSING);
	            pl.addError(VALIDATION_MANDATORY_STP_MISSING, MSG_GROUP_HOUSING);
	        }
	    }

	    if (F.equals(typeCode) || G.equals(typeCode)) {
	        if (builtUpArea != null && builtUpArea.compareTo(BigDecimal.valueOf(1500)) > 0 && !hasWaste) {
	            LOG.info("Error: {}", MSG_COMMERCIAL);
	            pl.addError(VALIDATION_MANDATORY_STP_MISSING, MSG_COMMERCIAL);
	        }
	    }

	    if (C.equals(typeCode)) {
	        if (hospitalBeds != null && hospitalBeds.compareTo(BigDecimal.valueOf(40)) >= 0 && !hasWaste) {
	            LOG.info("Error: {}", MSG_HOSPITAL);
	            pl.addError(VALIDATION_MANDATORY_STP_MISSING, MSG_HOSPITAL);
	        }
	    }
	}
	
	private void addBioWasteInfoToReport(Plan pl, ScrutinyDetail scrutinyDetail,
	        List<BiodegradableWasteTreatment> waste) {

	    LOG.info("Adding biodegradable waste info to report for plan");

	    for (BiodegradableWasteTreatment bioWaste : waste) {
	        LOG.info("Processing biodegradable waste");
	       
	        // Collect area (if available)
	        String area = bioWaste.getArea() != null && bioWaste.getArea().compareTo(BigDecimal.ZERO) > 0
	                ? bioWaste.getArea().toString()
	                : "Not Provided";

	        // Build "Provided" column dynamically
	        StringBuilder providedBuilder = new StringBuilder();
	        if (!"Not Provided".equals(area)) {
	            providedBuilder.append("Area: ").append(area).append(" sq.m");
	        }
	       
	        // If absolutely nothing is provided, skip adding to report
	        if (providedBuilder.length() == 0) {
	            LOG.info("Skipping biodegradable waste - no details provided.");
	            continue;
	        }

	        LOG.info("Adding biodegradable waste details to report: {}", providedBuilder);

	        ReportScrutinyDetail detail = new ReportScrutinyDetail();
	        detail.setRuleNo(RULE_45_E);
	        detail.setDescription("Biodegradable waste  provided");
	        detail.setPermitted("As per approved norms");
	        detail.setProvided(providedBuilder.toString());
	        detail.setStatus(Result.Verify.toString()); 

	        Map<String, String> details = mapReportDetails(detail);
	        addScrutinyDetailtoPlan(scrutinyDetail, pl, details);
	    }
	}




    private void setReportOutputDetailsWithoutOccupancy(Plan pl, String ruleNo, String ruleDesc, String expected, String actual,
            String status) {
        ReportScrutinyDetail detail = new ReportScrutinyDetail();
        detail.setRuleNo(ruleNo);
        detail.setDescription(ruleDesc);
        detail.setRequired(expected);
        detail.setProvided(actual);
        detail.setStatus(status);

        Map<String, String> details = mapReportDetails(detail);
        addScrutinyDetailtoPlan(scrutinyDetail, pl, details);
    }
    
    @Override
    public Map<String, Date> getAmendments() {
        return new LinkedHashMap<>();
    }
}
