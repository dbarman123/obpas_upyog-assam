import { PrivateRoute, BreadCrumb, BackButton, CloseSvg } from "@upyog/digit-ui-react-components";
import React, { Fragment } from "react";
import { Switch, useLocation } from "react-router-dom";
import { useTranslation } from "react-i18next";
import Search from "../citizen/Search";
import EnhancedReport from "../../pageComponents/EnhancedReport";


const EmployeeApp = ({ path }) => {
  const location = useLocation()
  const { t } = useTranslation();
  const Inbox = Digit.ComponentRegistryService.getComponent("OBPSV2Inbox");
  const RTPInbox = Digit.ComponentRegistryService.getComponent("RTPInbox");
  const BPAApplicationDetails = Digit?.ComponentRegistryService?.getComponent("BPAEmployeeDetails");
  console.log("testtststststtss",path)

  return (
    <Fragment>
      {/* {!isFromNoc && !isRes ? <div style={isLocation ? {marginLeft: "10px"} : {}}><OBPSBreadCrumbs location={location} /></div> : null}
      {isFromNoc ? <BackButton style={{ border: "none", margin: "0", padding: "0" }}>{t("CS_COMMON_BACK")}</BackButton>: null} */}
      <Switch>
        
        <PrivateRoute path={`${path}/inbox`} component={(props) => <Inbox {...props} parentRoute={path} />} />
         <PrivateRoute path={`${path}/application/:acknowledgementIds/:tenantId`} component={BPAApplicationDetails}></PrivateRoute>
        <PrivateRoute path={`${path}/rtp/inbox`} component={(props) => <RTPInbox {...props} parentRoute={path} />} />
        <PrivateRoute path={`${path}/search/application`} component={(props) => <Search {...props} parentRoute={path} />} />
        <PrivateRoute path={`${path}/obpsApplicationReport`} component={(props) => <EnhancedReport {...props} parentRoute={path} moduleName="rainmaker-obps" reportName="obpsApplicationReport" />} />
        <PrivateRoute path={`${path}/obpsRegistryReport`} component={(props) => <EnhancedReport {...props} parentRoute={path} moduleName="rainmaker-obps" reportName="obpsRegistryReport" />} />
        <PrivateRoute path={`${path}/totalapplicationssummary`} component={(props) => <EnhancedReport {...props} parentRoute={path} moduleName="rainmaker-obps" reportName="totalapplicationssummary" />} />
        <PrivateRoute path={`${path}/newapplicationsdatewise`} component={(props) => <EnhancedReport {...props} parentRoute={path} moduleName="rainmaker-obps" reportName="newapplicationsdatewise" />} />
        <PrivateRoute path={`${path}/dailybackendupdatesresport`} component={(props) => <EnhancedReport {...props} parentRoute={path} moduleName="rainmaker-obps" reportName="dailybackendupdatesresport" />} />
        <PrivateRoute path={`${path}/applicationworkflowtrace`} component={(props) => <EnhancedReport {...props} parentRoute={path} moduleName="rainmaker-obps" reportName="applicationworkflowtrace" />} />
      
      </Switch>
    </Fragment>
  )
}

export default EmployeeApp;