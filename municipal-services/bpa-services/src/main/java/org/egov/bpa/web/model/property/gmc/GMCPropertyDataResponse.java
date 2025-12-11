package org.egov.bpa.web.model.property.gmc;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GMCPropertyDataResponse {
    private String holding;

    @JsonProperty("owner_name")
    private String ownerName;

    @JsonProperty("guardian_name")
    private String guardianName;

    private String address;
    private String phone;
    private String ulb;
    private String zone;
    private String ward;

    @JsonProperty("construction_type")
    private List<GMCPropertyConstructionType> constructionType;

    @JsonProperty("tax_paid")
    private Boolean taxPaid;
}

