package org.egov.common.entity.edcr;

import java.io.Serializable;

public class RecreationalSpaceDetails implements Serializable {

	private static final long serialVersionUID = -294197037077759384L;

	private Plantation plantation = new Plantation();

	private ContinuousGreenPlantationStrip continuousGreenPlantationStrip = new ContinuousGreenPlantationStrip();

	private UnpavedArea unpavedArea = new UnpavedArea();

	private Landcaping landcaping = new Landcaping();

	public Plantation getPlantation() {
		return plantation;
	}

	public void setPlantation(Plantation plantation) {
		this.plantation = plantation;
	}

	public ContinuousGreenPlantationStrip getContinuousGreenPlantationStrip() {
		return continuousGreenPlantationStrip;
	}

	public void setContinuousGreenPlantationStrip(ContinuousGreenPlantationStrip continuousGreenPlantationStrip) {
		this.continuousGreenPlantationStrip = continuousGreenPlantationStrip;
	}

	public UnpavedArea getUnpavedArea() {
		return unpavedArea;
	}

	public void setUnpavedArea(UnpavedArea unpavedArea) {
		this.unpavedArea = unpavedArea;
	}

	public Landcaping getLandcaping() {
		return landcaping;
	}

	public void setLandcaping(Landcaping landcaping) {
		this.landcaping = landcaping;
	}

}
