package org.upyog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
public class ReportServiceConfig {
    
    @Value("${report.service.host}")
    private String reportServiceHost;
    
    @Value("${report.service.endpoint}")
    private String reportServiceEndpoint;
}
