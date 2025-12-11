package org.egov.bpa.web.model.property.gmc;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GMCPropertyResponse {
    private Integer status;
    private String message;
    private GMCPropertyDataResponse data;
    private List<String> errors;
}

