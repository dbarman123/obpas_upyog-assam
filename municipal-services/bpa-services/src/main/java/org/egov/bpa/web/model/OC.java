package org.egov.bpa.web.model;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.egov.bpa.web.model.landInfo.LandInfo;
import org.egov.bpa.web.model.landInfo.OwnerInfoV2;
import org.hibernate.validator.constraints.SafeHtml;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ApiModel(description = "OC application object to capture the details of land; land owners; and address of the land.")
@Validated
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class OC {

	/** Unique Identifier(UUID) of the BPA application for internal reference. */
	@SafeHtml
	@Size(min = 1, max = 64)
	private String id;

	/** Unique ULB identifier. */
	@SafeHtml
	@NotNull
	@Size(min = 2, max = 256)
	private String tenantId;

	/** Formatted unique identifier of the building permit application. */
	@SafeHtml
	@Size(min = 1, max = 64)
	private String applicationNo;

	/** Application submission date. */
	private Long applicationDate;

	/** Unique Identifier(UUID) of the land for internal reference. */
	@SafeHtml
	@Size(min = 1, max = 64)
	private String landId;

	/** Land information associated with the application. */
	private LandInfo landInfo;

	/** Building Permit application number. */
	private String bpaApplicationNo;

	private String nameOfMasterPlan;

	private String nameOfUlbPanchayat;

	private String nameOfApplicant;
	
	private String phoneNumber;
	
	private String email;

	/** Approval number based on workflow status. */
	@SafeHtml
	@Size(min = 1, max = 64)
	private String approvalNo;

	/** Approval date based on workflow status. */
	private Long approvalDate;

	private String occupancyCertificateNo;

	/** Status of the application. */
	@SafeHtml
	private String status;

	/** Property owners, these will be citizen users in the system */
	@JsonProperty("owners")
	@NotNull
	@Valid
	private List<OwnerInfoV2> owners;

	private AreaMappingDetail areaMapping;

	/** Business service associated with the application. */
	@SafeHtml
	@Size(min = 1, max = 64)
	private String businessService;

	/** JSON object to capture custom fields. */
	private Object additionalDetails;

	/** Workflow details of the application. */
	private Workflow workflow;

	/** List of documents attached by the owner for exemption. */
	@Valid
	private List<Document> documents = new ArrayList<>();

	private String nocNo;
	private String nocDate;
	private String proposedUseOfBuilding;
	private String noOfFloors;

	@Builder.Default
	private Boolean isPaymentDone = false;

	private String ocFileStoreId;

	// attachments
	private String planningPermit;
	private String buildingPermit;

	private String buildingPermitDrawing;
	private String asBuiltDrawing;

	private String form16;
	private String form17;
	private String form18;
	private String form19;
	private String form27;
	private String liftLicense;
	private String powerAllotmentLetter;
	private String groundWaterAuthorityClearance;
	private String buildingPhotoGraph1;
	private String buildingPhotoGraph2;
	private String buildingPhotoGraph3;
	private String buildingPhotoGraph4;

	/** Audit details of the application. */
	private AuditDetails auditDetails;
}
