package org.egov.bpa.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.egov.bpa.config.BPAConfiguration;
import org.egov.bpa.repository.IdGenRepository;
import org.egov.bpa.repository.OCRepository;
import org.egov.bpa.repository.ServiceRequestRepository;
import org.egov.bpa.util.BPAErrorConstants;
import org.egov.bpa.util.BPAUtil;
import org.egov.bpa.util.OCErrorConstants;
import org.egov.bpa.web.model.AuditDetails;
import org.egov.bpa.web.model.BPA;
import org.egov.bpa.web.model.BPASearchCriteria;
import org.egov.bpa.web.model.OC;
import org.egov.bpa.web.model.OCRequest;
import org.egov.bpa.web.model.OCSearchCriteria;
import org.egov.bpa.web.model.RequestInfoWrapper;
import org.egov.bpa.web.model.Workflow;
import org.egov.bpa.web.model.idgen.IdResponse;
import org.egov.bpa.web.model.landInfo.LandInfo;
import org.egov.bpa.web.model.landInfo.LandSearchCriteria;
import org.egov.bpa.web.model.workflow.BusinessService;
import org.egov.bpa.workflow.WorkflowIntegrator;
import org.egov.bpa.workflow.WorkflowService;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.utils.MultiStateInstanceUtil;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OCServiceV2 {

	@Autowired
	private MultiStateInstanceUtil centralInstanceUtil;

	@Autowired
	private BPAUtil util;

	@Autowired
	private OCRepository ocRepository;

	@Autowired
	private BPAConfiguration config;

	@Autowired
	private IdGenRepository idGenRepository;

	@Autowired
	private OCLandService ocLandService;

	@Autowired
	private EnrichmentService enrichmentService;

	@Autowired
	private BPAService bpaService;


	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private MdmsCacheService mdmsCacheService;
	
	@Autowired
    private WorkflowIntegrator wfIntegrator;
	
	@Autowired
	private WorkflowService workflowService;


	public OC createOC(OCRequest ocRequest) {
		if (null != ocRequest.getOc()) {

			RequestInfo requestInfo = ocRequest.getRequestInfo();
			String stateTenantId = centralInstanceUtil.getStateLevelTenant(ocRequest.getOc().getTenantId());
//			 String tenantId = ocRequest.getOc().getAreaMapping().getConcernedAuthority();

			 // Get MDMS Data for request validation
//			 Object mdmsTenantData = mdmsCacheService.getMdmsData(requestInfo, tenantId);
//			 Object mdmsStateTenantData = mdmsCacheService.getMdmsData(requestInfo, stateTenantId);

			if (centralInstanceUtil.isTenantIdStateLevel(ocRequest.getOc().getTenantId())) {
				throw new CustomException(OCErrorConstants.INVALID_TENANT,
						"Application cannot be create at StateLevel");
			}

			//this.validateOCCreation(ocRequest, mdmsTenantData, mdmsStateTenantData);
			ocRequest.getOc().setApplicationDate(util.getCurrentTimestampMillis());

			ocLandService.addLandInfoToOC(ocRequest);

			enrichmentService.enrichOCCreateRequest(ocRequest, null);

			log.info("OC Request:: {}", ocRequest);
			ocRepository.save(ocRequest);
			
			// wfIntegrator.callWorkFlow(ocRequest);
			wfIntegrator.callWorkFlowForOc(ocRequest);
		}
		return ocRequest.getOc();
	}

	public OC updateOC(@Valid OCRequest ocRequest) {

		validateUpdateOC(ocRequest);

		OC bpa = ocRequest.getOc();

		BusinessService businessService = workflowService.getBusinessService(bpa.getTenantId(),bpa.getBusinessService(), ocRequest.getRequestInfo(),
				bpa.getApplicationNo());

		OC existingOc = getOcWithOcId(ocRequest);

		enrichOCUpdateRequest(ocRequest);

		ocRepository.update(ocRequest);

		//workflow
		ocRequest.getOc().setAuditDetails(existingOc.getAuditDetails());

		String action = Optional.ofNullable(bpa.getWorkflow()).map(Workflow::getAction).orElse("");

		switch (action.toUpperCase()) {

		case "FORWARD":
			enrichOCUpdateRequest(ocRequest);
			wfIntegrator.callWorkFlowForOc(ocRequest);
			ocRepository.update(ocRequest);
			//landService.updateLandInfo(bpaRequest);
			break;

		default:
			enrichOCUpdateRequest(ocRequest);
			wfIntegrator.callWorkFlowForOc(ocRequest);
			ocRepository.update(ocRequest);
			break;
		}

		return ocRequest.getOc();
	}

	public void setIdgenIds(OCRequest request) {
		RequestInfo requestInfo = request.getRequestInfo();
		String tenantId = util.extractState(request.getOc().getTenantId());

		List<String> applicationNumbers = getIdList(requestInfo, tenantId, config.getApplicationNoIdgenName(), null, 1);
		ListIterator<String> itr = applicationNumbers.listIterator();

		Map<String, String> errorMap = new HashMap<>();

		if (!errorMap.isEmpty())
			throw new CustomException(errorMap);

		request.getOc().setApplicationNo(itr.next());

	}

	private List<String> getIdList(RequestInfo requestInfo, String tenantId, String idKey, String idformat, int count) {
		List<IdResponse> idResponses = idGenRepository.getId(requestInfo, tenantId, idKey, idformat, count)
				.getIdResponses();

		if (CollectionUtils.isEmpty(idResponses))
			throw new CustomException(BPAErrorConstants.IDGEN_ERROR, "No ids returned from idgen Service");

		return idResponses.stream().map(IdResponse::getId).collect(Collectors.toList());
	}

	private void validateUpdateOC(@Valid OCRequest ocRequest) {
		if (!StringUtils.hasText(ocRequest.getOc().getId())) {
			throw new CustomException(OCErrorConstants.UPDATE_ERROR, "Application Not found in the System to Update");
		}
		getOcWithOcId(ocRequest);
	}

	private OC getOcWithOcId(@Valid OCRequest ocRequest) {

		OCSearchCriteria criteria = OCSearchCriteria.builder().id(ocRequest.getOc().getId())
				.tenantId(ocRequest.getOc().getTenantId()).build();

		List<OC> ocSearchResult = ocRepository.search(criteria);

		if (CollectionUtils.isEmpty(ocSearchResult) || ocSearchResult.size() > 1) {
			throw new CustomException(OCErrorConstants.UPDATE_ERROR, 
					"Failed to Update the Application, Found None or multiple applications!");
		}
		return  ocSearchResult.get(0);
	}

	public List<OC> searchOC(OCSearchCriteria criteria, RequestInfo requestInfo) {

		List<OC> ocList = new ArrayList<>();

		// Direct search from oc tbl
		if (StringUtils.hasText(criteria.getApplicationNo())
				|| StringUtils.hasText(criteria.getOccupancyCertificateNo())
				|| StringUtils.hasText(criteria.getTenantId())
				|| StringUtils.hasText(criteria.getBpaApplicationNo())) {

			ocList = ocRepository.search(criteria);
			enrichOCWithLandInfo(ocList, criteria.getTenantId(), requestInfo);
			return ocList;
		}

		// Search by mobile number
		if (StringUtils.hasText(criteria.getMobileNumber())) {

			ocList = ocRepository.searchByMobileNumber(criteria);
			enrichOCWithLandInfo(ocList, criteria.getTenantId(), requestInfo);
			return ocList;
		}

		return ocList;
	}

	private void enrichOCWithLandInfo(List<OC> ocList, String tenantId,
			RequestInfo requestInfo) {

		if (CollectionUtils.isEmpty(ocList)) {
			return;
		}

		Map<String, BPA> bpaMap = new HashMap<>();
		Map<String, LandInfo> landMap = new HashMap<>();

		for (OC oc : ocList) {

			// OC created from BPA
			if (StringUtils.hasText(oc.getBpaApplicationNo())) {
				BPA bpa = bpaMap.get(oc.getBpaApplicationNo());
				if (bpa == null) {
					BPASearchCriteria bpaCriteria = BPASearchCriteria.builder()
							.tenantId(tenantId)
							.applicationNo(oc.getBpaApplicationNo())
							.build();

					List<BPA> bpaList = bpaService.search(bpaCriteria, requestInfo);
					if (!CollectionUtils.isEmpty(bpaList)) {
						bpa = bpaList.get(0);
						bpaMap.put(oc.getBpaApplicationNo(), bpa);
					}
				}

				if (bpa != null && bpa.getLandInfo() != null) {
					oc.setLandInfo(bpa.getLandInfo());
					oc.setLandId(bpa.getLandInfo().getId());
				}
			}
			// Manual OC → landInfo fetch from Land service
			else {
				if (!StringUtils.hasText(oc.getLandId())) {
					continue;
				}

				LandInfo landInfo = landMap.get(oc.getLandId());
				if (landInfo == null) {
					landInfo = searchLandById(tenantId, oc.getLandId(),	requestInfo);

					if (landInfo == null) {
						log.warn("No LandInfo found by landId: {}", oc.getLandId());
						continue;
					}
					landMap.put(oc.getLandId(), landInfo);
				}
				oc.setLandInfo(landInfo);
			}
		}
	}

	public LandInfo searchLandById(String tenantId,	String landId,
			RequestInfo requestInfo) {

		LandSearchCriteria landCriteria = new LandSearchCriteria();
		landCriteria.setTenantId(tenantId);
		landCriteria.setIds(Collections.singletonList(landId));

		StringBuilder url = getLandSerchURLWithParams(requestInfo, landCriteria);

		RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();

		LinkedHashMap responseMap = (LinkedHashMap) serviceRequestRepository.fetchResult(url, requestInfoWrapper);

		if (responseMap == null || responseMap.get("LandInfo") == null) {
			return null;
		}
		ArrayList<?> landInfo = (ArrayList<?>) responseMap.get("LandInfo");

		if (CollectionUtils.isEmpty(landInfo)) {
			return null;
		}

		return mapper.convertValue(landInfo.get(0), LandInfo.class);
	}

	private StringBuilder getLandSerchURLWithParams(RequestInfo requestInfo, LandSearchCriteria landcriteria) {
		StringBuilder uri = new StringBuilder(config.getLandInfoHost());
		uri.append(config.getLandInfoSearch());
		uri.append("?tenantId=");
		uri.append(landcriteria.getTenantId());
		//		LandSearchCriteria landSearchCriteria = new LandSearchCriteria();
		//		LandInfoRequest landRequest = new LandInfoRequest();
		//		landRequest.setRequestInfo(requestInfo);
		if (landcriteria.getIds() != null) {
			//			landSearchCriteria.setIds(landcriteria.getIds());
			uri.append("&").append("ids=");
			for (int i = 0; i < landcriteria.getIds().size(); i++) {
				if (i != 0) {
					uri.append(",");
				}
				uri.append(landcriteria.getIds().get(i));
			}
		} else if (landcriteria.getMobileNumber() != null) {
			//			landSearchCriteria.setMobileNumber(landcriteria.getMobileNumber());
			uri.append("&").append("mobileNumber=");
			uri.append(landcriteria.getMobileNumber());
		}
		return uri;
	}

	private void enrichOCUpdateRequest(@Valid OCRequest ocRequest) {
		
		RequestInfo requestInfo = ocRequest.getRequestInfo();
		AuditDetails auditDetails = util.getAuditDetails(requestInfo.getUserInfo().getUuid(), false);
		auditDetails.setCreatedBy(ocRequest.getOc().getAuditDetails().getCreatedBy());
		auditDetails.setCreatedTime(ocRequest.getOc().getAuditDetails().getCreatedTime());
		ocRequest.getOc().getAuditDetails().setLastModifiedTime(auditDetails.getLastModifiedTime());
	}
}
