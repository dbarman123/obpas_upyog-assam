package org.egov.bpa.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.egov.bpa.config.BPAConfiguration;
import org.egov.bpa.repository.IdGenRepository;
import org.egov.bpa.repository.OCRepository;
import org.egov.bpa.util.BPAErrorConstants;
import org.egov.bpa.util.BPAUtil;
import org.egov.bpa.util.OCErrorConstants;
import org.egov.bpa.web.model.OC;
import org.egov.bpa.web.model.OCRequest;
import org.egov.bpa.web.model.OCSearchCriteria;
import org.egov.bpa.web.model.idgen.IdResponse;
import org.egov.bpa.workflow.WorkflowIntegrator;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.utils.MultiStateInstanceUtil;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OCServiceV2 {

	@Autowired
	private MultiStateInstanceUtil centralInstanceUtil;

	@Autowired
	private MdmsCacheService mdmsCacheService;

	@Autowired
	private BPAUtil util;

	@Autowired
	private WorkflowIntegrator wfIntegrator;

	@Autowired
	private OCRepository repository;

	@Autowired
	private BPAConfiguration config;

	@Autowired
	private IdGenRepository idGenRepository;
	
	@Autowired
	private OCLandService ocLandService;
	
	@Autowired
	private EnrichmentService enrichmentService;

	public OC createOC(OCRequest ocRequest) {
		if (null != ocRequest.getOc()) {

			RequestInfo requestInfo = ocRequest.getRequestInfo();
			String stateTenantId = centralInstanceUtil.getStateLevelTenant(ocRequest.getOc().getTenantId());
			String tenantId = ocRequest.getOc().getAreaMapping().getConcernedAuthority();

			// Get MDMS Data for request validation
			 Object mdmsTenantData = mdmsCacheService.getMdmsData(requestInfo, tenantId);
			 Object mdmsStateTenantData = mdmsCacheService.getMdmsData(requestInfo, stateTenantId);

			 if(centralInstanceUtil.isTenantIdStateLevel(ocRequest.getOc().getTenantId())) {
				 throw new CustomException(OCErrorConstants.INVALID_TENANT, "Application cannot be create at StateLevel");
			 }

//			 Since approval number should be generated at approve stage
			 if(StringUtils.isNotEmpty(ocRequest.getOc().getApprovalNo())) {
				 ocRequest.getOc().setApprovalNo(null);
			 }
//			 this.validateOCCreation(ocRequest, mdmsTenantData, mdmsStateTenantData);
			ocRequest.getOc().setApplicationDate(util.getCurrentTimestampMillis());

			ocLandService.addLandInfoToOC(ocRequest);
			
			enrichmentService.enrichOCCreateRequest(ocRequest, null);
			
			// wfIntegrator.callWorkFlow(ocRequest);
			log.info("OC Request:: {}", ocRequest);
			repository.save(ocRequest);
		}
		return ocRequest.getOc();
	}

	public OC updateOC(@Valid OCRequest ocRequest) {
		RequestInfo requestInfo = ocRequest.getRequestInfo();
		validateUpdateOC(ocRequest);
		return null;
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
		if(StringUtils.isNotEmpty(ocRequest.getOc().getId())) {
			throw new CustomException(BPAErrorConstants.UPDATE_ERROR, "Application Not found in the System to Update");	
		}
		
		getOcByOcId(ocRequest);
	}

	private List<OC> getOcByOcId(@Valid OCRequest ocRequest) {

		OCSearchCriteria criteria = OCSearchCriteria.builder().ids(Collections.singletonList(ocRequest.getOc().getId()))
				.tenantId(ocRequest.getOc().getTenantId()).build();

//		List<OC> oc = repository.getOCDetail(criteria);
		return null;
	}

}
