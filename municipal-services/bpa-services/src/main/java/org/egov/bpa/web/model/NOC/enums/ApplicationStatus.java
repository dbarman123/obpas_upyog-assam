package org.egov.bpa.web.model.NOC.enums;

public enum ApplicationStatus {

	DRAFT("DRAFT"), 
	APPROVED("APPROVED"),
	COMPLETED("COMPLETED"),
	PENDING("PENDING"),
	REJECTED("REJECTED");

	private final String description;

	// Constructor for enum
	ApplicationStatus(String description) {
		this.description = description;
	}

	// Getter method to retrieve the description
	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return this.description;
	}


}
