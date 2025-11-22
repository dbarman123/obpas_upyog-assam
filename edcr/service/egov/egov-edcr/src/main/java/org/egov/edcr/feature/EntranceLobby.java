package org.egov.edcr.feature;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.entity.edcr.Plan;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EntranceLobby extends FeatureProcess{
    private static final Logger LOG = LogManager.getLogger(EntranceLobby.class);

    public Plan validate(Plan plan){
        return plan;
    }

    @Override
    public Plan process(Plan pl){
        LOG.info("Entrance Lobby Feature....");
        return pl;
    }

    public Map<String, Date> getAmendments(){return new LinkedHashMap<>();}
}
