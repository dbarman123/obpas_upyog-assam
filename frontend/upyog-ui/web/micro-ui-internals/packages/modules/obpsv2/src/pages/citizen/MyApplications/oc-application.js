import { Card, KeyNote, SubmitBar, CardSubHeader } from "@upyog/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";

const OCApplication = ({ application, tenantId, buttonLabel }) => {
  const { t } = useTranslation();

  return (
    <Card> 
      <KeyNote keyValue={t("OC_APPLICATION_NO")} note={application?.applicationNo} />
      <KeyNote keyValue={t("OC_TYPE_OF_CONSTRUCTION")} note={t(application?.applicationType)} />
      <KeyNote keyValue={t("OCCUPANCY_TYPE")} note={t(application?.landInfo?.units[0]?.occupancyType)} />
      <KeyNote keyValue={t("PT_COMMON_TABLE_COL_STATUS_LABEL")} note={t(application?.status)} />
      
      {application?.slaDaysRemaining && (
        <KeyNote 
          keyValue={t("OC_SLA_DAYS_REMAINING")} 
          note={`${application.slaDaysRemaining} ${t("OC_DAYS")} ${t("OC_SLA_DISCLAIMER")}`} 
        />
      )}
      <Link to={`/upyog-ui/citizen/obpsv2/application/${application?.applicationNo}/${application?.tenantId}`}>
        <SubmitBar label={buttonLabel} />
      </Link>
    </Card>
  );
};

export default OCApplication;