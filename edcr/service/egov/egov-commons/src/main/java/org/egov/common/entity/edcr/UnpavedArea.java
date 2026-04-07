package org.egov.common.entity.edcr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UnpavedArea implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1807751893213416265L;

	private List<Measurement> unpavedArea = new ArrayList<>();

	public List<Measurement> getUnpavedArea() {
		return unpavedArea;
	}

	public void setUnpavedArea(List<Measurement> unpavedArea) {
		this.unpavedArea = unpavedArea;
	}

}
