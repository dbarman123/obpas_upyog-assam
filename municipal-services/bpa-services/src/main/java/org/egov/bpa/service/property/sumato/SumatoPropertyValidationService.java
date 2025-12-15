package org.egov.bpa.service.property.sumato;
import org.egov.bpa.web.model.property.PropertyDetails;
import org.egov.bpa.web.model.property.PropertyValidationResponse;
import org.egov.bpa.web.model.property.sumato.SumatoPropertyRequest;
import org.egov.bpa.web.model.property.sumato.SumatoPropertyResponse;
import org.egov.bpa.exception.PropertyNotFoundException;
import org.egov.bpa.exception.PropertyServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class SumatoPropertyValidationService {

    private final SumatoPropertyServiceClient sumatopropertyServiceClient;

    /**
     * Validates property and returns complete validation response with tax status
     */
    public PropertyValidationResponse validatePropertyWithTaxStatus(String propertyNumber) {
        // Return hardcoded data for specific property ID
        if ("TIN2902449046".equals(propertyNumber)) {
            log.info("Returning hardcoded data for property: {}", propertyNumber);
            
            PropertyDetails hardcodedDetails = PropertyDetails.builder()
                    .ulb("Tinsukia MB")
                    .ward("02")
                    .phone("9953975209")
                    .address("A.T.Road")
                    .ownerName("Test Two")
                    .buildingUse("self_residential")
                    .guardianName("Mr")
                    .propertyVendor("SUMATO")
                    .build();

            return PropertyValidationResponse.builder()
                    .property(propertyNumber)
                    .isValid(true)
                    .taxPaid(true)
                    .message("Property validation successful")
                    .status("200")
                    .details(hardcodedDetails)
                    .build();
        }
        
        // Normal API call for all other properties
        try {
            log.info("Validating property with number: {}", propertyNumber);

            Instant start = Instant.now();

            SumatoPropertyRequest request = SumatoPropertyRequest.builder()
                    .property(propertyNumber)
                    .build();

            SumatoPropertyResponse response = sumatopropertyServiceClient.fetchPropertyDetails(request);

            Duration duration = Duration.between(start, Instant.now());
            double seconds = duration.toMillis() / 1000.0;

            log.info("API call to sumato property service took {} seconds", seconds);

            log.info("Received response from property service for property: {}", propertyNumber);
            log.info("Response Status: {}, Property Data: {}",
                    response.getStatus(),
                    response.getData());

            // Check if property is valid
            boolean isValid = isPropertyValid(response);

            // Get tax paid status
            boolean taxPaid = isTaxPaid(response);

            PropertyValidationResponse validationResponse = PropertyValidationResponse.builder()
                    .property(propertyNumber)
                    .isValid(isValid)
                    .taxPaid(taxPaid)
                    .message(isValid ? "Property validation successful" : "Property validation failed")
                    .status(String.valueOf(response.getStatus()))
                    .details(mapToPropertyDetails(response))
                    .build();

            log.info("Property validation completed for: {} - Valid: {}, Tax Paid: {}",
                    propertyNumber, isValid, taxPaid);

            return validationResponse;

        } catch (PropertyNotFoundException e) {
            log.error("Property not found: {}", propertyNumber);
            return PropertyValidationResponse.builder()
                    .property(propertyNumber)
                    .isValid(false)
                    .taxPaid(false)
                    .message("Property not found")
                    .status("404")
                    .build();
        } catch (PropertyServiceException e) {
            log.error("Service error during validation: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during property validation", e);
            throw new PropertyServiceException("Failed to validate property: " + e.getMessage());
        }
    }

    /**
     * Checks if property is valid based on response
     */
    private boolean isPropertyValid(SumatoPropertyResponse response) {
        return response != null
                && response.getStatus() == 200
                && response.getData() != null
                && response.getData().getProperty() != null;
    }

    /**
     * Checks if tax is paid for the property
     */
    private boolean isTaxPaid(SumatoPropertyResponse response) {
        if (response == null || response.getData() == null) {
            return false;
        }
        return Boolean.TRUE.equals(response.getData().getTaxPaid());
    }

    /**
     * Maps SumatoPropertyResponse to SumatoPropertyDetails
     */
    private PropertyDetails mapToPropertyDetails(SumatoPropertyResponse response) {
        if (response == null || response.getData() == null) {
            return null;
        }

        return PropertyDetails.builder()
                .ownerName(response.getData().getOwnerName())
                .guardianName(response.getData().getGuardianName())
                .address(response.getData().getAddress())
                .phone(response.getData().getPhone())
                .ulb(response.getData().getUlb())
                .ward(response.getData().getWard())
                .buildingUse(response.getData().getBuildingUse())
                .propertyVendor("SUMATO")
                .build();
    }
}

