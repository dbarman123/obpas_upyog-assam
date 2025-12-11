package org.egov.bpa.config;

import feign.Logger;
import feign.RequestInterceptor;
import feign.auth.BasicAuthRequestInterceptor;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.egov.bpa.service.property.PropertyServiceErrorDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
@Slf4j
public class GMCFeignConfig {

    @Bean(name = "gmcLogger")
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean(name = "gmcErrorDecoder")
    public ErrorDecoder errorDecoder() {
        return new PropertyServiceErrorDecoder();
    }

    @Bean(name = "gmcRequestInterceptor")
    public RequestInterceptor requestInterceptor(
            @Value("${gmc.property.service.username}") String username,
            @Value("${gmc.property.service.password}") String password) {
        log.info("GMC Feign → Adding Basic Auth Header");

        return template -> {
            String creds = username + ":" + password;
            String encoded = Base64.getEncoder().encodeToString(creds.getBytes(StandardCharsets.UTF_8));
            template.header("Authorization", "Basic " + encoded);
            template.header("Content-Type", "application/json");
        };
    }
}

