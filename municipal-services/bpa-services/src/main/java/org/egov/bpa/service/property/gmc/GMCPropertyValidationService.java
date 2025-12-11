package org.egov.bpa.service.property.gmc;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egov.bpa.exception.PropertyNotFoundException;
import org.egov.bpa.exception.PropertyServiceException;
import org.egov.bpa.web.model.property.PropertyDetails;
import org.egov.bpa.web.model.property.PropertyValidationResponse;
import org.egov.bpa.web.model.property.gmc.GMCPropertyConstructionType;
import org.egov.bpa.web.model.property.gmc.GMCPropertyDataResponse;
import org.egov.bpa.web.model.property.gmc.GMCPropertyRequest;
import org.egov.bpa.web.model.property.gmc.GMCPropertyResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GMCPropertyValidationService {
    private final GMCPropertyRestClient gmcPropertyRestClient;


    public PropertyValidationResponse validateProperty(String propertyNumber) {
        try {
            log.info("Validating property in Guwahati service: {}", propertyNumber);

            GMCPropertyRequest request = GMCPropertyRequest.builder()
                    .holding(propertyNumber)
                    .build();

            GMCPropertyResponse response = gmcPropertyRestClient.fetchHoldingDetails(request);

            boolean isValid = response != null
                    && response.getStatus() == 200
                    && response.getData() != null;


            boolean taxPaid = isValid && Boolean.TRUE.equals(response.getData().getTaxPaid());

            PropertyDetails details = null;
            String cityName = null;

            if (isValid) {
                GMCPropertyDataResponse data = response.getData();
                cityName = data.getUlb();

                // Get construction type from first item
                String constructionType = null;
                String buildingUse = null;
                if (data.getConstructionType() != null && !data.getConstructionType().isEmpty()) {
                    GMCPropertyConstructionType construction = data.getConstructionType().get(0);
                    constructionType = construction.getConstructionType();
                    buildingUse = construction.getBuildingUse();
                }

                details = PropertyDetails.builder()
                        .ownerName(data.getOwnerName())
                        .guardianName(data.getGuardianName())
                        .address(data.getAddress())
                        .phone(data.getPhone())
                        .ulb(cityName)
                        .ward(data.getWard())
                        .buildingUse(buildingUse)
                        .propertyVendor("GMC")
                        .build();

                // Add zone to address if present
                if (data.getZone() != null && !data.getZone().isEmpty()) {
                    String fullAddress = data.getAddress() + " (Zone: " + data.getZone() + ")";
                    details.setAddress(fullAddress);
                }
            }

            return PropertyValidationResponse.builder()
                    .property(propertyNumber)
                    .isValid(isValid)
                    .taxPaid(taxPaid)
                    .message(isValid ? "Property validation successful" : "Property validation failed")
                    .details(details)
                    .build();

        } catch (PropertyNotFoundException e) {
            log.error("Property not found in Guwahati: {}", propertyNumber);
            return propertyNotFoundResponse(propertyNumber);
        } catch (Exception e) {
            log.error("Error validating property in Guwahati: {}", e.getMessage(), e);
            throw new PropertyServiceException("Failed to validate property: " + e.getMessage());
        }
    }

    private PropertyValidationResponse propertyNotFoundResponse(String propertyNumber) {
        return PropertyValidationResponse.builder()
                .property(propertyNumber)
                .isValid(false)
                .taxPaid(false)
                .message("Holding not found")
                .build();
    }
}
