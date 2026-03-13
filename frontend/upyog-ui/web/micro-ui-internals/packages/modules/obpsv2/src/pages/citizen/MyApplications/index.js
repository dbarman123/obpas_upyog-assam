import React, { useState, useEffect } from "react";
import { Header, Loader, TextInput, Dropdown, SubmitBar, CardLabel, Card } from "@upyog/digit-ui-react-components";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import BPAApplication from "./bpa-application";
import OCApplication from "./oc-application";

export const BPAMyApplications = () => {
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();
  const user = Digit.UserService.getUser().info;

  const [searchTerm, setSearchTerm] = useState("");
  const [shouldSearch, setShouldSearch] = useState(false);
  const [status, setStatus] = useState(null);
  const [applicationType, setApplicationType] = useState(null);
  const [applications, setApplications] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
const [totalCount, setTotalCount] = useState(0);

  const filter = window.location.href.split("/").pop();
  const t1 = !isNaN(parseInt(filter)) ? parseInt(filter) + 50 : 4;
  const off = !isNaN(parseInt(filter)) ? filter : "0";

  const initialFilters = !isNaN(parseInt(filter))
    ? { limit: "50", sortOrder: "ASC", sortBy: "createdTime", offset: off, tenantId }
    : { limit: "4", sortOrder: "ASC", sortBy: "createdTime", offset: "0", tenantId, mobileNumber: user?.mobileNumber };

  const [filters, setFilters] = useState(null);
  const isBPA = applicationType?.code === "BPA";
const isOC = applicationType?.code === "OCCUPANCY";

  // Use the search hook with dynamic filters
  // const { isLoading, data } = Digit.Hooks.obpsv2.useBPASearchApi({ filters });


  const handleApplicationTypeChange = (value) => {
  setApplicationType(value);
    setApplications([]);
  setTotalCount(0);
  setFilters(null);
  setStatus(null);
};


  // const handleSearch = () => {
  //   debugger
  //   const trimmedSearchTerm = searchTerm.trim();
  //   const searchFilters = {
  //     ...initialFilters,
  //     applicationNo: trimmedSearchTerm || undefined,
  //     status: status?.code || undefined,
  //   };

  //   setFilters(searchFilters);
  // };

const handleSearch = async () => {
  if (!applicationType?.code) return;
console.log("filters==",initialFilters)
  setIsLoading(true);
  setApplications([]);
  try {
    // 🔹 BPA SEARCH
    if (applicationType?.code === "BPA") {
  const payload = {
    filters: {
      ...initialFilters,
      applicationNo: searchTerm?.trim() || undefined,
      status: status?.code || undefined,
    },
  };
      const response = await Digit.OBPSV2Services.search(payload);
      setApplications(response?.bpa || []);
      setTotalCount(response?.count || 0);
    }

    // 🔹 OC SEARCH
    if (applicationType?.code === "OCCUPANCY") {
  const payload = {
    filters: {
      ...initialFilters,
      applicationNo: searchTerm?.trim() || undefined,
      status: status?.code || undefined,
    },
  };

      const response = await Digit.OBPSV2Services.ocsearch(payload);
      setApplications(response?.ocs || []);
      setTotalCount(response?.count || 0);
    }
  } catch (error) {
    console.error("Search failed", error);
    setApplications([]);
  } finally {
    setIsLoading(false);
  }
};



  const handleLoadMore = () => {
    const newFilters = {
      limit: "50",
      sortOrder: "ASC", 
      sortBy: "createdTime",
      offset: t1.toString(),
      tenantId,
      mobileNumber: user?.mobileNumber
    };
    setFilters(newFilters);
  };

//   if (isBPA && isBPALoading) return <Loader />;
// if (isOC && isOCLoading) return <Loader />;


  const statusOptions = [
    { i18nKey: "Pending RTP Approval", code: "PENDING_RTP_APPROVAL", value: t("BPA_PENDING_RTP_APPROVAL") },
    { i18nKey: "Edit Application", code: "EDIT_APPLICATION", value: t("BPA_EDIT_APPLICATION") },
    { i18nKey: "GIS Validation", code: "GIS_VALIDATION", value: t("BPA_GIS_VALIDATION") },
    { i18nKey: "Pending For Scrutiny", code: "PENDING_FOR_SCRUTINY", value: t("BPA_PENDING_FOR_SCRUTINY") },
    { i18nKey: "Send To Citizen", code: "SEND_TO_CITIZEN", value: t("BPA_SEND_TO_CITIZEN") },
    { i18nKey: "Citizen Approval", code: "CITIZEN_APPROVAL", value: t("BPA_CITIZEN_APPROVAL") },
    { i18nKey: "Pending DA Engineer", code: "PENDING_DA_ENGINEER", value: t("BPA_PENDING_DA_ENGINEER") },
    { i18nKey: "Pending DD AD Development Authority", code: "PENDING_DD_AD_DEVELOPMENT_AUTHORITY", value: t("BPA_PENDING_DD_AD_DEVELOPMENT_AUTHORITY") },
    { i18nKey: "Pending Chairman DA", code: "PENDING_CHAIRMAN_DA", value: t("BPA_PENDING_CHAIRMAN_DA") },
    { i18nKey: "Payment Pending", code: "PAYMENT_PENDING", value: t("BPA_PAYMENT_PENDING") },
    { i18nKey: "Forwarded To Technical Engineer", code: "FORWARDED_TO_TECHNICAL_ENGINEER", value: t("BPA_FORWARDED_TO_TECHNICAL_ENGINEER") },
    { i18nKey: "Forwarded To DD AD TCP", code: "FORWARDED_TO_DD_AD_TCP", value: t("BPA_FORWARDED_TO_DD_AD_TCP") },
    { i18nKey: "Pending Chairman President", code: "PENDING_CHAIRMAN_PRESIDENT", value: t("BPA_PENDING_CHAIRMAN_PRESIDENT") },
    { i18nKey: "Citizen Final Payment", code: "CITIZEN_FINAL_PAYMENT", value: t("BPA_CITIZEN_FINAL_PAYMENT") },
    { i18nKey: "Application Completed", code: "APPLICATION_COMPLETED", value: t("BPA_APPLICATION_COMPLETED") },
    { i18nKey: "Rejected", code: "REJECTED", value: t("BPA_REJECTED") }
  ].sort((a, b) => a.code.localeCompare(b.code));

  const applicationTypeOptions = [
  { i18nKey: "BPA", code: "BPA", value: t("BPA_APPLICATION_TYPE_BPA") },
  { i18nKey: "Occupancy", code: "OCCUPANCY", value: t("BPA_APPLICATION_TYPE_OCCUPANCY") }
].sort((a, b) => a.code.localeCompare(b.code));


  // const filteredApplications = data?.bpa || [];
const filteredApplications = applications;

console.log("filteredApplications==",filteredApplications)
  return (
    <React.Fragment>
      <Header>{`${t("BPA_MY_APPLICATIONS_HEADER")} (${filteredApplications.length})`}</Header>
      <Card>
        <div style={{ marginLeft: "16px" }}>
          <div style={{ display: "flex", flexDirection: "row", alignItems: "center", gap: "16px" }}>
            <div style={{ flex: 1 }}>
              <div style={{ display: "flex", flexDirection: "column" }}>
                <CardLabel>{t("BPA_APPLICATION_NO")}</CardLabel>
                <TextInput
                  placeholder={t("BPA_ENTER_APPLICATION_NO")}
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  style={{ width: "100%", padding: "8px", height: "150%" }}
                />
              </div>
            </div>
            <div style={{ flex: 1 }}>
              <div style={{ display: "flex", flexDirection: "column" }}>
                <CardLabel>{t("PT_COMMON_TABLE_COL_STATUS_LABEL")}</CardLabel>
                <Dropdown
                  className="form-field"
                  selected={status}
                  select={setStatus}
                  option={statusOptions}
                  placeholder={t("BPA_SELECT_STATUS")}
                  optionKey="value"
                  style={{ width: "100%" }}
                  t={t}
                />
              </div>
            </div>
            <div style={{ flex: 1 }}>
              <div style={{ display: "flex", flexDirection: "column" }}>
                <CardLabel>{t("APPLICATION TYPE")}</CardLabel>
                <Dropdown
                  className="form-field"
                  selected={applicationType}
                  select={handleApplicationTypeChange}
                  option={applicationTypeOptions}
                  placeholder={t("SELECT_APP_TYPE")}
                  optionKey="value"
                  style={{ width: "100%" }}
                  t={t}
                />
              </div>
            </div>
            <div>
              <div style={{ marginTop: "17%" }}>
                <SubmitBar label={t("ES_COMMON_SEARCH")} disabled={!applicationType?.code} onSubmit={handleSearch} />
                <p
                  className="link"
                  style={{ marginLeft: "30%", marginTop: "10px", display: "block" }}
                  onClick={() => {
                    setSearchTerm("");
                    setStatus(null);
                    setApplicationType(null);
                    setFilters(initialFilters);
                  }}
                >
                  {t(`ES_COMMON_CLEAR_ALL`)}
                </p>
              </div>
            </div>
          </div>
          <Link to="/upyog-ui/citizen/obpsv2/building-permit/area-mapping">
            <SubmitBar style={{ borderRadius: "30px", width: "20%", marginTop: "16px" }} label={t("BPA_NEW_APPLICATION") + " +"} />
          </Link>
        </div>
      </Card>
      <div>
        {!isLoading && filteredApplications.length > 0 && applicationType?.code === "BPA" &&
          filteredApplications.map((application, index) => (
            <div key={index}>
              <BPAApplication application={application} tenantId={tenantId} buttonLabel={t("BPA_VIEW_DETAILS")} />
            </div>
          ))}
          {!isLoading && filteredApplications.length > 0 && applicationType?.code === "OCCUPANCY" &&
          filteredApplications.map((application, index) => (
            <div key={index}>
              <OCApplication application={application} tenantId={tenantId} buttonLabel={t("OC_VIEW_DETAILS")} />
            </div>
          ))}
        {!isLoading && filteredApplications.length === 0 && isBPA && (
          <p style={{ marginLeft: "16px", marginTop: "16px" }}>{t("BPA_NO_APPLICATION_FOUND_MSG")}</p>
        )}
        {!isLoading && filteredApplications.length === 0 && isOC && (
          <p style={{ marginLeft: "16px", marginTop: "16px" }}>{t("OC_NO_APPLICATION_FOUND_MSG")}</p>
        )}

        {filteredApplications.length !== 0 && totalCount > t1 && (
          <div>
            <p style={{ marginLeft: "16px", marginTop: "16px" }}>
              <span className="link" onClick={handleLoadMore}>
                {t("BPA_LOAD_MORE_MSG")}
              </span>
            </p>
          </div>
        )}
      </div>
    </React.Fragment>
  );
};