package org.upyog.gis.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.request.RequestInfo;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GisLogSearchRequest {

    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    @JsonProperty("GisSearchCriteria")
    private GisLogSearchCriteria criteria;
}
