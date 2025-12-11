package org.egov.bpa.web.model.property.gmc;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GMCPropertyConstructionType {
    @JsonProperty("building_use")
    private String buildingUse;

    @JsonProperty("building_type")
    private String buildingType;

    @JsonProperty("construction_type")
    private String constructionType;

    @JsonProperty("construction_year")
    private String constructionYear;

    @JsonProperty("land_area")
    private String landArea;
}

