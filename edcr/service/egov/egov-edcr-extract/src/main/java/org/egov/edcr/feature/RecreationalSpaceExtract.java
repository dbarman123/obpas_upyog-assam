package org.egov.edcr.feature;

import java.math.BigDecimal;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Occupancy;
import org.egov.common.entity.edcr.OccupancyType;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.entity.blackbox.MeasurementDetail;
import org.egov.edcr.entity.blackbox.PlanDetail;
import org.egov.edcr.service.LayerNames;
import org.egov.edcr.utility.Util;
import org.kabeja.dxf.DXFLWPolyline;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RecreationalSpaceExtract extends FeatureExtract {
	private static final Logger LOG = LogManager.getLogger(RecreationalSpaceExtract.class);
	public static final String SUB_RULE_50_DESC = "Recreational space for Residential Apartment ";
	public static final String SUB_RULE_50_DESC_CELLER = " Ground floor Recreational space ";
	public static final String SUB_RULE_50 = "50";
	public static final String SUB_RULE_50_2 = "50(2)";
	public static final String RECREATION = "RECREATION";
	public static final String RECREATIONAL_SPACE = "RECREATIONAL_SPACE";
	public static final int TOTALNUMBEROFUNITS = 12;
	public static final BigDecimal THREE = BigDecimal.valueOf(3);

	public static final int PLANTATION_COLOR_CODE = 112;
	public static final int CONTINUOUS_GREEN_PLANTATION_STRIP_COLOR_CODE = 106;
	public static final int UNPAVED_AREA_COLOR_CODE = 122;
	public static final int LANDCAPING_COLOR_CODE = 124;
	@Autowired
	private LayerNames layerNames;

	@Override
	public PlanDetail extract(PlanDetail pl) {
		if (LOG.isDebugEnabled())
			LOG.debug("Starting of Recreational Space Extract......");
		String layerRegEx = RECREATIONAL_SPACE;
//        for (Block block : pl.getBlocks())
//            for (Floor floor : block.getBuilding().getFloors()) {
//                layerRegEx = layerNames.getLayerName("LAYER_NAME_BLOCK_NAME_PREFIX") + block.getNumber() + "_"
//                        + layerNames.getLayerName("LAYER_NAME_FLOOR_NAME_PREFIX") + floor.getNumber() + "_"
//                        + RECREATION;
//                for (DXFLWPolyline pline : Util.getPolyLinesByLayer(pl.getDoc(), layerRegEx))
//                    for (Occupancy existingOcc : floor.getOccupancies())
//                        if (OccupancyType.OCCUPANCY_A4.equals(existingOcc.getType()))
//                            // defined
//                            // only for apartment occupancies.
//                            existingOcc.getRecreationalSpace().add(new MeasurementDetail(pline, true));
//            }
		List<DXFLWPolyline> dxflwPolylines = Util.getPolyLinesByLayer(pl.getDoc(), layerRegEx);

		// Plantation
		for (DXFLWPolyline pline : Util.getPolyLinesByLayerAndColor(pl.getDoc(), layerRegEx, PLANTATION_COLOR_CODE,
				pl)) {
			pl.getRecreationalSpaceDetails().getPlantation().getPlantations().add(new MeasurementDetail(pline, true));
		}

		// Continuous Green Plantation Strip
		for (DXFLWPolyline pline : Util.getPolyLinesByLayerAndColor(pl.getDoc(), layerRegEx,
				CONTINUOUS_GREEN_PLANTATION_STRIP_COLOR_CODE, pl)) {
			MeasurementDetail measurementDetail = new MeasurementDetail(pline, true);
			pl.getRecreationalSpaceDetails().getContinuousGreenPlantationStrip().getContinuousGreenPlantationStrips()
					.add(measurementDetail);
		}

		try {
			if (!pl.getRecreationalSpaceDetails().getContinuousGreenPlantationStrip()
					.getContinuousGreenPlantationStrips().isEmpty()) {
				BigDecimal treeCount = new BigDecimal(Util.getMtextByLayerName(pl.getDoc(), layerRegEx, "TREE"));
				pl.getRecreationalSpaceDetails().getContinuousGreenPlantationStrip().setNoOfTreesToBePlant(treeCount);
			}

		} catch (Exception e) {
			e.printStackTrace();
			pl.addError(layerRegEx + "treeCount",
					"Mtex is either not defined or not numeric in layer RECREATIONAL_SPACE");
		}
		
		//UNPAVED AREA
		for (DXFLWPolyline pline : Util.getPolyLinesByLayerAndColor(pl.getDoc(), layerRegEx,
				UNPAVED_AREA_COLOR_CODE, pl)) {
			MeasurementDetail measurementDetail = new MeasurementDetail(pline, true);
			pl.getRecreationalSpaceDetails().getUnpavedArea().getUnpavedArea()
					.add(measurementDetail);
		}
		
		// LANDCAPING
		for (DXFLWPolyline pline : Util.getPolyLinesByLayerAndColor(pl.getDoc(), layerRegEx, LANDCAPING_COLOR_CODE,
				pl)) {
			MeasurementDetail measurementDetail = new MeasurementDetail(pline, true);
			pl.getRecreationalSpaceDetails().getLandcaping().getLandcaping().add(measurementDetail);
		}

		if (LOG.isDebugEnabled())
			LOG.debug("End of Recreational Space Extract......");
		return pl;
	}

	@Override
	public PlanDetail validate(PlanDetail pl) {
		return pl;
	}

}
