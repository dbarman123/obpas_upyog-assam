package org.egov.bpa.web.controller;

import java.util.Collections;
import java.util.List;

import javax.validation.Valid;

import org.egov.bpa.service.OCServiceV2;
import org.egov.bpa.util.ResponseInfoFactory;
import org.egov.bpa.web.model.BPA;
import org.egov.bpa.web.model.BPAResponse;
import org.egov.bpa.web.model.OC;
import org.egov.bpa.web.model.OCRequest;
import org.egov.bpa.web.model.OCResponse;
import org.egov.bpa.web.model.OCSearchCriteria;
import org.egov.bpa.web.model.RequestInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/oc")
@Slf4j
public class OCController {

	@Autowired
	private OCServiceV2 ocServiceV2;

	@Autowired
	private ResponseInfoFactory responseInfoFactory;

	@PostMapping(value = "/_create")
	public ResponseEntity<?> create(@Valid @RequestBody OCRequest ocRequest) {
//		bpaUtil.defaultJsonPathConfig();
		try {
			log.info("LandInfo details:: {}", new ObjectMapper().writeValueAsString(ocRequest.getOc().getLandInfo()));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		OC oc = ocServiceV2.createOC(ocRequest);
		OCResponse response = OCResponse.builder().ocs(Collections.singletonList(oc))
				.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(ocRequest.getRequestInfo(), true))
				.build();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PostMapping(value = "/_update")
	public ResponseEntity<?> update(@Valid @RequestBody OCRequest ocRequest) {
//		bpaUtil.defaultJsonPathConfig();
		OC oc = ocServiceV2.updateOC(ocRequest);
		OCResponse response = OCResponse.builder().ocs(Collections.singletonList(oc))
				.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(ocRequest.getRequestInfo(), true))
				.build();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	

	@PostMapping(value = "/_search")
	public ResponseEntity<?> search(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
			@Valid @ModelAttribute OCSearchCriteria criteria) {

		List<OC> ocList = ocServiceV2.searchOC(criteria, requestInfoWrapper.getRequestInfo());
		//int count = ocServiceV2.getOCCount(criteria, requestInfoWrapper.getRequestInfo());
		OCResponse response = OCResponse.builder().ocs(ocList).responseInfo(
				responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
				.build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
