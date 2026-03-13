import React, { Fragment, useMemo, useState, useEffect } from "react";
import { FormStep, TextInput, CardLabel,UploadFile, MobileNumber, RadioButtons,PDFSvg,Banner } from "@upyog/digit-ui-react-components";
import axios from 'axios';
import { Link } from "react-router-dom/cjs/react-router-dom.min";
import { useLocation } from "react-router-dom";
import { OBPSV2Services } from "../../../../libraries/src/services/elements/OBPSV2";
import { Dropdown } from "@upyog/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router-dom";
import useWorkflowDetails from "@upyog/digit-ui-libraries/src/hooks/workflow";
import { ActionBar } from "@upyog/digit-ui-react-components";
import { SubmitBar } from "@upyog/digit-ui-react-components";
import { Menu } from "@upyog/digit-ui-react-components";
import ActionModal from "../../../templates/ApplicationDetails/Modal/OBPSV2ActionModal";

const OcBuildingPermit = () => {
    const { t } = useTranslation();
    const [formData,setFormData]=useState({"nameOfMasterPlan": "Kamrup Metro MP", "nameOfUlbPanchayat": "Guwahati Municipal Corporation"})
    const [parameter,setParameter]=useState({action:['Submit Report','Reject']})
    const [isLoading,setIsLoading]=useState(false)
    const [attachments,setAttachments]=useState({})
    const [showAction,setShowAction]=useState(false)
    const [showModal,setShowModal]=useState(false)
    const [modalDetails,setModalDetails]=useState({})
    const location = useLocation();
    const [selectedAction,setSelectedAction]=useState(null)
    // const tenantId = Digit.ULBService.getCitizenCurrentTenant(true);
const prefillData = location.state?.prefillData;
const { acknowledgementIds, tenantId: tenantFromURL } = useParams();
const [hasAccess, setHasAccess] = useState(false);
const user = Digit.UserService.getUser();
const isCitizen = user?.info?.type === "CITIZEN";
const isEmployee = user?.info?.type === "EMPLOYEE";
const roles = user?.info?.roles || [];


let tenantId = Digit.ULBService.getCitizenCurrentTenant(true);

if (!tenantId) {
  tenantId = tenantFromURL;
}
    const { data: ocSearchData } = Digit.Hooks.obpsv2.useOCSearchApi(
        {
            tenantId,
            filters: { applicationNo: acknowledgementIds },
        },
        { enabled: !!acknowledgementIds }
    );
let workflowDetails = useWorkflowDetails({
  tenantId: tenantId,
  id: acknowledgementIds,
  moduleCode: "OC",
});
console.log("OC workflow==",workflowDetails)
useEffect(() => {
  if (!isEmployee) return;
  const nextActionRoles =
    workflowDetails?.data?.ProcessInstances?.[0]?.nextActions?.[0]?.roles || [];

  const access = roles?.some((role) =>
    nextActionRoles.includes(role?.code)
  );

  setHasAccess(access);
}, [workflowDetails, roles, isEmployee]);
let isBpaPrefilled = Boolean(location.state?.buildingpermitNumber);

const allFormFields = FORM_JSON.reduce((acc, section) => {
  section?.formData?.forEach(item => {
    if (item.field) acc.push(item.field);
  });
  return acc;
}, []);

useEffect(() => {
  if (ocSearchData?.ocs?.length) {
    const oc = ocSearchData.ocs[0];
    setFormData({
      ...formData,
      isEmployee: isEmployee,
      nameOfMasterPlan: oc?.nameOfMasterPlan,
      nameOfUlbPanchayat: oc?.nameOfUlbPanchayat,
      nameOfApplicant: oc?.nameOfApplicant,
      phoneNumber: oc?.landInfo?.owners?.[0]?.mobileNumber,
      email: oc?.landInfo?.owners?.[0]?.emailId,
      propertyID: oc?.additionalDetails?.propertyID,
      nocNo: oc?.additionalDetails?.nocNo,
      nocDate: oc?.additionalDetails?.nocDate,
      nameOfRtp: oc?.additionalDetails?.nameOfRtp,
      registrationNoRtp: oc?.additionalDetails?.registrationNoRtp,
      noOfFloors: oc?.additionalDetails?.noOfFloors,
            district: oc?.areaMapping?.district || "",
      planningArea: oc?.areaMapping?.planningArea || "",
      ppAuthority: oc?.areaMapping?.ppAuthority || "",
      bpAuthority: oc?.areaMapping?.bpAuthority || "",
      concernedAuthority: oc?.areaMapping?.concernedAuthority || "",
      mouza: oc?.areaMapping?.mouza || "",
      revenueVillage: oc?.areaMapping?.revenueVillage || "",
      siteAddressHouseNo: oc?.landInfo?.owners?.[0]?.permanentAddress?.houseNo || "",
      siteAddressAddressLineOne: oc?.landInfo?.owners?.[0]?.permanentAddress?.addressLine1 || "",
      siteAddressAddressLineTwo: oc?.landInfo?.owners?.[0]?.permanentAddress?.addressLine2 || "",
      siteAddressState: oc?.landInfo?.owners?.[0]?.permanentAddress?.state || "",
      siteAddressDistrict: oc?.landInfo?.owners?.[0]?.permanentAddress?.district || "",
      siteAddressCityVillage: oc?.landInfo?.owners?.[0]?.permanentAddress?.locality?.name || "",
      siteAddressPincode: oc?.landInfo?.owners?.[0]?.permanentAddress?.pincode || "",
      correspondenceAddressHouseNo: oc?.landInfo?.owners?.[0]?.correspondenceAddress?.houseNo || "",
      correspondenceAddressAddressLineOne: oc?.landInfo?.owners?.[0]?.correspondenceAddress?.addressLine1 || "",
      correspondenceAddressAddressLineTwo: oc?.landInfo?.owners?.[0]?.correspondenceAddress?.addressLine2 || "",
      correspondenceAddressState: oc?.landInfo?.owners?.[0]?.correspondenceAddress?.state || "",
      correspondenceAddressDistrict: oc?.landInfo?.owners?.[0]?.correspondenceAddress?.district || "",
      correspondenceAddressCityVillage: oc?.landInfo?.owners?.[0]?.correspondenceAddress?.locality?.name || "",
      correspondenceAddressPincode: oc?.landInfo?.owners?.[0]?.correspondenceAddress?.pincode || "",
      ulb: oc?.additionalDetails?.propertyDetails?.details?.ulb || "",
      ward: oc?.additionalDetails?.propertyDetails?.details?.ward || "",
      ownerName: oc?.additionalDetails?.propertyDetails?.details?.ownerName || "",
      guardianName: oc?.additionalDetails?.propertyDetails?.details?.guardianName || "",
      phone: oc?.additionalDetails?.propertyDetails?.details?.phone || "",
      address: oc?.additionalDetails?.propertyDetails?.details?.address || "",
      propertyVendor: oc?.additionalDetails?.propertyDetails?.details?.propertyVendor || "",
      buildingUse: oc?.additionalDetails?.propertyDetails?.details?.buildingUse || "",
      penaltyApplicable: oc?.additionalDetails?.isPanalityApplicable? "YES" : "NO",
      penaltyAmount: oc?.additionalDetails?.penaltyAmount
    });

    setParameter((prev) => ({
      ...prev,
      wfCurrentState: workflowDetails?.data?.actionState?.state,
    }));
  }
}, [ocSearchData]);

useEffect(() => {
  if (prefillData && Object.keys(prefillData).length) {
    updateFormWithPrefill(prefillData);
  }
}, [prefillData]);

const updateFormWithPrefill = (data) => {
  setFormData(prev => {
    const newFormData = { ...prev,isEmployee: isEmployee };

    allFormFields.forEach((field) => {
      if (data[field] !== undefined) {
        newFormData[field] = data[field];
      }
    });

    return newFormData;
  });

  if (data.attachments) {
    setAttachments(data.attachments);
  }
};

    const formJson = useMemo(() => {
        return Object.keys(attachments || {}).length === 0
            ?FORM_JSON: FORM_JSON?.filter(val => val?.title !== "Attachments");
    }, [attachments]);

    // useEffect(()=>{
    //     (async()=>{
    //         axios.post("http://localhost:8100/oc-services/v1/oc/_search", {"applicationNo":'AS/OC/2025/009' })
    //         .then(res => {
    //             const searchedData=JSON.parse(res?.data?.OCRequest?.[0]?.dataJson)
    //             setFormData(searchedData)
    //             setAttachments(searchedData?.attachments)
    //             setParameter({...parameter,wfCurrentState:res?.data?.OCRequest?.[0]?.wfCurrentState})
    //             console.log("User search response:", res?.data?.OCRequest?.[0]);
    //         })
    //         .catch(err => {
    //             console.error("User search error:", err);
    //         });
    //     })()
    // },[])

  const handleOnChange=(field,value)=>{
    setFormData((prev)=>({...prev,[field]:value}))
  }

    const onChangeFile = (field,e) => {
    setIsLoading(true)
    setFormData((prev)=>({...prev,[field]:e.target.files[0]}))
    setIsLoading(false)
  };

  const onDeleteFile = (field) => {
    setFormData((prev)=>({...prev,[field]:undefined}))
  };

  const getNextWorkflowState=(currentState)=>{
    if(currentState==='APPLY_FOR_OC'){
        return 'FORWARD_TO_BPA_ENGINEER_GMDA'
    }else if(currentState==='FORWARD_TO_BPA_ENGINEER_GMDA'){
        return 'FORWARD_TO_BPA_ASSOCIATE_PLANNER'
    }else if(currentState==='FORWARD_TO_BPA_ASSOCIATE_PLANNER'){
        return 'FORWARD_TO_BPA_COMMISSIONER'
    }else if(currentState==='FORWARD_TO_BPA_COMMISSIONER'){
        return 'PENDING_BPA_COMMISSIONER_APPROVAL'
    }else{
        return 'APPLY_FOR_OC'
    }
  }

const buildOCPayload = (formData, tenantId, action) => {
  return {
  OC: {
    tenantId,
    id: ocSearchData?.ocs?.[0]?.id,
        applicationNo: ocSearchData?.ocs?.[0]?.applicationNo,
        applicationDate: ocSearchData?.ocs?.[0]?.applicationDate,
        landId: ocSearchData?.ocs?.[0]?.landId,

    nameOfMasterPlan: formData?.nameOfMasterPlan,
    nameOfUlbPanchayat: formData?.nameOfUlbPanchayat,
    nameOfApplicant: formData?.nameOfApplicant,

    businessService: ocSearchData?.ocs?.[0]?.businessService,
    status: ocSearchData?.ocs?.[0]?.status,
    isPaymentDone: false,
            areaMapping: {
            "district": formData?.district?.code,
            "planningArea": formData?.planningArea?.code,
            "planningPermitAuthority": formData?.ppAuthority?.code,
            "buildingPermitAuthority": formData?.bpAuthority?.code,
            "revenueVillage": formData?.revenueVillage?.code,
            "villageName": formData?.villageName?.code,
            "concernedAuthority": formData?.concernedAuthority?.code,
            "mouza": formData?.mouza?.code,
            "ward": formData?.ward?.code
        },
    landInfo: {
      landUId: formData?.landUId,
      landUniqueRegNo: formData?.landUniqueRegNo,
      ownershipCategory: "INDIVIDUAL",
      tenantId,

      address: {
        houseNo: formData?.siteAddressHouseNo,
        addressLine1: formData?.siteAddressAddressLineOne,
        addressLine2: formData?.siteAddressAddressLineTwo,
        district: formData?.siteAddressDistrict,
        state: formData?.siteAddressState,
        pincode: formData?.siteAddressPincode,
        addressType: "SITE"
      },

      owners: [
        {
          name: formData?.nameOfApplicant,
          mobileNumber: formData?.phoneNumber,
          emailId: formData?.email,
          ownerType: "INDIVIDUAL",
          isPrimaryOwner: true,
          status: true,
          active: true,
          tenantId: tenantId,

          permanentAddress: {
            addressType: "PERMANENT_ADDRESS",
            houseNo: formData?.siteAddressHouseNo,
            addressLine1: formData?.siteAddressAddressLineOne,
            district: formData?.siteAddressDistrict,
            state: formData?.siteAddressState,
            pincode: formData?.siteAddressPincode,
            tenantId: tenantId,
          },

          correspondenceAddress: {
            addressType: "CORRESPONDENCE_ADDRESS",
            houseNo: formData?.correspondenceAddressHouseNo,
            addressLine1: formData?.correspondenceAddressAddressLineOne,
            district: formData?.correspondenceAddressDistrict,
            state: formData?.correspondenceAddressState,
            pincode: formData?.correspondenceAddressPincode,
            tenantId: tenantId,
          }
        }
      ],
      ownerAddresses: [],
      additionalDetails: {
        landUsage: formData?.landUsage,
        plotType: formData?.plotType,
        roadWidth: formData?.roadWidth
      }
    },
    additionalDetails: {
      propertyID: formData?.propertyID,

      propertyDetails: {
        valid: true,
        details: {
          ulb: formData?.ulb,
          ward: formData?.ward,
          ownerName: formData?.ownerName,
          guardianName: formData?.guardianName,
          phone: formData?.phone,
          address: formData?.address,
          propertyVendor: formData?.propertyVendor,
          buildingUse: formData?.buildingUse,
        }
      },

      nocNo: formData?.nocNo,
      nocDate: formData?.nocDate,
      nameOfRtp: formData?.nameOfRtp,
      registrationNoRtp: formData?.registrationNoRtp,
      proposedUseOfBuilding: formData?.proposedUseOfBuilding,
      noOfFloors: formData?.noOfFloors,
      isPanalityApplicable: formData?.penaltyApplicable?.code === "YES" ?true:false,
      panalityAmount: formData?.panalityAmount,

    },
    documents: [
      formData?.planningPermit && {
        documentType: "PLANNING_PERMIT",
        fileStoreId: formData.planningPermit?.fileStoreId
      },
      formData?.buildingPermit && {
        documentType: "BUILDING_PERMIT",
        fileStoreId: formData.buildingPermit?.fileStoreId
      },
      formData?.buildingPermitDrawing && {
        documentType: "BUILDING_PERMIT_DRAWING",
        fileStoreId: formData.buildingPermitDrawing?.fileStoreId
      },
      formData?.asBuiltDrawing && {
        documentType: "AS_BUILT_DRAWING",
        fileStoreId: formData.asBuiltDrawing?.fileStoreId
      },
      formData?.form16 && {
        documentType: "FORM_16",
        fileStoreId: formData.form16?.fileStoreId
      },
      formData?.form17 && {
        documentType: "FORM_17",
        fileStoreId: formData.form17?.fileStoreId
      },
      formData?.form18 && {
        documentType: "FORM_18",
        fileStoreId: formData.form18?.fileStoreId
      },
      formData?.form19 && {
        documentType: "FORM_19",
        fileStoreId: formData.form19?.fileStoreId
      },
      formData?.form27 && {
        documentType: "FORM_27",
        fileStoreId: formData.form27?.fileStoreId
      },
      formData?.liftLicense && {
        documentType: "LIFT_LICENSE",
        fileStoreId: formData.liftLicense?.fileStoreId
      },
      formData?.powerAllotmentLetter && {
        documentType: "POWER_ALLOTMENT_LETTER",
        fileStoreId: formData.powerAllotmentLetter?.fileStoreId
      },
      formData?.groundWaterAuthorityClearance && {
        documentType: "GROUND_WATER_AUTHORITY_CLEARANCE",
        fileStoreId: formData.groundWaterAuthorityClearance?.fileStoreId
      },
      formData?.buildingPhotoGraph1 && {
        documentType: "BUILDING_PHOTOGRAPH_1",
        fileStoreId: formData.buildingPhotoGraph1?.fileStoreId
      },
      formData?.buildingPhotoGraph2 && {
        documentType: "BUILDING_PHOTOGRAPH_2",
        fileStoreId: formData.buildingPhotoGraph2?.fileStoreId
      },
      formData?.buildingPhotoGraph3 && {
        documentType: "BUILDING_PHOTOGRAPH_3",
        fileStoreId: formData.buildingPhotoGraph3?.fileStoreId
      },
      formData?.buildingPhotoGraph4 && {
        documentType: "BUILDING_PHOTOGRAPH_4",
        fileStoreId: formData.buildingPhotoGraph4?.fileStoreId
      }
    ].filter(Boolean)
  }
};
};
const saveOCSession = (payload) => {
  sessionStorage.setItem(
    "OC_SUBMIT_PAYLOAD",
    JSON.stringify(payload)
  );
};

// const onSubmitOC = async (action) => {
//   const payload = buildOCPayload(formData, tenantId, action);

//   saveOCSession(payload);

//   if (action === "APPLY") {
//       payload.OC.workflow = {
//     action,
//     comments: ""
//   };
//     await OBPSV2Services.occreate({
//       tenantId,
//       ...payload,
//     });
//   } else {
//     debugger
//       payload.OC.workflow = {
//     action,
//     comments: ""
//   };
//   payload.OC.auditDetails = ocSearchData?.ocs?.[0]?.auditDetails;
//     await OBPSV2Services.ocupdate({
//       tenantId,
//       ...payload,
//     });
//   }
// };

const onSubmitOC = async (action) => {
  try {
    const payload = buildOCPayload(formData, tenantId, action);

    saveOCSession(payload);

    // ✅ Always attach workflow
    payload.OC.workflow = {
      action,
      comments: ""
    };

    let res;

    if (action === "APPLY") {
      res = await OBPSV2Services.occreate({
        tenantId,
        ...payload,
      });
    } else {
      payload.OC.auditDetails = ocSearchData?.ocs?.[0]?.auditDetails;

      res = await OBPSV2Services.ocupdate({
        tenantId,
        ...payload,
      });
    }

    console.log("OC Response:", res?.ocs?.[0]?.status);

    // ✅ Show success modal
    setShowModal(true);
    setModalDetails({
      applicationNumber: res?.ocs?.[0]?.applicationNo,
      isSuccess: true,
      message:
        res?.ocs?.[0]?.status === "PENDING_COMMISSIONER"
          ? "Occupancy Certificate Application Approved Successfully"
          : res?.ocs?.[0]?.status === "FORWARDED_TO_ZONAL_OFFICER"
          ? "Occupancy Certificate Application Successfully Forwarded to Zonal Officer"
          : res?.ocs?.[0]?.status === "FORWARDED_TO_ASSOCIATE_PLANNER"
          ? "Occupancy Certificate Application Successfully Forwarded to Associate Planner"
          : "Occupancy Certificate Application Successfully Submitted",

      subMessage:
        res?.ocs?.[0]?.status === "PENDING_COMMISSIONER"
          ? "Application Approved"
          : res?.ocs?.[0]?.status === "FORWARDED_TO_ASSOCIATE_PLANNER"
          ? "Application Pending for BPA Commissioner Approval"
          : res?.ocs?.[0]?.status === "FORWARDED_TO_ZONAL_OFFICER"
          ? "Application Pending for Associate Planner Approval"
          : "Application Submitted and Pending for Engineer GMC Approval"
    });

  } catch (err) {
    console.error("OC error:", err);

    // ❌ Show error modal
    setShowModal(true);
    setModalDetails({
      isSuccess: false,
      message: "Something went wrong while processing the application.",
      subMessage: "Please try again."
    });
  }
};

  console.log('formData',formData)
  console.log("User Type:", user?.info?.type);
console.log("isEmployee:", isEmployee);
console.log("roles:", roles);
console.log("workflow nextActions:", workflowDetails?.data?.processInstances[0]?.nextActions);
console.log("hasAccess:", hasAccess);
console.log("Final Condition:", isEmployee && hasAccess);
  return (
    <React.Fragment>
        <div style={{...styles.wrapper}}>
            {showModal?
            <div>
                <Banner
                      message={modalDetails?.message}
                      applicationNumber={modalDetails?.applicationNumber}
                      info={'Application No.'}
                      successful={modalDetails?.isSuccess}
                      headerStyles={{fontSize: "32px"}}
                    />
                <div style={{color:'#A52A2A',fontSize:'1.5rem',fontWeight:'500',marginTop:'1rem',marginBottom:'1rem'}}>{modalDetails.subMessage}</div>
                <Link style={{color:'#A52A2A',marginBottom:'1rem'}} to={{pathname:isEmployee? '/upyog-ui/employee':`/upyog-ui/citizen`}}>Go back to home page</Link>
            </div>
                
            :<div>
                <div style={styles.header}>{t("OCCUPANCY_CERTIFICATE")}</div>
                {formJson?.map(val=>
                <Fragment>
                    {/* <div style={styles.title}>{val?.title??''}</div> */}
                    <div style={styles.title}>
  {val?.title ? t(val.title) : ""}
</div>

                    {val?.formData
  ?.filter(item =>
    !item?.renderCondition ||
    item.renderCondition(formData)
  )
  ?.map(item =>
                
                    {
                        // return (item?.type==='file'?
                        //     <Fragment>
                        //         <CardLabel>{item?.title} </CardLabel>
                        //         <div className="field" style={{ marginBottom: "16px" }}>
                        //             <UploadFile
                        //             onUpload={e=>onChangeFile(item?.field,e)}
                        //             onDelete={()=>onDeleteFile(item?.field)}
                        //             id="form39"
                        //             message={isLoading ? (
                        //                 <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                        //                     <LoadingSpinner />
                        //                     <span>Uploading...</span>
                        //                 </div>
                        //             ) : formData?.[item?.field] ? "1 File Uploaded" : "No File Uploaded"}
                        //             textStyles={{ width: "100%" }}
                        //             inputStyles={{ width: "280px" }}
                        //             accept=".pdf, .jpeg, .jpg, .png, .dwg"
                        //             buttonType="button"
                        //             error={!formData?.[item?.field]}
                        //             />
                        //         </div>
                        //     </Fragment>: 
                        //     <Fragment>
                        //         <CardLabel>{item?.title}{item?.isRequired && <span className="check-page-link-button">*</span>}</CardLabel>
                        //         <TextInput
                        //             type={item?.type}
                        //             name={item?.field}
                        //             placeholder={`Enter ${item.title}`}
                        //             value={formData?.[item.field]??''}
                        //             onChange={(e) => handleOnChange(item?.field,e.target.value)}
                        //             ValidationRequired={item?.validationRequired}
                        //             disabled={item?.isDisabled || pathName==='/upyog-ui/employee/obpsv2/ocbpa'}
                        //             // {...{ pattern: "^[a-zA-Z ]+$", title: 'Name of Master Plan is a required field' }}
                        //         />
                        //     </Fragment>
                        //     )}
                               const disabledByCondition =
      item?.disableCondition
        ? item.disableCondition(formData)
        : false;

    const finalDisabled =
      item?.isDisabled || disabledByCondition;
                            return (
  item?.type === "file" ? (
    /* FILE */
    <Fragment>
      <CardLabel>{item?.title}</CardLabel>
      <div className="field" style={{ marginBottom: "16px" }}>
        <UploadFile
          onUpload={(e) => onChangeFile(item?.field, e)}
          onDelete={() => onDeleteFile(item?.field)}
          id="form39"
          message={
            isLoading ? (
              <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
                <LoadingSpinner />
                <span>Uploading...</span>
              </div>
            ) : formData?.[item?.field]
              ? "1 File Uploaded"
              : "No File Uploaded"
          }
          textStyles={{ width: "100%" }}
          inputStyles={{ width: "280px" }}
          accept=".pdf, .jpeg, .jpg, .png, .dwg"
          buttonType="button"
          error={!formData?.[item?.field]}
        />
      </div>
    </Fragment>
  ) : 
  item && item?.type?.toLowerCase() === "radio"  ? (
 /* RADIO BUTTONS */
<Fragment>
  <CardLabel>
    {item?.title}
    {item?.isRequired && (
      <span className="check-page-link-button">*</span>
    )}
  </CardLabel>

  <RadioButtons
t={t}
      options={Array.isArray(item?.options) ? item.options : []}
      optionsKey="code"
      name={item?.field}
      disabled={finalDisabled}
      selectedOption={formData?.[item?.field] || "NO"}
      onSelect={(value) =>
  handleOnChange(item.field, value)
}

    innerStyles={{
      display: "inline-block",
      marginRight: "20px"
    }}
  />
</Fragment>

) :item?.type?.toLowerCase() === "dropdown" ? (
    /* DROPDOWN (READ ONLY) */
        <Fragment>
    <CardLabel>
      {t(item?.title)}
      {item?.isRequired && <span className="check-page-link-button">*</span>}
    </CardLabel>

    <Dropdown
      selected={formData?.[item.field]}
      select={(val) => handleOnChange(item.field, val)}
      onBlur={() => {}}
      option={item?.options || []}
      optionKey="i18nKey"
      t={t}
      disable={item?.isDisabled || pathName === "/upyog-ui/employee/obpsv2/ocbpa"}
      disabled={finalDisabled}
    />
  </Fragment>
  ) : (
    /* NORMAL TEXT / NUMBER / DATE */
    <Fragment>
      <CardLabel>
        {item?.title}
        {item?.isRequired && <span className="check-page-link-button">*</span>}
      </CardLabel>
      <TextInput
        type={item?.type}
        name={item?.field}
        placeholder={`Enter ${item.title}`}
        value={formData?.[item.field] ?? ""}
        onChange={(e) => handleOnChange(item?.field, e.target.value)}
        ValidationRequired={item?.validationRequired}
        disabled={finalDisabled}
      />
    </Fragment>
  )
)}                       
                    )}                
                </Fragment>)                
            }
            {Object.keys(attachments || {}).length>0 && 
                <Fragment>
                    <div style={styles.title}>{'Attachments'}</div>
                    <div style={styles.docPreviewWrapper}>
                        {Object.keys(attachments)?.map(key => {
                        return (
                            <div style={styles.docPreview}>
                                <div style={{  marginBottom: "5px"}}>
                                    <PDFSvg />
                                </div>
                                <p style={{ 
                                    textAlign: "start", 
                                    fontSize: "14px", 
                                    fontWeight: "bold", 
                                    color: "#505A5F",
                                    margin: 0
                                }}>
                                    {attachments?.[key]}
                                </p>
                            </div>
                        )
                    })}
                    </div>
                </Fragment>}

                {parameter?.wfCurrentState==='APPLY_FOR_OC'&&SITE_REPORT_DOCUMENT?.map(val=>{
                    return(
                        val?.formData?.map(item=>{
                            return<Fragment>
                                <CardLabel>{item?.title} </CardLabel>
                                <div className="field" style={{ marginBottom: "16px" }}>
                                    <UploadFile
                                    onUpload={e=>onChangeFile(item?.field,e)}
                                    onDelete={()=>onDeleteFile(item?.field)}
                                    id="form39"
                                    message={isLoading ? (
                                        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                                            <LoadingSpinner />
                                            <span>Uploading...</span>
                                        </div>
                                    ) : formData?.[item?.field] ? "1 File Uploaded" : "No File Uploaded"}
                                    textStyles={{ width: "100%" }}
                                    inputStyles={{ width: "280px" }}
                                    accept=".pdf, .jpeg, .jpg, .png, .dwg"
                                    buttonType="button"
                                    error={!formData?.[item?.field]}
                                    />
                                </div>
                            </Fragment>
                        })
                    )
                })}
            </div>}
            
            
            </div>
         {/* {!showModal && !isEmployee && <footer style={styles.footer}>
                <div style={styles.inner}>
                    
                            <button type='button' onClick={()=>onSubmitOC(undefined)} className='submit-bar' style={{width:'8rem', border: '1px solid #a82227', color: 'white', background: '#a82227', paddingLeft: '1rem', paddingRight: '1rem' }}>
                                <header>Submit</header>
                            </button>                   
                </div>
            </footer>} */}
            {/* ------------------------- */}
            {/* {!showModal && (
  <footer style={styles.footer}>
    <div style={styles.inner}>
      {isCitizen && (
        <button
          type="button"
          onClick={() => onSubmitOC(undefined)}
          className="submit-bar"
          style={{
            width: "8rem",
            border: "1px solid #a82227",
            color: "white",
            background: "#a82227",
            padding: "0.25rem 1rem"
          }}
        >
          {t("Submit")}
        </button>
      )}
      </div>
      </footer>
)}
      {isEmployee && (
        <ActionBar>
          {showAction &&
            workflowDetails?.data?.processInstances[0]?.nextActions && (
              <Menu
                options={workflowDetails?.data?.processInstances[0]?.nextActions?.map(
                  (action) => action.action
                )}
                t={t}
                onSelect={(action) => onSubmitOC(action)}
              />
            )}

          <SubmitBar
            label={t("WF_TAKE_ACTION")}
            onSubmit={() => setShowAction(!showAction)}
          />
        </ActionBar>
)} */}

{/* Citizen Footer */}
{!showModal && isCitizen && (
  <footer style={styles.footer}>
    <div style={styles.inner}>
      <button
        type="button"
        onClick={() => onSubmitOC("APPLY")}
        className="submit-bar"
        style={{
          width: "8rem",
          border: "1px solid #a82227",
          color: "white",
          background: "#a82227",
          padding: "0.25rem 1rem"
        }}
      >
        {t("Submit")}
      </button>
    </div>
  </footer>
)}

{/* Employee Workflow */}
{!showModal && isEmployee && (
  <ActionBar>
    {showAction &&
      workflowDetails?.data?.processInstances?.[0]?.nextActions && (
        <Menu
          options={
            workflowDetails?.data?.processInstances?.[0]?.nextActions?.map(
              (action) => action.action
            ) || []
          }
          t={t}
          // onSelect={(action) => onSubmitOC(action)}
          onSelect={(action) => {
setSelectedAction({ action });
setShowAction(false);
}}
        />
      )}

    <SubmitBar
      label={t("WF_TAKE_ACTION")}
      onSubmit={() => setShowAction(!showAction)}
    />
  </ActionBar>
)}
             {selectedAction && (
   <ActionModal
     t={t}
     action={selectedAction}
     tenantId={tenantId}
     state={workflowDetails?.data?.actionState}
     id={acknowledgementIds}
     applicationDetails={ocSearchData?.ocs?.[0]}
     workflowDetails={workflowDetails}
     businessService="OC"
     moduleCode="OC"
     closeModal={() => setSelectedAction(null)}
     submitAction={(data) => {
       onSubmitOC(selectedAction.action);
       setSelectedAction(null);
}}
/>
)}

    </React.Fragment>
   
  );
};

export default OcBuildingPermit;

const styles = {
    wrapper:{
        background:'#FFFFFF',
        marginLeft:'0.6rem',
        width:'65rem',
        padding:'0.5rem',
        borderRadius:'0.25rem',
        border:'solid 1px #d3d3d3',
        marginBottom:'10rem'
    },
    modal:{
        background:'green'
    },
    header:{
        fontSize:'3rem',
        fontWeight:'900',
        marginBottom:'1rem'
    },
    title:{
        fontSize:'2rem',
        fontWeight:'500',
        marginBottom:'0.5rem'
    },
    buttonwrapper:{
        display:"flex",
        justifyContent:'end',
        marginRight:'2rem'
    },
    submitButton:{
        cursor:'pointer',
        background:'#A52A2A',
        width:'15rem',
        marginTop:'2rem',
        marginBottom:'2rem',
        color:'white',
        fontSize:'1.25rem',
        fontWeight:'400',
        display:'flex',
        justifyContent:'center',
        alignItems:'center'
    },
    docPreviewWrapper:{
        display:'flex',
        flexWrap: 'wrap'
    },
    docPreview:{
        width:'8rem',
        padding:'4px'
    },
    footer: {
        position: 'fixed',
        bottom: 0,
        left: 0,
        width: '100%',
        height: '5rem',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'end',
        background: '#ffffff',
        padding: '0.5rem 1rem',
        boxSizing: 'border-box',
        boxShadow: '0 -0.125rem 0.5rem rgba(0, 0, 0, 0.08)',
        zIndex: 2000
    },
};

// const FORM_JSON = [
//   {
//     formData: [
//       { i18nKey: "NAME_OF_MASTER_PLAN", field: "nameOfMasterPlan", type: "text", isDisabled: true },
//       { i18nKey: "NAME_OF_ULB_PANCHAYAT", field: "nameOfUlbPanchayat", type: "text", isDisabled: true },
//       { i18nKey: "NAME_OF_APPLICANT", field: "nameOfApplicant", type: "text", isRequired: true },
//       { i18nKey: "PHONE_NUMBER", field: "phoneNumber", type: "number", isRequired: true },
//       { i18nKey: "EMAIL", field: "email", type: "text", isRequired: true }
//     ]
//   },
//   {
//     titleKey: "BPA_AREA_MAPPING",
//     formData: [
//       { i18nKey: "DISTRICT", field: "district", type: "dropdown", isDisabled: true },
//       { i18nKey: "PLANNING_AREA", field: "planningArea", type: "dropdown", isDisabled: true },
//       { i18nKey: "PP_AUTHORITY", field: "ppAuthority", type: "dropdown", isDisabled: true },
//       { i18nKey: "BP_AUTHORITY", field: "bpAuthority", type: "dropdown", isDisabled: true },
//       { i18nKey: "MUNICIPAL_BOARD", field: "concernedAuthority", type: "dropdown", isDisabled: true },
//       { i18nKey: "MOUZA", field: "mouza", type: "dropdown", isDisabled: true },
//       { i18nKey: "REVENUE_VILLAGE", field: "revenueVillage", type: "dropdown", isDisabled: true }
//     ]
//   },
//   {
//     titleKey: "SITE_ADDRESS",
//     formData: [
//       { i18nKey: "HOUSE_NO", field: "siteAddressHouseNo", type: "text", disableCondition: (formData) => formData?.siteAddressHouseNo !== "" },
//       { i18nKey: "ADDRESS_LINE_1", field: "siteAddressAddressLineOne", type: "text", isRequired: true, disableCondition: (formData) => formData?.siteAddressAddressLineOne !== "" },
//       { i18nKey: "ADDRESS_LINE_2", field: "siteAddressAddressLineTwo", type: "text", disableCondition: (formData) => formData?.siteAddressAddressLineTwo !== "" },
//       { i18nKey: "STATE", field: "siteAddressState", type: "text", disableCondition: (formData) => formData?.siteAddressState !== "" },
//       { i18nKey: "DISTRICT", field: "siteAddressDistrict", type: "text", disableCondition: (formData) => formData?.siteAddressDistrict !== "" },
//       { i18nKey: "CITY_VILLAGE", field: "siteAddressCityVillage", type: "text", disableCondition: (formData) => formData?.siteAddressCityVillage !== "" },
//       { i18nKey: "PIN_CODE", field: "siteAddressPincode", type: "number", disableCondition: (formData) => formData?.siteAddressPincode !== "" }
//     ]
//   },
//   {
//     titleKey: "CORRESPONDENCE_ADDRESS",
//     formData: [
//       { i18nKey: "HOUSE_NO", field: "correspondenceAddressHouseNo", type: "text", disableCondition: (formData) => formData?.correspondenceAddressHouseNo !== "" },
//       { i18nKey: "ADDRESS_LINE_1", field: "correspondenceAddressAddressLineOne", type: "text", isRequired: true, disableCondition: (formData) => formData?.correspondenceAddressAddressLineOne !== "" },
//       { i18nKey: "ADDRESS_LINE_2", field: "correspondenceAddressAddressLineTwo", type: "text", disableCondition: (formData) => formData?.correspondenceAddressAddressLineTwo !== "" },
//       { i18nKey: "STATE", field: "correspondenceAddressState", type: "text", disableCondition: (formData) => formData?.correspondenceAddressState !== "" },
//       { i18nKey: "DISTRICT", field: "correspondenceAddressDistrict", type: "text", disableCondition: (formData) => formData?.correspondenceAddressDistrict !== "" },
//       { i18nKey: "CITY_VILLAGE", field: "correspondenceAddressCityVillage", type: "text", disableCondition: (formData) => formData?.correspondenceAddressCityVillage !== "" },
//       { i18nKey: "PIN_CODE", field: "correspondenceAddressPincode", type: "number", disableCondition: (formData) => formData?.correspondenceAddressPincode !== "" }
//     ]
//   },
//   {
//     titleKey: "PROPERTY_DETAILS",
//     formData: [
//       { i18nKey: "PROPERTY_ID_PROPERTY_NO", field: "propertyID", type: "text", isRequired: true, isDisabled: true },
//       { i18nKey: "ULB", field: "ulb", type: "text", isDisabled: true },
//       { i18nKey: "WARD", field: "ward", type: "text", isDisabled: true },
//       { i18nKey: "OWNER_NAME", field: "ownerName", type: "text", isDisabled: true },
//       { i18nKey: "GUARDIAN_NAME", field: "guardianName", type: "text", isDisabled: true },
//       { i18nKey: "MOBILE_NUMBER", field: "phone", type: "text", isDisabled: true },
//       { i18nKey: "ADDRESS", field: "address", type: "text", isDisabled: true },
//       { i18nKey: "PROPERTY_VENDOR", field: "propertyVendor", type: "text", isDisabled: true },
//       { i18nKey: "BUILDING_USE", field: "buildingUse", type: "text", isDisabled: true }
//     ]
//   },
//   {
//     titleKey: "OTHER_DETAILS",
//     formData: [
//       { i18nKey: "NOC_NO", field: "nocNo", type: "text", isRequired: true, disableCondition: (formData) => formData?.nocNo !== "" },
//       { i18nKey: "NOC_DATE", field: "nocDate", type: "date", isRequired: true, disableCondition: (formData) => formData?.nocDate !== "" },
//       { i18nKey: "NAME_OF_RTP", field: "nameOfRtp", type: "text", isRequired: true },
//       { i18nKey: "REGISTRATION_NO_RTP", field: "registrationNoRtp", type: "text", isRequired: true },
//       { i18nKey: "PROPOSED_USE_OF_BUILDING", field: "proposeduseOfbuilding", type: "text", isRequired: true },
//       { i18nKey: "NO_OF_FLOORS", field: "noOfFloors", type: "number", isRequired: true },

//       {
//         i18nKey: "IS_PENALTY_APPLICABLE",
//         field: "penaltyApplicable",
//         type: "radio",
//         options: [
//           { i18nKey: "YES", code: "YES" },
//           { i18nKey: "NO", code: "NO" }
//         ],
//         renderCondition: (formData) => formData?.isEmployee === true
//       },

//       {
//         i18nKey: "PENALTY_AMOUNT",
//         field: "penaltyAmount",
//         type: "number",
//         renderCondition: (formData) => formData?.penaltyApplicable?.code === "YES"
//       }
//     ]
//   },
//   {
//     titleKey: "ATTACHMENTS",
//     formData: [
//       { i18nKey: "PLANNING_PERMIT", field: "planningPermit", type: "file" },
//       { i18nKey: "BUILDING_PERMIT", field: "buildingPermit", type: "file" },
//       { i18nKey: "BUILDING_PERMIT_DRAWING", field: "buildingPermitDrawing", type: "file" },
//       { i18nKey: "AS_BUILT_DRAWING", field: "asBuiltDrawing", type: "file" },
//       { i18nKey: "FORM_16", field: "form16", type: "file" },
//       { i18nKey: "FORM_17", field: "form17", type: "file" },
//       { i18nKey: "FORM_18", field: "form18", type: "file" },
//       { i18nKey: "FORM_19", field: "form19", type: "file" },
//       { i18nKey: "FORM_27", field: "form27", type: "file" },
//       { i18nKey: "LIFT_LICENSE", field: "liftLicense", type: "file" },
//       { i18nKey: "POWER_ALLOTMENT_LETTER", field: "powerAllotmentLetter", type: "file" },
//       { i18nKey: "GROUND_WATER_AUTHORITY_CLEARANCE", field: "groundWaterAuthorityClearance", type: "file" },
//       { i18nKey: "BUILDING_PHOTOGRAPH_1", field: "buildingPhotoGraph1", type: "file" },
//       { i18nKey: "BUILDING_PHOTOGRAPH_2", field: "buildingPhotoGraph2", type: "file" },
//       { i18nKey: "BUILDING_PHOTOGRAPH_3", field: "buildingPhotoGraph3", type: "file" },
//       { i18nKey: "BUILDING_PHOTOGRAPH_4", field: "buildingPhotoGraph4", type: "file" }
//     ]
//   }
// ];

const FORM_JSON=[
    {
        formData:[
            {title:'Name of the Master Plan',field:'nameOfMasterPlan',type:'text',isDisabled:true},
            {title:'Name of ULB/Panchayat',field:'nameOfUlbPanchayat',type:'text',isDisabled:true},
            {title:'Name of Applicant',field:'nameOfApplicant',type:'text',isRequired:true},
            {title:'Phone Number',field:'phoneNumber',type:'number',isRequired:true},
            {title:'E-Mail',field:'email',type:'text',isRequired:true}
        ]    
    },
    {
        title: "BPA_AREA_MAPPING",
        formData: [
              {
    title: 'DISTRICT',
    field: 'district',
    type: 'dropdown',
    isDisabled: true
  },
  {
    title: 'PLANNING_AREA',
    field: 'planningArea',
    type: 'dropdown',
    isDisabled: true
  },
  {
    title: 'PP_AUTHORITY',
    field: 'ppAuthority',
    type: 'dropdown',
    isDisabled: true
  },
  {
    title: 'BP_AUTHORITY',
    field: 'bpAuthority',
    type: 'dropdown',
    isDisabled: true
  },
  {
    title: 'MUNICIPAL_BOARD',
    field: 'concernedAuthority',
    type: 'dropdown',
    isDisabled: true
  },
  {
    title: 'MOUZA',
    field: 'mouza',
    type: 'Dropdown',
    isDisabled: true
  },
  {
    title: 'REVENUE_VILLAGE',
    field: 'revenueVillage',
    type: 'dropdown',
    isDisabled: true
  }
]

    },
    {
        title:'Site Address',
        formData:[
            {title:'House No',field:'siteAddressHouseNo',type:'text',disableCondition: (formData) => formData?.siteAddressHouseNo !== ""},
            {title:'Address Line 1',field:'siteAddressAddressLineOne',type:'text',isRequired:true,disableCondition: (formData) => formData?.siteAddressAddressLineOne !== ""},
            {title:'Address Line 2',field:'siteAddressAddressLineTwo',type:'text',disableCondition: (formData) => formData?.siteAddressAddressLineTwo !== ""},
            {title:'State',field:'siteAddressState',type:'text',disableCondition: (formData) => formData?.siteAddressState !== ""},
            {title:'District',field:'siteAddressDistrict',type:'text',disableCondition: (formData) => formData?.siteAddressDistrict !== ""},
            {title:'City/Village',field:'siteAddressCityVillage',type:'text',disableCondition: (formData) => formData?.siteAddressCityVillage !== ""},
            {title:'PIN Code',field:'siteAddressPincode',type:'number',disableCondition: (formData) => formData?.siteAddressPincode !== ""}
        ]
    },
    {
        title:'Correspondence Address',
        formData:[
            {title:'House No',field:'correspondenceAddressHouseNo',type:'text',disableCondition: (formData) => formData?.correspondenceAddressHouseNo !==""},
            {title:'Address Line 1',field:'correspondenceAddressAddressLineOne',type:'text',isRequired:true,disableCondition: (formData) => formData?.correspondenceAddressAddressLineOne !==""},
            {title:'Address Line 2',field:'correspondenceAddressAddressLineTwo',type:'text',disableCondition: (formData) => formData?.correspondenceAddressAddressLineTwo !== ""},
            {title:'State',field:'correspondenceAddressState',type:'text',disableCondition: (formData) => formData?.correspondenceAddressState !== ""},
            {title:'District',field:'correspondenceAddressDistrict',type:'text',disableCondition: (formData) => formData?.correspondenceAddressDistrict !== ""},
            {title:'City/Village',field:'correspondenceAddressCityVillage',type:'text',disableCondition: (formData) => formData?.correspondenceAddressCityVillage !== ""},
            {title:'PIN Code',field:'correspondenceAddressPincode',type:'number',disableCondition: (formData) => formData?.correspondenceAddressPincode !== ""}
        ]
    },
    {
  title: "Property Details",
  formData: [
        {
      title: "Property ID / Property No.",
      field: "propertyID",
      type: "text",
      isRequired: true,
      isDisabled: true
    },
    {
      title: "ULB",
      field: "ulb",
      type: "text",
      isRequired: false,
      isDisabled: true,
    },
    {
      title: "Ward",
      field: "ward",
      type: "text",
      isRequired: false,
      isDisabled: true,
    },
    {
      title: "Owner Name",
      field: "ownerName",
      type: "text",
      isRequired: false,
      isDisabled: true,
    },
    {
      title: "Guardian Name",
      field: "guardianName",
      type: "text",
      isRequired: false,
      isDisabled: true,
    },
    {
      title: "Mobile Number",
      field: "phone",
      type: "text",
      isRequired: false,
      isDisabled: true,
    },
    {
      title: "Address",
      field: "address",
      type: "text",
      isRequired: false,
      isDisabled: true,
    },
    {
      title: "Property Vendor",
      field: "propertyVendor",
      type: "text",
      isRequired: false,
      isDisabled: true,
    },
    {
      title: "Building Use",
      field: "buildingUse",
      type: "text",
      isRequired: false,
      isDisabled: true,
    }
  ]
},
    {
        title:'Other Details',
        formData:[
            {title:'NOC No.',field:'nocNo',type:'text',isRequired:true,disableCondition: (formData) => formData?.nocNo !== ""},
            {title:'NOC Date',field:'nocDate',type:'date',isRequired:true,disableCondition: (formData) => formData?.nocDate !== ""},
            {title:'Name of RTP',field:'nameOfRtp',type:'text',isRequired:true,disableCondition: (formData) => formData?.nameOfRtp !== ""},
            {title:'Registration No. of RTP',field:'registrationNoRtp',type:'text',isRequired:true,disableCondition: (formData) => formData?.registrationNoRtp !== ""},
            {title:'Proposed use of Building',field:'proposeduseOfbuilding',type:'text',isRequired:true,disableCondition: (formData) => formData?.proposeduseOfbuilding !== ""},
            {title:'No. of Floors',field:'noOfFloors',type:'number',isRequired:true,disableCondition: (formData) => formData?.number !== ""},
                //Radio button for penalty
{ 
  title: "Is Penalty Applicable?", 
  field: "penaltyApplicable", 
  type: "radio", 
options: [
  { i18nKey: "YES", code: "YES" },
  { i18nKey: "NO", code: "NO" }
],
  isRequired: false,
  renderCondition: (formData) => formData?.isEmployee === true,
},
    // Penalty amount field that will be conditionally rendered
    { 
        title: 'Penalty Amount', 
        field: 'penaltyAmount', 
        type: 'number', 
        isRequired: false, 
        renderCondition: (formData) => formData?.penaltyApplicable?.code === "YES",
    }
        ]
    },
    {
        title:'Attachments',
        formData:[
            {title:'Planning Permit',field:'planningPermit',type:'file'},
            {title:'Building Permit',field:'buildingPermit',type:'file'},
            {title:'Building Permit Drawing (.pdf Format)',field:'buildingPermitDrawing',type:'file'},
            {title:'As-Built-Drawing (.dwg Format)',field:'asBuiltDrawing',type:'file'},
            {title:'Form 16',field:'form16',type:'file'},
            {title:'Form 17',field:'form17',type:'file'},
            {title:'Form 18',field:'form18',type:'file'},
            {title:'Form 19',field:'form19',type:'file'},
            {title:'Form 27',field:'form27',type:'file'},
            {title:'Lift License',field:'liftLicense',type:'file'},
            {title:'Power Allotment Letter',field:'powerAllotmentLetter',type:'file'},
            {title:'Ground water authority clearance',field:'groundWaterAuthorityClearance',type:'file'},
            {title:'Building Photograph 1',field:'buildingPhotoGraph1',type:'file'},
            {title:'Building Photograph 2',field:'buildingPhotoGraph2',type:'file'},
            {title:'Building Photograph 3',field:'buildingPhotoGraph3',type:'file'},
            {title:'Building Photograph 4',field:'buildingPhotoGraph4',type:'file'},
        ]
    } 
]

const SITE_REPORT_DOCUMENT=[{
    formData:[{title:'Site Verification Report',field:'siteVerificationReport',type:'file'},]
}]

const disableJSON = (originalJson, fieldsToDisable, excludeFields) => {
    let disabledJson;
    if (excludeFields) {
        disabledJson = originalJson?.map((val) => ({
            ...val,
            formData: val?.formData?.map((item) => (fieldsToDisable.includes(item.field) ? { ...item, isDisabled: false } : { ...item, isDisabled: true }))
        }));
    } else if (!excludeFields && fieldsToDisable?.length) {
        disabledJson = originalJson?.map((val) => ({
            ...val,
            formData: val?.formData?.map((item) => (fieldsToDisable.includes(item.field) ? { ...item, isDisabled: true } : { ...item }))
        }));
    } else {
        disabledJson = originalJson?.map((val) => ({ ...val, formData: val?.formData?.map((item) => ({ ...item, isDisabled: true })) }));
    }

    return disabledJson;
};