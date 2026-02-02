package org.egov.bpa.web.model.NOC.enums;

public enum ApplicationActions {
	CREATE("Create"), 
	FORWARD_TO_AE("FORWARD_TO_AE"),
	FORWARD_TO_EE("FORWARD_TO_EE"),
	FORWARD_TO_SE("FORWARD_TO_SE"),
	FORWARD_TO_ACE("FORWARD_TO_ACE"),
	FORWARD_TO_CE("FORWARD_TO_CE"),
	SEND_BACK_TO_JE("SEND_BACK_TO_JE"),
	SEND_BACK_TO_AE("SEND_BACK_TO_AE"),
	SEND_BACK_TO_EE("SEND_BACK_TO_EE"),
	SEND_BACK_TO_SE("SEND_BACK_TO_SE"),
	APPROVE("Approve"),
	REJECT("Reject");

	private final String description;

	// Constructor for enum
	ApplicationActions(String description) {
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
