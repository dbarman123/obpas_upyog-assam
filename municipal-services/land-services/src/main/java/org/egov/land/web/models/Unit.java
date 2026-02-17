package org.egov.land.web.models;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.SafeHtml;
import org.springframework.validation.annotation.Validated;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Unit
 */
@Validated
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Unit {

  @ApiModelProperty(value = "Unique Identifier of the Unit(UUID).")
  @SafeHtml
  private String id;

  @ApiModelProperty(value = "Tenant ID of the Property.")
  @SafeHtml
  @Size(min = 2, max = 256)
  private String tenantId;

  @ApiModelProperty(value = "Floor number of the Unit.")
  @SafeHtml
  @Size(min = 1, max = 64)
  private String floorNo;

  @ApiModelProperty(value = "Unit type is master data.")
  @SafeHtml
  private String unitType;

  @ApiModelProperty(value = "This is about the usage of the property like Residential, Non-residential, Mixed (Property used for Residential and Non-residential purposes).")
  @SafeHtml
  @Size(max = 64)
  private String usageCategory;

  @ApiModelProperty(value = "Occupancy type of the Unit.")
  @SafeHtml
  private String occupancyType;

  @ApiModelProperty(value = "Date on which the unit is occupied.")
  private Long occupancyDate;

  @ApiModelProperty(value = "JSON object to capture any extra information which is not accommodated by the model.")
  private Object additionalDetails;

  @ApiModelProperty(value = "Audit details of the Unit.")
  @Valid
  private AuditDetails auditDetails;
  
  private String specifyUsage;
}