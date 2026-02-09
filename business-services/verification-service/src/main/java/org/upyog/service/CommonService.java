package org.upyog.service;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.upyog.web.models.CommonDetails;
import org.upyog.web.models.ModuleSearchRequest;
import org.upyog.web.models.TrackApplicationRequest;

public interface CommonService {
    CommonDetails getApplicationCommonDetails(@Valid ModuleSearchRequest request);
    
    Object trackApplication(@Valid TrackApplicationRequest request);
}
