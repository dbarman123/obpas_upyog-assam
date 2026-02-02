package org.egov.bpa.web.model.NOC.enums;

public enum CurrentStates {

	PROJECT_DRAFT("Project Draft"), REVIEW_LEVEL_1("Review Level 1"), REVIEW_LEVEL_2("Review Level 2"),
	REVIEW_LEVEL_3("Review Level 3"), REVIEW_LEVEL_4("Review Level 4"), REVIEW_LEVEL_CE("Review Level CE"),
	CONSULTANT_LEVEL_1("Cosultant Level 1"), CONSULTANT_LEVEL_2("Cosultant Level 2"),
	CONSULTANT_LEVEL_3("Cosultant Level 3"), CONSULTANT_ACE_LEVEL_1("Cosultant ACE Level 1"),
	CONSULTANT_ACE_LEVEL_2("Cosultant ACE Level 2"), CONSULTANT_ACE_LEVEL_3("Cosultant ACE Level 3"),
	COMPLETED("Completed"), REJECT("Reject"), CONSULTANT_CE_LEVEL_1("Cosultant CE Level 1"),
	CONSULTANT_CE_LEVEL_2("Cosultant CE Level 2"), CONSULTANT_CE_LEVEL_3("Cosultant CE Level 3"),
	CONSULTANT_CE_LEVEL_4("Cosultant CE Level 4"),MB_DRAFT("MB Draft"),BILL_DRAFT("Bill Draft");

	private final String description;

	// Constructor for enum
	CurrentStates(String description) {
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
