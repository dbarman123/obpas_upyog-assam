package org.egov.common.entity.edcr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Landcaping implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1807751893213416265L;

	private List<Measurement> Landcaping = new ArrayList<>();

	public List<Measurement> getLandcaping() {
		return Landcaping;
	}

	public void setLandcaping(List<Measurement> landcaping) {
		Landcaping = landcaping;
	}

}
