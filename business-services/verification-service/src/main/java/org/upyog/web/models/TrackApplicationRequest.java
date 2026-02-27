package org.upyog.web.models;

import javax.validation.constraints.NotBlank;

import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrackApplicationRequest {
    
    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;
    
    @JsonProperty("appl_ref_no")
    @NotBlank(message = "Application reference number is required")
    private String applRefNo;
    
    @JsonProperty("tenantId")
    @NotBlank(message = "Tenant ID is required")
    private String tenantId;
}
