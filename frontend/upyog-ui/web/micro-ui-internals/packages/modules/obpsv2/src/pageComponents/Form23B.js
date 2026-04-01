import React, { useEffect, useState } from "react";
import {
  FormStep,
  TextInput,
  CardLabel,
  Card,
  CardSubHeader,
  DatePicker
} from "@upyog/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import Timeline from "../components/Timeline";

const Form23B = ({ config, onSelect, userType, formData }) => {
  const { t } = useTranslation();
  const planDetail = (formData &&
 formData.form &&
 formData.form.scrutinyDetails &&
 formData.form.scrutinyDetails.planDetail != null)
  ? formData.form.scrutinyDetails.planDetail
  : {}

  const [purpose, setPurpose] = useState((planDetail &&
 planDetail.planInformation &&
 planDetail.planInformation.occupancy != null)
  ? planDetail.planInformation.occupancy
  : "");
  const [noOfInhabitants, setNoOfInhabitants] = useState((planDetail && planDetail.noOfInhabitants != null) ? planDetail.noOfInhabitants : "");
  const [waterSource, setWaterSource] = useState((planDetail && planDetail.waterSource != null) ? planDetail.waterSource : "");
  const [distanceFromSewer, setDistanceFromSewer] = useState((planDetail && planDetail.distanceFromSewer != null) ? planDetail.distanceFromSewer : "");
  const [materials, setMaterials] = useState((planDetail && planDetail.materials != null) ? planDetail.materials : "");
  const [architectName, setArchitectName] = useState((planDetail && planDetail.architectName != null) ? planDetail.architectName : "");
  const [registrationNumber, setRegistrationNumber] = useState((planDetail && planDetail.registrationNumber != null) ? planDetail.registrationNumber : "");
  const [architectAddress, setArchitectAddress] = useState((planDetail && planDetail.architectAddress != null) ? planDetail.architectAddress : "");
  const [constructionValidUpto, setConstructionValidUpto] = useState((planDetail && planDetail.constructionValidUpto != null) ? planDetail.constructionValidUpto : null);
  const [leaseExtensionUpto, setLeaseExtensionUpto] = useState((planDetail && planDetail.leaseExtensionUpto != null) ? planDetail.leaseExtensionUpto : null);
  const [dwellingUnitSize, setDwellingUnitSize] = useState((planDetail && planDetail.dwellingUnitSize != null) ? planDetail.dwellingUnitSize : "");

  const initialFloorsSource = (planDetail &&
 planDetail.blocks &&
 planDetail.blocks[0] &&
 planDetail.blocks[0].building &&
 planDetail.blocks[0].building.floors != null)
  ? planDetail.blocks[0].building.floors
  : []
  const [floorsData, setFloorsData] = useState(() =>
    initialFloorsSource.map((f, i) => {
      const occ = (f && f.occupancies && f.occupancies[0] != null) ? f.occupancies[0] : {};
      return {
        label: (f && f.name != null) 
          ? f.name 
          : `Floor ${(f && f.number != null) ? f.number : (i + 1)}`,
existing: (occ && occ.builtUpArea != null) ? occ.builtUpArea : "",
proposed: (occ && occ.proposed != null) ? occ.proposed : "",
total: (occ && occ.total != null) ? occ.total : ""
      };
    })
  );

  const [sanitaryDetails, setSanitaryDetails] = useState([
    { description: "NUMBER OF URINALS", total: (planDetail && planDetail.totalUrinals != null) ? planDetail.totalUrinals : "" },
    { description: "NUMBER OF BATHROOMS", total: (planDetail && planDetail.totalBathrooms != null) ? planDetail.totalBathrooms : "" },
    { description: "NUMBER OF LATRINES", total: (planDetail && planDetail.totalLatrines != null) ? planDetail.totalLatrines : "" },
    { description: "NUMBER OF KITCHENS", total: (planDetail && planDetail.totalKitchens != null) ? planDetail.totalKitchens : "" }
  ]);

  const updateFloorField = (index, key, value) => {
    setFloorsData((prev) => {
      const next = [...prev];
      next[index] = { ...next[index], [key]: value };
      return next;
    });
  };

  const updateSanitaryField = (index, value) => {
    setSanitaryDetails((prev) => {
      const next = [...prev];
      next[index].total = value;
      return next;
    });
  };

  const goNext = () => {
    const formStepData = {
      purpose,
      noOfInhabitants,
      waterSource,
      distanceFromSewer,
      materials,
      architectName,
      registrationNumber,
      architectAddress,
      dwellingUnitSize,
      constructionValidUpto,
      leaseExtensionUpto,
      floors: floorsData,
      sanitaryDetails
    };

    if (userType === "citizen") {
      onSelect(config.key, { ...formData[config.key], ...formStepData });
    } else {
      onSelect(config.key, formStepData);
    }
  };

  const onSkip = () => onSelect();

  useEffect(() => {
    if (userType === "citizen") goNext();
  }, []);

  const tableStyle = { width: "80%", borderCollapse: "collapse", marginLeft: "20px" };
  const thStyle = { border: "1px solid #ddd", padding: "8px", textAlign: "left", background: "#f7f7f7" };
  const tdStyle = { border: "1px solid #ddd", padding: "8px" };
  const inputStyle = { width: "100%", boxSizing: "border-box", padding: "6px" };

  return (
    <React.Fragment>
      <Timeline currentStep={7} flow={"editApplication"} />
      <Card>
        <CardSubHeader style={{ textAlign: "center" }}>
          <h2>{t("Form 23")}</h2>
          <div style={{ fontSize: "14px", color: "#555" }}>{t("(BPA_ANNEXURE_DESCRIPTION_B)")}</div>
        </CardSubHeader>
      </Card>

      <FormStep config={config} onSelect={goNext} onSkip={onSkip} t={t} isDisabled={false}>
        <CardLabel>(1) {t("THE_PURPOSE")}</CardLabel>
        <TextInput value={purpose} onChange={(e) => setPurpose(e.target.value)} placeholder={t("Text Input")} />

        <CardLabel>(2) {t("DETAILS_OF_AREA")}</CardLabel>
        {floorsData.length > 0 ? (
          <table style={tableStyle}>
            <thead>
              <tr>
                <th style={thStyle}>{t("FLOOR")}</th>
                <th style={thStyle}>{t("EXISTING")} (sq. m.)</th>
                <th style={thStyle}>{t("PROPOSED")} (sq. m.)</th>
                <th style={thStyle}>{t("TOTAL")} (sq. m.)</th>
              </tr>
            </thead>
            <tbody>
              {floorsData.map((floor, idx) => (
                <tr key={floor.id != null ? floor.id : idx}>
  <td style={tdStyle}>
    {`${t("FLOOR")} ${floor.number != null ? floor.number : idx + 1}`}
  </td>
                  <td style={tdStyle}>
                    <input
                      value={floor.existing != null ? floor.existing : ""}
                      onChange={(e) => updateFloorField(idx, "existing", e.target.value)}
                      placeholder={t("Text Input")}
                      style={inputStyle}
                      inputMode="numeric"
                    />
                  </td>
                  <td style={tdStyle}>
                    <input
                      value={floor.proposed != null ? floor.proposed : ""}
                      onChange={(e) => updateFloorField(idx, "proposed", e.target.value)}
                      placeholder={t("Text Input")}
                      style={inputStyle}
                      inputMode="numeric"
                    />
                  </td>
                  <td style={tdStyle}>
                    <input
                      value={floor.total != null ? floor.total : ""}
                      onChange={(e) => updateFloorField(idx, "total", e.target.value)}
                      placeholder={t("Text Input")}
                      style={inputStyle}
                      inputMode="numeric"
                    />
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        ) : (
          <div style={{ marginLeft: "20px" }}>{t("No floor data available")}</div>
        )}

        <br />

        <CardLabel>(3) (a) {t("NO_OF_INHABITANTS")}</CardLabel>
        <TextInput value={noOfInhabitants != null ? noOfInhabitants : ""} onChange={(e) => setNoOfInhabitants(e.target.value)} placeholder={t("Text Input")} />

        <CardLabel>(3) (b) {t("DETAILS_OF_SANITARY_AND_KITCHEN_FACILITIES")}</CardLabel>
        <table style={tableStyle}>
          <thead>
            <tr>
              <th style={thStyle}>{t("DESCRIPTION")}</th>
              <th style={thStyle}>{t("TOTAL NUMBER")}</th>
            </tr>
          </thead>
          <tbody>
            {sanitaryDetails.map((row, idx) => (
              <tr key={idx}>
                <td style={tdStyle}>{t(row.description)}</td>
                <td style={tdStyle}>
                  <input
                    value={row.total}
                    onChange={(e) => updateSanitaryField(idx, e.target.value)}
                    placeholder={t("Text Input")}
                    style={inputStyle}
                    inputMode="numeric"
                  />
                </td>
              </tr>
            ))}
          </tbody>
        </table>

        <CardLabel>(c) {t("SOURCE_OF_WATER")}</CardLabel>
        <TextInput value={waterSource} onChange={(e) => setWaterSource(e.target.value)} placeholder={t("Text Input")} />

        <CardLabel>(d) {t("DISTANCE_FROM_PUBLIC_SEWER")}</CardLabel>
        <TextInput value={distanceFromSewer} onChange={(e) => setDistanceFromSewer(e.target.value)} placeholder={t("Text Input")} />

        <CardLabel>(e) {t("MATERIALS_TO_BE_USED")}</CardLabel>
        <TextInput value={materials} onChange={(e) => setMaterials(e.target.value)} placeholder={t("Text Input")} />

        <CardLabel style={{ fontSize: "14px", marginLeft: "24px" }}>(I) {t("NAME_OF_REGISTERED_ARCHITECT")}</CardLabel>
        <TextInput style={{ fontSize: "14px", marginLeft: "24px" }} value={architectName} onChange={(e) => setArchitectName(e.target.value)} placeholder={t("Text Input")} />

        <CardLabel style={{ fontSize: "14px", marginLeft: "24px" }}>(II) {t("REGISTRATION_NO_OF_ARCHITECT")}</CardLabel>
        <TextInput style={{ fontSize: "14px", marginLeft: "24px" }} value={registrationNumber} onChange={(e) => setRegistrationNumber(e.target.value)} placeholder={t("Text Input")} />

        <CardLabel style={{ fontSize: "14px", marginLeft: "24px" }}>(III) {t("ADDRESS_OF_ARCHITECT")}</CardLabel>
        <TextInput style={{ fontSize: "14px", marginLeft: "24px" }} value={architectAddress} onChange={(e) => setArchitectAddress(e.target.value)} placeholder={t("Text Input")} />

        <div style={{ display: "flex", alignItems: "center", flexWrap: "wrap", gap: "12px", marginBottom: "1rem", width: "100%" }}>
          (4) The period of construction valid up to
          <DatePicker style={{ width: "30%" }} date={constructionValidUpto} onChange={(date) => setConstructionValidUpto(date)} name="constructionValidUpto" />
          as per the lease condition/further extension of the time for construction granted by the leaser is valid up to
          <DatePicker style={{ width: "30%" }} date={leaseExtensionUpto} onChange={(date) => setLeaseExtensionUpto(date)} name="leaseExtensionUpto" />
          Time construction obtained from the Competent Authority.
        </div>

        <CardLabel>(5) {t("SIZE_OF_DWELLING_UNIT")}</CardLabel>
        <TextInput value={dwellingUnitSize} onChange={(e) => setDwellingUnitSize(e.target.value)} placeholder={t("Text Input")} />
      </FormStep>
    </React.Fragment>
  );
};

export default Form23B;
