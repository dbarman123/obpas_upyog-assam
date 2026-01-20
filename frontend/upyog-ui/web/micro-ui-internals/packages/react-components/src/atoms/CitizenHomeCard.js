import React, { useState } from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
// import { PopUp, Card, SubmitBar } from "@upyog/digit-ui-react-components";
import {
  Modal,
  Card,
} from "@upyog/digit-ui-react-components";

import { useHistory } from "react-router-dom/cjs/react-router-dom.min";
import { OBPSV2Services } from "../../../libraries/src/services/elements/OBPSV2";

const CitizenHomeCard = ({ header, links = [], state, Icon, Info, isInfo = false, styles }) => {
  const { t } = useTranslation();
    const [showOcPopup, setShowOcPopup] = useState(false);
    const [showBuildingpermitModal, setShowBuildingpermitModal] = useState(false);
const [buildingpermitNumber, setBuildingPermitNumber] = useState("");
const [propertyId, setPropertyId] = useState("");
const [propertyDetails, setPropertyDetails] = useState(null);
const [loading, setLoading] = useState(false);
const [error, setError] = useState(null);
const tenantId = Digit.ULBService.getCitizenCurrentTenant(true);

    const history = useHistory();
  // const handleOcYes = () => {
  //   setShowOcPopup(false);
  //   history.push("/upyog-ui/citizen/obpsv2/ocbpa");
  // };

const handleBuildingpermitSubmit = async () => {
    setShowBuildingpermitModal(false);
    setBuildingPermitNumber("");
  try {
    const tenantId = Digit.ULBService.getCurrentTenantId();

    // Call your BPA search API with propertyNumber
    const filters = { applicationNo: buildingpermitNumber };
    const bpaRes = await Digit.OBPSV2Services.search({ tenantId, filters });
    const application = bpaRes?.bpa?.[0];

    if (!application) {
      alert("No BPA record found for this building permit number");
      return;
    }
        if (propertyId) {
      const isPropertyValid = await getPropertyDetails();
      if (!isPropertyValid){ 
        alert("Property not found");
        return
      };
    }
debugger
    // Build prefill data
    const prefillData = {
      nameOfMasterPlan: application?.additionalDetails?.masterPlanName,
      nameOfUlbPanchayat: application?.additionalDetails?.ulbPanchayatName,
      phoneNumber: application?.landInfo?.owners[0]?.mobileNumber,
      email: application?.landInfo?.owners[0]?.emailId,
      nameOfApplicant: application?.landInfo?.owners[0]?.name,
        siteAddressHouseNo: application?.additionalDetails?.siteAddress?.houseNo,
  siteAddressAddressLineOne: application?.additionalDetails?.siteAddress?.addressLine1,
  siteAddressAddressLineTwo: application?.additionalDetails?.siteAddress?.addressLine2,
  siteAddressState: application?.additionalDetails?.siteAddress?.state,
  siteAddressDistrict: application?.additionalDetails?.siteAddress?.district,
  siteAddressCityVillage: application?.additionalDetails?.siteAddress?.city,
  siteAddressPincode: application?.additionalDetails?.siteAddress?.pincode,
    correspondenceAddressHouseNo: application?.additionalDetails?.correspondenceAddress?.houseNo,
  correspondenceAddressAddressLineOne: application?.additionalDetails?.correspondenceAddress?.addressLine1,
  correspondenceAddressAddressLineTwo: application?.additionalDetails?.correspondenceAddress?.addressLine2,
  correspondenceAddressState: application?.additionalDetails?.correspondenceAddress?.state,
  correspondenceAddressDistrict: application?.additionalDetails?.correspondenceAddress?.district,
  correspondenceAddressCityVillage: application?.additionalDetails?.correspondenceAddress?.city,
  correspondenceAddressPincode: application?.additionalDetails?.correspondenceAddress?.pincode,
    propertyID: application?.additionalDetails?.propertyID,

  ulb: application?.additionalDetails?.propertyDetails?.details?.ulb,
  ward: application?.additionalDetails?.propertyDetails?.details?.ward,
  ownerName: application?.additionalDetails?.propertyDetails?.details?.ownerName,
  guardianName: application?.additionalDetails?.propertyDetails?.details?.guardianName,
  phone: application?.additionalDetails?.propertyDetails?.details?.phone,
  address: application?.additionalDetails?.propertyDetails?.details?.address,
  propertyVendor: application?.additionalDetails?.propertyDetails?.details?.propertyVendor,
  buildingUse: application?.additionalDetails?.propertyDetails?.details?.buildingUse,
    nocNo: application?.additionalDetails?.nocNo,
  nocDate: application?.additionalDetails?.nocDate,
  nameOfRtp: application?.additionalDetails?.nameOfRtp,
  registrationNoRtp: application?.additionalDetails?.registrationNoRtp,
  proposedUseOfBuilding: application?.additionalDetails?.proposedUseOfBuilding,
  noOfFloors: application?.additionalDetails?.noOfFloors

      // add other mappings
    };
    // Navigate with state
    history.push({
      pathname: "/upyog-ui/citizen/obpsv2/ocbpa",
      state: { prefillData, buildingpermitNumber },
    });
  } catch (err) {
    console.error("Error searching building permit number:", err);
  }
};

  const getPropertyDetails = async () => {
  if (!propertyId) {
    setPropertyDetails(null);
    setError("Invalid Property ID. Please enter a valid TIN number.");
    return false;
  }
  try {
    const property = await OBPSV2Services.propertyValidate({
      tenantId: 'as.gmc',
      propertyNumber: propertyId
    });
    if (!property?.valid) {
      setPropertyDetails(null);
      setError(property?.message || "Invalid Property ID");
      return false;
    }

    setPropertyDetails(property?.details);
    return true;
  } catch (error) {
    setPropertyDetails(null);
    setError("Error fetching property details");
    return false;
  }
};

  const Heading = (props) => {
    return <p className="heading-m">{props.label}</p>;
  };
    const Close = () => (
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="#FFFFFF">
      <path d="M0 0h24v24H0V0z" fill="none" />
      <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12 19 6.41z" />
    </svg>
  );
    const CloseBtn = (props) => {
    return (
      <div className="icon-bg-secondary" onClick={props.onClick}>
        <Close />
      </div>
    );
  };

  const handleOcNo = () => {
    setShowOcPopup(false);
    setBuildingPermitNumber('')
    setPropertyId(''),
    history.push("/upyog-ui/citizen/obpsv2/ocbpa");
  };
  console.log("linkslinks",links)
  function replaceDigitUiWithUpyogUi(data) {
    return data.map(item => ({
      ...item,
      sidebarURL: item.sidebarURL ? item.sidebarURL.replace("digit-ui", "upyog-ui") : item.sidebarURL,
      navigationURL: item.navigationURL ? item.navigationURL.replace("digit-ui", "upyog-ui") : item.navigationURL,
      link: item.link ? item.link.replace("digit-ui", "upyog-ui") : item.link
    }));
  }
  let updatedData = replaceDigitUiWithUpyogUi(links);
  const rtpObject=updatedData?.[2]
  
  updatedData=[
    ...updatedData?.filter(val=>val?.id!==3074),
    {
      "id": 3079,
      "name": "Apply for Occupancy Certificate",
      "url": "digit-ui-card",
      "displayName": "Apply for Occupancy Certificate",
      "orderNumber": 1,
      "parentModule": "OBPSV2",
      "enabled": true,
      "serviceCode": "",
      "code": "",
      "path": "",
      "navigationURL": "/upyog-ui/citizen/obpsv2/ocbpa",
      "leftIcon": "OBPSIcon",
      "rightIcon": "",
      "queryParams": "",
      "sidebar": "digit-ui-links",
      "sidebarURL": "/upyog-ui/citizen/obpsv2-home",
      "link": "/upyog-ui/citizen/obpsv2/ocbpa",
      "i18nKey": "Apply for Occupancy Certificate"
    },
    rtpObject
  ]
//   function updateDisplayName(data, roles) {
//     return data.map(item => {
//         if (item.id === 3074) {
//             const isCitizen = roles.some(role => role.code === "CITIZEN");
//             const isArchitect = roles.some(role => role.code === "BPA_ARCHITECT");
            
//             if (isArchitect) {
//                 return {
//                     ...item,
//                     i18nKey: "View as RTP"  
//                 };
//             } else if (isCitizen) {
//                 return {
//                     ...item,
//                     i18nKey: "Register as RTP"  
//                 };
//             }
//         }
//         return item;
//     });
// }

// const roles = Digit.SessionStorage.get("User")?.info?.roles;

// const updatedData = updateDisplayName(updatedLinks, roles);

  
  return (
    <div className="CitizenHomeCard" style={styles ? styles : {}}>
      <div className="header">
        <h2>{header}</h2>
        <Icon />
      </div>

      {/* <div className="links">
        {updatedData.map((e, i) => (
          <div className="linksWrapper" style={{paddingLeft:"10px"}}>
            {(e?.parentModule?.toUpperCase() == "BIRTH" ||
              e?.parentModule?.toUpperCase() == "DEATH" ||
              e?.parentModule?.toUpperCase() == "FIRENOC") ?
              <a href={e.link}>{e.i18nKey}</a> :
              <Link key={i} to={{ pathname: e.link, state: e.state }}>
                {e.i18nKey}
              </Link>
            }
          </div>
        ))}
      </div> */}
      <div className="links">
      {updatedData.map((e, i) => (
  <div className="linksWrapper" style={{ paddingLeft: "10px" }} key={i}>
    {(e?.parentModule?.toUpperCase() === "BIRTH" ||
      e?.parentModule?.toUpperCase() === "DEATH" ||
      e?.parentModule?.toUpperCase() === "FIRENOC") ? (
      <a href={e.link}>{e.i18nKey}</a>
    ) : (
      e.i18nKey === "Apply for Occupancy Certificate" ? (
        <a
          href="#"
          onClick={(ev) => {
            ev.preventDefault();
            setShowOcPopup(true);
          }}
        >
          {e.i18nKey}
        </a>
      ) : (
        <Link to={{ pathname: e.link, state: e.state }}>
          {e.i18nKey}
        </Link>
      )
    )}
  </div>
))}
</div>

      <div>{isInfo ? <Info /> : null}</div>
      {/* {showOcPopup && (
  <PopUp>
    <div className="popup-module">
      <h2 style={{ marginBottom: "16px" }}>
        Is the Building Permit generated from OBPASS?
      </h2>

      <div style={{ display: "flex", gap: "12px", justifyContent: "flex-end" }}>
        <SubmitBar label="Yes" onSubmit={handleOcYes} />
        <SubmitBar label="No" onSubmit={handleOcNo} />
      </div>
    </div>
  </PopUp>
)} */}
{showOcPopup && (
  <Modal
    headerBarMain={<Heading label={t("Confirmation")} />}
    headerBarEnd={<CloseBtn onClick={handleOcNo} />}
    actionCancelLabel={t("No")}
    actionCancelOnSubmit={handleOcNo}
    actionSaveLabel={t("Yes")}
    actionSaveOnSubmit={() => {
      setShowOcPopup(false);
      setShowBuildingpermitModal(true);
    }}
    formId="obpass-confirmation-modal"
  >
    <div style={{ width: "100%",marginTop:'-20px' }}>
    <form id="obpass-confirmation-modal">
      <Card>
        <p>Is the Building Permit generated from OBPASS?</p>
      </Card>
    </form>
    </div>
  </Modal>
)}
{showBuildingpermitModal && (
  <Modal
    headerBarMain={<Heading label={t("Enter Details")} />}
    headerBarEnd={
      <CloseBtn
        onClick={() => {
          setShowBuildingpermitModal(false);
          setBuildingPermitNumber('');
          setPropertyId('');
        }}
      />
    }
    actionCancelLabel={t("Cancel")}
    actionCancelOnSubmit={() => {
      setShowBuildingpermitModal(false);
      setBuildingPermitNumber('');
      setPropertyId('');
    }}
    actionSaveLabel={t("Proceed")}
    actionSaveOnSubmit={handleBuildingpermitSubmit}
    isDisabled={!propertyId}
    formId="building-permit-number-modal"
  >
    <div style={{ width: "100%", marginTop: "-20px" }}>
      <form id="building-permit-number-modal">
        <Card>

          {/* Property ID (Mandatory) */}
          <label>
            {t("Property ID")} <span style={{ color: "red" }}>*</span>
          </label>
          <input
            type="text"
            value={propertyId}
            onChange={(e) => setPropertyId(e.target.value)}
            placeholder={t("Enter Property ID")}
            style={{ width: "100%", padding: "8px", marginTop: "8px" }}
          />

          {/* Building Permit Number (Optional) */}
          <label style={{ marginTop: "16px", display: "block" }}>
            {t("Building Permit Number")}
          </label>
          <input
            type="text"
            value={buildingpermitNumber}
            onChange={(e) => setBuildingPermitNumber(e.target.value)}
            placeholder={t("Enter Building Permit Number")}
            style={{ width: "100%", padding: "8px", marginTop: "8px" }}
          />

        </Card>
      </form>
    </div>
  </Modal>
)}

{/* {showBuildingpermitModal && (
  <Modal
    headerBarMain={<Heading label="Enter Property Number" />}
    headerBarEnd={<CloseBtn onClick={() => setShowBuildingpermitModal(false)} />}
    actionCancelLabel="Cancel"
    actionCancelOnSubmit={() => setShowBuildingpermitModal(false)}
    actionSaveLabel="Proceed"
    actionSaveOnSubmit={handlePropertySubmit}
    formId="property-number-modal"
  >
    <form id="property-number-modal">
      <Card>
        <label>Property Number</label>
        <input
          type="text"
          value={propertyNumber}
          onChange={(e) => setPropertyNumber(e.target.value)}
          placeholder="Enter Property Number"
          style={{ width: "100%", padding: "8px", marginTop: "8px" }}
        />
      </Card>
    </form>
  </Modal>
)} */}
    </div>
  );
};

export default CitizenHomeCard;
