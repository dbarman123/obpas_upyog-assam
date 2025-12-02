package org.egov.edcr.feature;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.entity.edcr.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.egov.edcr.constants.CommonFeatureConstants.*;
import static org.egov.edcr.constants.CommonKeyConstants.BLOCK;
import static org.egov.edcr.service.FeatureUtil.addScrutinyDetailtoPlan;
import static org.egov.edcr.service.FeatureUtil.mapReportDetails;

@Service
public class GarbagePit extends FeatureProcess {
    private static final Logger LOG = LogManager.getLogger(GarbagePit.class);

    @Override
    public Plan validate(Plan plan) {
        return plan;
    }


    @Override
    public Plan process(Plan plan) {

        if (!plan.getBlocks().isEmpty())
            for (Block bl : plan.getBlocks()) {
                ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
                scrutinyDetail.setKey(BLOCK + bl.getNumber() + UNDERSCORE + GARBAGE_PIT);
                scrutinyDetail.addColumnHeading(1, DESCRIPTION);
                scrutinyDetail.addColumnHeading(2, PROVIDED);
                scrutinyDetail.addColumnHeading(3, STATUS);

                if (!plan.getGarbagePit().getDryGarbagePits().isEmpty()) {
                    ReportScrutinyDetail detail = new ReportScrutinyDetail();

                    if (!plan.getGarbagePit().getDryGarbagePits().isEmpty()) {
                        detail.setDescription(DRY_GARBAGE_DESCRIPTION);
                        detail.setProvided(DRY_GARBAGE_PROVIDED);
                    }
                    detail.setStatus(Result.Accepted.getResultVal());

                    Map<String, String> details = mapReportDetails(detail);
                    addScrutinyDetailtoPlan(scrutinyDetail, plan, details);
                }

                if (!plan.getGarbagePit().getWetGarbagePits().isEmpty()) {
                    ReportScrutinyDetail detail = new ReportScrutinyDetail();

                    if (!plan.getGarbagePit().getDryGarbagePits().isEmpty()) {
                        detail.setDescription(WET_GARBAGE_DESCRIPTION);
                        detail.setProvided(WET_GARBAGE_PROVIDED);
                    }
                    detail.setStatus(Result.Accepted.getResultVal());

                    Map<String, String> details = mapReportDetails(detail);
                    addScrutinyDetailtoPlan(scrutinyDetail, plan, details);
                }

            }
        return plan;
    }

    /**
     * Returns amendment information for this feature.
     * Currently returns an empty map as no amendments are tracked.
     *
     * @return An empty LinkedHashMap of amendments
     */
    @Override
    public Map<String, Date> getAmendments() {
        return new LinkedHashMap<>();
    }
}
