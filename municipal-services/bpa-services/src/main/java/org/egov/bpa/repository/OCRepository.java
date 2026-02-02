package org.egov.bpa.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.egov.bpa.config.BPAConfiguration;
import org.egov.bpa.producer.Producer;
import org.egov.bpa.repository.querybuilder.OCQueryBuilder;
import org.egov.bpa.repository.rowmapper.OCDetailsRowMapper;
import org.egov.bpa.util.BPAConstants;
import org.egov.bpa.web.model.BPARequest;
import org.egov.bpa.web.model.OC;
import org.egov.bpa.web.model.OCRequest;
import org.egov.bpa.web.model.OCSearchCriteria;
import org.egov.common.exception.InvalidTenantIdException;
import org.egov.common.utils.MultiStateInstanceUtil;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class OCRepository {

	@Autowired
	private Producer producer;

	@Autowired
	private BPAConfiguration config;

	@Autowired
	private OCQueryBuilder queryBuilder;

	@Autowired
	private MultiStateInstanceUtil centralInstanceUtil;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private OCDetailsRowMapper ocRowMapper;

	public void save(OCRequest ocRequest) {
		producer.push(ocRequest.getOc().getTenantId(), config.getSaveOcTopic(), ocRequest);
	}

	public List<OC> search(OCSearchCriteria criteria) {

		List<Object> preparedStmtList = new ArrayList<>();
		StringBuilder query = new StringBuilder(getBaseQuery());

		addWhereClause(query);

		if (StringUtils.hasText(criteria.getTenantId())) {
			query.append(" oc.tenant_id = ? ");
			preparedStmtList.add(criteria.getTenantId());
		}

		if (StringUtils.hasText(criteria.getApplicationNo())) {
			query.append(" AND oc.application_no = ? ");
			preparedStmtList.add(criteria.getApplicationNo());
		}

		if (StringUtils.hasText(criteria.getOccupancyCertificateNo())) {
			query.append(" AND oc.occupancy_certificate_no = ? ");
			preparedStmtList.add(criteria.getOccupancyCertificateNo());
		}

		if (StringUtils.hasText(criteria.getId())) {
			query.append(" AND oc.id = ? ");
			preparedStmtList.add(criteria.getId());
		}

		if (StringUtils.hasText(criteria.getBpaApplicationNo())) {
			query.append(" AND oc.bpa_application_no = ? ");
			preparedStmtList.add(criteria.getBpaApplicationNo());
		}
		query.append(" ORDER BY oc.application_date DESC ");

		log.info("OC search query: {}", query);
		return jdbcTemplate.query(query.toString(), preparedStmtList.toArray(), ocRowMapper);
	}

	public List<OC> searchByMobileNumber(OCSearchCriteria criteria) {

		List<Object> preparedStmtList = new ArrayList<>();
		StringBuilder query = new StringBuilder(getBaseQuery());

		addWhereClause(query);

		if (StringUtils.hasText(criteria.getTenantId())) {
			query.append(" oc.tenant_id = ? ");
			preparedStmtList.add(criteria.getTenantId());
		}

		if (StringUtils.hasText(criteria.getMobileNumber())) {
			query.append(" AND oc.phone_number = ? ");
			preparedStmtList.add(criteria.getMobileNumber());
		}

		query.append(" ORDER BY oc.application_date DESC ");

		log.debug("OC mobile search query: {}", query);
		return jdbcTemplate.query(query.toString(), preparedStmtList.toArray(), ocRowMapper);
	}

	private String getBaseQuery() {
		return " SELECT DISTINCT oc.* FROM ug_oc_details oc ";
	}

	private void addWhereClause(StringBuilder query) {
		query.append(" WHERE ");
	}

	public void update(OCRequest ocRequest) {
		producer.push(ocRequest.getOc().getTenantId(), config.getUpdateOcTopic(), ocRequest);
	}
}
