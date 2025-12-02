package org.egov.edcr.feature;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.entity.edcr.Footpath;
import org.egov.common.entity.edcr.Garbage;
import org.egov.common.entity.edcr.Measurement;
import org.egov.edcr.entity.blackbox.MeasurementDetail;
import org.egov.edcr.entity.blackbox.PlanDetail;
import org.egov.edcr.service.LayerNames;
import org.egov.edcr.utility.Util;
import org.kabeja.dxf.DXFLWPolyline;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class GarbagePitExtract extends FeatureExtract {
    private static final Logger LOG = LogManager.getLogger(GarbagePitExtract.class);

    @Autowired
    private LayerNames layerNames;

    @Override
    public PlanDetail validate(PlanDetail pl) {
        return pl;
    }

    @Override
    public PlanDetail extract(PlanDetail pl) {
        if (LOG.isInfoEnabled())
            LOG.info(".......Starting Garbage Extract......");

        String dryGarbageLayer = layerNames.getLayerName("LAYER_NAME_DRY_GARBAGE_PIT");
        String wetGarbageLayer = layerNames.getLayerName("LAYER_NAME_WET_GARBAGE_PIT");
        List<String> dryGarbageLayerNames = Util.getLayerNamesLike(pl.getDoc(), dryGarbageLayer);
        List<String> wetGarbageLayerNames = Util.getLayerNamesLike(pl.getDoc(), wetGarbageLayer);

        Garbage garbage = new Garbage();
        if (!dryGarbageLayerNames.isEmpty()) {
            for (String layerName : dryGarbageLayerNames) {
                List<DXFLWPolyline> garbagePolygons = Util.getPolyLinesByLayer(pl.getDoc(), layerName);
                if (garbagePolygons != null && !garbagePolygons.isEmpty()) {
                    List<Measurement> garbagePits = new ArrayList<>();
                    for (DXFLWPolyline polygon : garbagePolygons) {
                        Measurement measurement = new MeasurementDetail(polygon, true);
                        measurement.setName(layerName);
                        garbagePits.add(measurement);
                    }
                    garbage.setDryGarbagePits(garbagePits);
                }
            }
        }

        if (!wetGarbageLayerNames.isEmpty()) {
            for (String layerName : wetGarbageLayerNames) {
                List<DXFLWPolyline> garbagePolygons = Util.getPolyLinesByLayer(pl.getDoc(), layerName);
                if (garbagePolygons != null && !garbagePolygons.isEmpty()) {
                    List<Measurement> garbagePits = new ArrayList<>();
                    for (DXFLWPolyline polygon : garbagePolygons) {
                        Measurement measurement = new MeasurementDetail(polygon, true);
                        measurement.setName(layerName);
                        garbagePits.add(measurement);
                    }
                    garbage.setWetGarbagePits(garbagePits);
                }
            }
        }

        pl.setGarbagePit(garbage);

        if (LOG.isInfoEnabled())
            LOG.info(".......End of Garbage Extract......");
        return pl;
    }
}
