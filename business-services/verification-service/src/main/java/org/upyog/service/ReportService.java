package org.upyog.service;

import java.util.HashMap;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upyog.config.MainConfiguration;
import org.upyog.repository.ServiceRequestRepository;
import org.upyog.web.models.TrackApplicationRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * Service class for tracking applications by fetching data from Report Service.
 * This service acts as a bridge between the verification service and the report service.
 */
@Service
@Slf4j
public class ReportService {

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private CommonServiceImpl commonService;

	@Autowired
	private MainConfiguration mainConfiguration;

	/**
	 * Tracks an application by fetching complete application data from Report Service.
	 * If user is not logged in (RequestInfo is null), system user credentials are used.
	 * 
	 * @param request TrackApplicationRequest containing application reference number and tenant ID
	 * @return Object containing complete application data from Report Service in raw format
	 * @throws CustomException if there is an error while fetching application data
	 */
	public Object trackApplication(TrackApplicationRequest request) {
		try {
			// Extract request parameters
			String applRefNo = request.getApplRefNo();
			String tenantId = request.getTenantId();
			RequestInfo requestInfo = request.getRequestInfo();

			log.info("Tracking application: {} for tenant: {}", applRefNo, tenantId);

			// Construct Report Service URL from configuration
			String reportServiceUrl = mainConfiguration.getReportServiceHost()
					+ mainConfiguration.getReportServiceEndpoint();

			log.info("Calling Report Service at: {}", reportServiceUrl);

			// Build request body for Report Service
			Map<String, Object> requestBody = new HashMap<>();
			// Use provided RequestInfo or fallback to system user if not available
			requestBody.put("RequestInfo", requestInfo != null ? requestInfo : commonService.getSystemUserDetails());
			requestBody.put("appl_ref_no", applRefNo);
			requestBody.put("tenantId", tenantId);

			// Call Report Service and fetch application data
			Object result = serviceRequestRepository.fetchResult(new StringBuilder(reportServiceUrl), requestBody);

			log.info("Successfully fetched application data from Report Service");
			return result;

		} catch (Exception e) {
			log.error("Error tracking application: {}", request.getApplRefNo(), e);
			throw new CustomException("TRACK_APPLICATION_ERROR", "Error tracking application: " + e.getMessage());
		}
	}
}
