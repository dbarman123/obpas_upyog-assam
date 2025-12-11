package org.egov.bpa.config;

import feign.codec.ErrorDecoder;
import lombok.Data;
import org.egov.bpa.service.property.PropertyServiceErrorDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SoftThinkFeignConfig {

}
