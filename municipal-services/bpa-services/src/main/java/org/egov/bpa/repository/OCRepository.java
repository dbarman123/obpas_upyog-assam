package org.egov.bpa.repository;

import java.util.ArrayList;
import java.util.List;

import org.egov.bpa.config.BPAConfiguration;
import org.egov.bpa.producer.Producer;
import org.egov.bpa.web.model.BPA;
import org.egov.bpa.web.model.OCRequest;
import org.egov.bpa.web.model.OCSearchCriteria;
import org.egov.common.exception.InvalidTenantIdException;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class OCRepository {

	@Autowired
	private Producer producer;

	@Autowired
	private BPAConfiguration config;

	public void save(OCRequest ocRequest) {
		producer.push(ocRequest.getOc().getTenantId(), config.getSaveOcTopic(), ocRequest);
	}
	
//	public List<BPA> getOCDetail(OCSearchCriteria criteria) {
//		List<Object> preparedStmtList = new ArrayList<>();
//		String query = queryBuilder.getBPADetailSearchQuery(criteria, preparedStmtList, edcrNos, false);
//		try {
//			query = centralInstanceUtil.replaceSchemaPlaceholder(query, criteria.getTenantId());
//			log.info("getBPADetailData query : {} and preparedStmtList : {}", query, preparedStmtList);
//		} catch (InvalidTenantIdException e) {
//			throw new CustomException("EG_PT_TENANTID_ERROR",
//					"TenantId length is not sufficient to replace query schema in a multi state instance");
//		}
//		List<BPA> BPAData = jdbcTemplate.query(query, preparedStmtList.toArray(), rowMapper);
//		return BPAData;
//	}

}
