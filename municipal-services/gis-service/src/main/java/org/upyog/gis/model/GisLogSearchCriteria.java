package org.upyog.gis.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * Search criteria model for querying GIS processing logs from the database.
 * 
 * <p>This class encapsulates filter parameters used to search GIS logs generated during
 * KML file processing operations. It supports filtering by application number, RTPI ID,
 * processing status, and tenant ID, along with pagination parameters.</p>
 * 
 * <p>The tenantId field is mandatory and must be provided for all search operations.
 * Other filter parameters (applicationNo, rtpId, status) are optional and can be used
 * in combination to narrow down search results.</p>
 * 
 *
 *
 * @see GisLog
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GisLogSearchCriteria {

    private String applicationNo;
    private String rtpId;
    private String status;

    @NotNull
    private String tenantId;
    private Integer offset;
    private Integer limit;

}
