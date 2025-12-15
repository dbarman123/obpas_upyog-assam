package org.egov.bpa.web.model.property.gmc;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GMCPropertyRequest {
    @JsonProperty("holding")
    private String holding;
}
