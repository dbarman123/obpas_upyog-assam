package org.egov.bpa.service.property.gmc;

import lombok.extern.slf4j.Slf4j;
import org.egov.bpa.web.model.property.gmc.GMCPropertyRequest;
import org.egov.bpa.web.model.property.gmc.GMCPropertyResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@Slf4j
public class GMCPropertyRestClient {

    @Value("${gmc.property.service.url}")
    private String baseUrl;

    @Value("${gmc.property.service.username}")
    private String username;

    @Value("${gmc.property.service.password}")
    private String password;

    private final RestTemplate restTemplate = new RestTemplate();

    public GMCPropertyResponse fetchHoldingDetails(GMCPropertyRequest request) {

        String url = baseUrl + "/api/v1/obps/holding/fetch";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // --- BASIC AUTH HEADER ---
        String creds = username + ":" + password;
        String encodedCreds = Base64.getEncoder().encodeToString(creds.getBytes(StandardCharsets.UTF_8));
        headers.set("Authorization", "Basic " + encodedCreds);

        // EXACT BODY EXPECTED BY GMC
        String jsonBody = "{\"holding\": \"" + request.getHolding() + "\"}";

        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

        ResponseEntity<GMCPropertyResponse> response =
                restTemplate.exchange(url, HttpMethod.POST, entity, GMCPropertyResponse.class);

        return response.getBody();
    }
}

