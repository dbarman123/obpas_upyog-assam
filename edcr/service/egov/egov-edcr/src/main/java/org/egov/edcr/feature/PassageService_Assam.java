package org.egov.edcr.feature;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.entity.edcr.*;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.service.MDMSCacheManager;
import org.egov.edcr.utility.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.egov.common.entity.edcr.Passage;

import static org.egov.edcr.constants.CommonKeyConstants.*;
import static org.egov.edcr.constants.EdcrReportConstants.*;
import static org.egov.edcr.service.FeatureUtil.addScrutinyDetailtoPlan;
import static org.egov.edcr.service.FeatureUtil.mapReportDetails;

public class PassageService_Assam extends FeatureProcess {
    private static final Logger LOG = LogManager.getLogger(PassageService_Assam.class);

    @Autowired
    MDMSCacheManager cache;

    /**
     * Validates the plan (no validation logic implemented for passages).
     *
     * @param plan The plan to validate
     * @return The unchanged plan
     */
    @Override
    public Plan validate(Plan plan) {
        return plan;
    }

    /**
     * Main processing method that validates passage and passage stair dimensions
     * for all blocks based on building occupancy type and passage length.
     *
     * @param plan The plan to process
     * @return The processed plan with validation results
     */
    @Override
    public Plan process(Plan plan) {
    	LOG.info("Starting passage validation process for plan: {}", plan.getPlanInformation());

        BigDecimal passageStairMinimumWidth = BigDecimal.ZERO;
        BigDecimal passageServiceValueA = BigDecimal.ZERO;
        BigDecimal passageServiceValueBD = BigDecimal.ZERO;
        BigDecimal passageServiceValueDefault = BigDecimal.ZERO;
        BigDecimal passageServiceValueFH = BigDecimal.ZERO;
        BigDecimal passageServiceValueFlow = BigDecimal.ZERO;
        BigDecimal passageServiceValueFhigh = BigDecimal.ZERO;
        BigDecimal passageServicePassageLength = BigDecimal.ZERO;

        List<Object> rules = cache.getFeatureRules(plan, FeatureEnum.PASSAGE_SERVICE.getValue(), false);
        LOG.info("Fetched {} rules for PASSAGE_SERVICE feature.", rules.size());

        for (Object obj : rules) {
            PassageRequirement rule = (PassageRequirement) obj;
            passageStairMinimumWidth = rule.getPassageServiceValueOne();
            LOG.info("Passage stair minimum width from rules: {}", passageStairMinimumWidth);
            passageServiceValueA = rule.getPassageServiceValueA();
            passageServiceValueBD = rule.getPassageServiceValueBD();
            passageServiceValueDefault = rule.getPassageServiceValueDefault();
            passageServiceValueFH = rule.getPassageServiceValueFH();
            passageServiceValueFlow = rule.getPassageServiceValueFlow();
            passageServiceValueFhigh = rule.getPassageServiceValueFhigh();
            passageServicePassageLength = rule.getPassageServicePassageLength();
            break;
        }

        for (Block block : plan.getBlocks()) {
        	LOG.info("Processing Block: {}", block.getNumber());

            if (block.getBuilding() == null || block.getBuilding().getPassage() == null) {
            	LOG.info("Skipping Block {} as it has no building or passage defined.", block.getNumber());
                continue;
            }

            ScrutinyDetail passageDetail = createScrutinyDetail(BLOCK + block.getNumber() + PASSAGE_SUFFIX);
            ScrutinyDetail passageStairDetail = createScrutinyDetail(BLOCK + block.getNumber() + PASSAGE_STAIR_SUFFIX);

            Passage passage = block.getBuilding().getPassage();
            BigDecimal passageLength = passage.getPassageLength();
            BigDecimal requiredPassageWidth = getBuildingTypeBasedWidth(block.getBuilding(), passageLength,
                    passageServiceValueA,
                    passageServiceValueBD,
                    passageServiceValueDefault,
                    passageServiceValueFH,
                    passageServiceValueFlow,
                    passageServiceValueFhigh,
                    passageServicePassageLength
            );

            validatePassageDimension(
                    passage.getPassageDimensions(),
                    requiredPassageWidth,
                    RULE41,
                    RULE_41_DESCRIPTION,
                    passageDetail,
                    plan
            );
            LOG.info("Validated passage dimensions for Block {}", block.getNumber());

            validatePassageDimension(
                    passage.getPassageStairDimensions(),
                    passageStairMinimumWidth,
                    RULE39_6,
                    RULE39_6_DESCRIPTION,
                    passageStairDetail,
                    plan
            );
            LOG.info("Validated passage stair dimensions for Block {}", block.getNumber());
        }

        LOG.info("Completed passage validation process for plan.");
        return plan;
    }


    /**
     * Determines required passage width based on building occupancy type and passage length.
     * Different widths apply for residential (A), commercial (F), educational/institutional (B/D),
     * and hotel (F-H) occupancies.
     *
     * @param building The building to check
     * @param passageLength Length of the passage
     * @param passageServiceValueA Width for residential buildings
     * @param passageServiceValueBD Width for educational/institutional buildings
     * @param passageServiceValueDefault Default width for other occupancies
     * @param passageServiceValueFH Width for hotel buildings
     * @param passageServiceValueFlow Width for short commercial passages
     * @param passageServiceValueFhigh Width for long commercial passages
     * @param passageServicePassageLength Length threshold for commercial passages
     * @return Required passage width based on occupancy type
     */
    private BigDecimal getBuildingTypeBasedWidth(Building building, BigDecimal passageLength,
                                                 BigDecimal passageServiceValueA,
                                                 BigDecimal passageServiceValueBD,
                                                 BigDecimal passageServiceValueDefault,
                                                 BigDecimal passageServiceValueFH,
                                                 BigDecimal passageServiceValueFlow,
                                                 BigDecimal passageServiceValueFhigh,
                                                 BigDecimal passageServicePassageLength
                                                 ) {
        String occupancy = building.getMostRestrictiveFarHelper().getType() != null
                ? building.getMostRestrictiveFarHelper().getType().getCode() : null;
        String subOccupancy = building.getMostRestrictiveFarHelper().getSubtype() != null
                ? building.getMostRestrictiveFarHelper().getSubtype().getCode() : null;

        // Return passage width based on occupancy
        if (occupancy != null) {
            LOG.info("Setting Passage Width according to Occupancy: " + occupancy);
            switch (occupancy) {
                case DxfFileConstants.A:
                    return passageServiceValueA;
                case DxfFileConstants.F:
                    return getShoppingComplexWidth(passageLength, passageServiceValueFlow, passageServiceValueFhigh, passageServicePassageLength);
                case DxfFileConstants.B:
                case DxfFileConstants.D:
                    return passageServiceValueBD;
                default:
                    return passageServiceValueDefault;
            }
        }
        if (subOccupancy != null) {
            LOG.info("Setting Passage Width according to SubOccupancy: " + subOccupancy);
            switch (subOccupancy) {
                case DxfFileConstants.F_RT:
                    return passageServiceValueFH;
                default:
                    return passageServiceValueDefault;
            }
        }

        return passageServiceValueDefault;
    }

    /**
     * Determines passage width for shopping complexes based on passage length.
     * Uses higher width requirement for passages longer than threshold.
     *
     * @param passageLength Length of the passage
     * @param passageServiceValueFlow Width for shorter passages
     * @param passageServiceValueFhigh Width for longer passages
     * @param passageServicePassageLength Length threshold
     * @return Required passage width for shopping complex
     */
    private BigDecimal getShoppingComplexWidth(BigDecimal passageLength, BigDecimal passageServiceValueFlow, BigDecimal passageServiceValueFhigh, BigDecimal passageServicePassageLength) {
        if (passageLength != null) {
//            BigDecimal maxLength = passageLength.stream().reduce(BigDecimal::max).orElse(BigDecimal.ZERO);
            return passageLength.compareTo(passageServicePassageLength) > 0 ? passageServiceValueFhigh : passageServiceValueFlow;
        }
        return passageServiceValueFlow;
    }

    /**
     * Validates passage dimensions against minimum width requirements.
     * Finds minimum width from provided dimensions and compares with permissible width.
     *
     * @param dimensions List of passage dimensions
     * @param permissibleWidth Minimum required width
     * @param ruleNo Rule number for validation
     * @param ruleDesc Rule description
     * @param detail Scrutiny detail object to store results
     * @param plan The plan being validated
     */
    private void validatePassageDimension(List<BigDecimal> dimensions, BigDecimal permissibleWidth,
                                          String ruleNo, String ruleDesc, ScrutinyDetail detail, Plan plan) {
        LOG.info("Validating Passage Dimensions...");
        if (dimensions != null && !dimensions.isEmpty()) {
            BigDecimal minWidth = Util.roundOffTwoDecimal(dimensions.stream().reduce(BigDecimal::min).get());
            String result = minWidth.compareTo(permissibleWidth) >= 0
                    ? Result.Accepted.getResultVal()
                    : Result.Not_Accepted.getResultVal();

            setReportOutputDetails(plan, ruleNo, ruleDesc, permissibleWidth.toString(),
                    String.valueOf(minWidth), result, detail);
        }
    }

    /**
     * Creates a new scrutiny detail object with standard column headings
     * for passage validation reports.
     *
     * @param key Unique key for the scrutiny detail
     * @return Configured ScrutinyDetail object
     */
    private ScrutinyDetail createScrutinyDetail(String key) {
        ScrutinyDetail detail = new ScrutinyDetail();
        detail.addColumnHeading(1, RULE_NO);
        detail.addColumnHeading(2, REQUIRED);
        detail.addColumnHeading(3, PROVIDED);
        detail.addColumnHeading(4, STATUS);
        detail.setKey(key);
        return detail;
    }

    /**
     * Sets validation results in the report output by creating and adding
     * scrutiny details to the plan.
     *
     * @param pl The plan to add results to
     * @param ruleNo Rule number
     * @param ruleDesc Rule description
     * @param expected Expected/required value
     * @param actual Provided/actual value
     * @param status Validation status (Accepted/Not_Accepted)
     * @param scrutinyDetail Scrutiny detail object to add results to
     */
    private void setReportOutputDetails(Plan pl, String ruleNo, String ruleDesc, String expected, String actual,
                                        String status, ScrutinyDetail scrutinyDetail) {
        ReportScrutinyDetail detail = new ReportScrutinyDetail();
        detail.setRuleNo(ruleNo);
        detail.setDescription(ruleDesc);
        detail.setRequired(expected);
        detail.setProvided(actual);
        detail.setStatus(status);

        Map<String, String> details = mapReportDetails(detail);
        addScrutinyDetailtoPlan(scrutinyDetail, pl, details);
    }

    /**
     * Returns amendments map (empty for this implementation).
     *
     * @return Empty LinkedHashMap of amendments
     */
    @Override
    public Map<String, Date> getAmendments() {
        return new LinkedHashMap<>();
    }
}