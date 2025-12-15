package org.egov.bpa.service.property.gmc;

import org.egov.bpa.config.GMCFeignConfig;
import org.egov.bpa.web.model.property.gmc.GMCPropertyRequest;
import org.egov.bpa.web.model.property.gmc.GMCPropertyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "gmc-property-service",
        url = "${gmc.property.service.url}",
        configuration = GMCFeignConfig.class
)
public interface GMCPropertyServiceClient {

    @PostMapping(
            value = "/api/v1/obps/holding/fetch",
            consumes = "application/json",
            produces = "application/json"
    )
    GMCPropertyResponse fetchPropertyDetails(@RequestBody GMCPropertyRequest request);
}



