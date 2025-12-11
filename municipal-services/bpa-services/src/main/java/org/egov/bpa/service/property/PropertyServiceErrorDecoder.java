package org.egov.bpa.service.property;


import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.egov.bpa.exception.PropertyNotFoundException;
import org.egov.bpa.exception.PropertyServiceException;

import java.io.IOException;

@Slf4j
public class PropertyServiceErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {

        String body = null;
        try {
            if (response.body() != null) {
                body = Util.toString(response.body().asReader());
            }
        } catch (IOException e) {
            body = "Unable to read response body: " + e.getMessage();
        }

        log.info("Feign Error → Status: " + response.status());
        log.info("Feign Error → Method: " + methodKey);
        log.info("Feign Error → Body: " + body);

        switch (response.status()) {
            case 404:
                return new PropertyNotFoundException("Property not found. Body: " + body);
            case 400:
                return new PropertyServiceException("Bad request - Invalid property number. Body: " + body);
            case 401:
                return new PropertyServiceException("Authentication failed. Body: " + body);
            case 500:
                return new PropertyServiceException("Internal server error. Body: " + body);
            default:
                return new RuntimeException("Unhandled error: " + body);
        }
    }

}

