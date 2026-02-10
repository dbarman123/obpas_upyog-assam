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

@Service
@Slf4j
public class ReportServiceImpl {

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private CommonServiceImpl commonService;

	@Autowired
	private MainConfiguration mainConfiguration;

	public Object trackApplication(TrackApplicationRequest request) {
		try {
			String applRefNo = request.getApplRefNo();
			String tenantId = request.getTenantId();
			RequestInfo requestInfo = request.getRequestInfo();

			log.info("Tracking application: {} for tenant: {}", applRefNo, tenantId);

			String reportServiceUrl = mainConfiguration.getReportServiceHost()
					+ mainConfiguration.getReportServiceEndpoint();

			log.info("Calling Report Service at: {}", reportServiceUrl);

			Map<String, Object> requestBody = new HashMap<>();
			requestBody.put("RequestInfo", requestInfo != null ? requestInfo : commonService.getSystemUserDetails());
			requestBody.put("appl_ref_no", applRefNo);
			requestBody.put("tenantId", tenantId);

			Object result = serviceRequestRepository.fetchResult(new StringBuilder(reportServiceUrl), requestBody);

			log.info("Successfully fetched application data from Report Service");
			return result;

		} catch (Exception e) {
			log.error("Error tracking application: {}", request.getApplRefNo(), e);
			throw new CustomException("TRACK_APPLICATION_ERROR", "Error tracking application: " + e.getMessage());
		}
	}
}
