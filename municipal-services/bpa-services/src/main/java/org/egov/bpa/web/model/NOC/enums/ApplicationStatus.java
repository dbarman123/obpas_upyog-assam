package org.egov.bpa.web.model.NOC.enums;

import java.util.EnumSet;
import java.util.Set;

public enum ApplicationStatus {

	FORWARDED_TO_ZONAL_OFFICER,
	FORWARDED_TO_TECHNICAL_ENGINEER_GP,
	FORWARDED_TO_TECHNICAL_ENGINEER_MB;

	private static final Set<ApplicationStatus> APPLICATION_STATUSES =
			EnumSet.of(
					FORWARDED_TO_ZONAL_OFFICER,
					FORWARDED_TO_TECHNICAL_ENGINEER_GP,
					FORWARDED_TO_TECHNICAL_ENGINEER_MB);

	public static boolean contains(ApplicationStatus status) {
		return APPLICATION_STATUSES.contains(status);
	}
	
	public static boolean isValid(String value) {
		for (ApplicationStatus status : ApplicationStatus.values()) {
			if (status.name().equals(value)) {
				return true;
			}
		}
		return false;
	}	
}
