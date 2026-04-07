package org.egov.common.entity.edcr;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ContinuousGreenPlantationStrip implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7322639893668499989L;

	private List<Measurement> continuousGreenPlantationStrips = new ArrayList<>();

	private BigDecimal noOfTreesToBePlant = BigDecimal.ZERO;

	public List<Measurement> getContinuousGreenPlantationStrips() {
		return continuousGreenPlantationStrips;
	}

	public void setContinuousGreenPlantationStrips(List<Measurement> continuousGreenPlantationStrips) {
		this.continuousGreenPlantationStrips = continuousGreenPlantationStrips;
	}

	public BigDecimal getNoOfTreesToBePlant() {
		return noOfTreesToBePlant;
	}

	public void setNoOfTreesToBePlant(BigDecimal noOfTreesToBePlant) {
		this.noOfTreesToBePlant = noOfTreesToBePlant;
	}

}
