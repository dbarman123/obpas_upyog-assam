package org.egov.bpa.util;

public enum ServiceType {

	BPA_SERVICE("BPA_GMDA_GMC"), 
	OC_SERVICE("OC_GMDA_GMC");

	private final String value;

	ServiceType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
