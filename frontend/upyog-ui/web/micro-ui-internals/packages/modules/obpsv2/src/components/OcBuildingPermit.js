import React, { Fragment, useMemo, useState, useEffect } from "react";
import { FormStep, TextInput, CardLabel,UploadFile, MobileNumber, RadioButtons,PDFSvg,Banner } from "@upyog/digit-ui-react-components";
import axios from 'axios';
import { Link } from "react-router-dom/cjs/react-router-dom.min";
import { useLocation } from "react-router-dom";

const OcBuildingPermit = () => {
    const pathName = window.location.pathname;
    const isEmployee=pathName==='/upyog-ui/employee/obpsv2/ocbpa'

    const [formData,setFormData]=useState({"nameOfMasterPlan": "Kamrup Metro MP", "nameOfUlbPanchayat": "Guwahati Municipal Corporation"})
    const [parameter,setParameter]=useState({action:['Submit Report','Reject']})
    const [isLoading,setIsLoading]=useState(false)
    const [attachments,setAttachments]=useState({})
    const [showAction,setShowAction]=useState(false)
    const [showModal,setShowModal]=useState(false)
    const [modalDetails,setModalDetails]=useState({})
    const location = useLocation();
const prefillData = location.state?.prefillData;

const allFormFields = FORM_JSON.reduce((acc, section) => {
  section?.formData?.forEach(item => {
    if (item.field) acc.push(item.field);
  });
  return acc;
}, []);

useEffect(() => {
  if (prefillData && Object.keys(prefillData).length) {
    updateFormWithPrefill(prefillData);
  }
}, [prefillData]);

const updateFormWithPrefill = (data) => {
  setFormData(prev => {
    const newFormData = { ...prev };

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

  const onSubmitOC=async(action)=>{
    let dataJson={
        ...formData,
        attachments:{
            planningPermit:formData?.planningPermit?.name,
            buildingPermit:formData?.buildingPermit?.name,
            buildingPermitDrawing:formData?.buildingPermitDrawing?.name,
            asBuiltDrawing:formData?.asBuiltDrawing?.name,
            form16:formData?.form16?.name,
            form17:formData?.form17?.name,
            form18:formData?.form18?.name,
            form19:formData?.form19?.name,
            form27:formData?.form27?.name,
            liftLicense:formData?.liftLicense?.name,
            powerAllotmentLetter:formData?.powerAllotmentLetter?.name,
            groundWaterAuthorityClearance:formData?.groundWaterAuthorityClearance?.name,
            buildingPhotoGraph1:formData?.buildingPhotoGraph1?.name,
            buildingPhotoGraph2:formData?.buildingPhotoGraph2?.name,
            buildingPhotoGraph3:formData?.buildingPhotoGraph3?.name,
            buildingPhotoGraph4:formData?.buildingPhotoGraph4?.name,
            siteVerificationReport:formData?.siteVerificationReport?.name,
        }
    }
    const payload = {
        uuid: parameter?.uuid??undefined,
        applicationNo: parameter?.applicationNo??undefined,
        wfCurrentState: getNextWorkflowState(parameter?.wfCurrentState),
        filestoreId: parameter?.filestoreId??undefined,
        dataJson: JSON.stringify(dataJson),
        wfJson: JSON.stringify([
            {
                "currentState": "APPLY_FOR_OC",
                "nextState": "FORWARD_TO_BPA_ENGINEER_GMDA",
                "login": "EMP_001-2025"
            },
            {
                "currentState": "FORWARD_TO_BPA_ENGINEER_GMDA",
                "nextState": "FORWARD_TO_BPA_ASSOCIATE_PLANNER",
                "login": "EMP_002-2025"
            },
            {
                "currentState": "FORWARD_TO_BPA_ASSOCIATE_PLANNER",
                "nextState": "FORWARD_TO_BPA_COMMISSIONER",
                "login": "EMP_003-2025"
            },
            {
                "currentState": "FORWARD_TO_BPA_COMMISSIONER",
                "nextState": "PENDING_BPA_COMMISSIONER_APPROVAL",
                "login": "EMP_004-2025"
            }
            ])

    }

    axios.post("http://localhost:8100/oc-services/v1/oc/_save", payload,
        {
    headers: {
      "Content-Type": "application/json"
    }
  }
    )
        .then(res => {
            console.log("User search response:", res);
            setShowModal(true)
            setModalDetails({applicationNumber:res?.data?.OCRequest?.[0]?.applicationNo, isSuccess:true, message:action==='PENDING_BPA_COMMISSIONER_APPROVAL'?'Occupancy Certificate Application Approved Successfully':action==='FORWARD_TO_BPA_COMMISSIONER'?'Occupancy Certificate Application Successfully Forwarded to BPA Commissioner':action==='FORWARD_TO_BPA_ASSOCIATE_PLANNER'?'Occupance Certificate Application Successfully Forwarded to Associate Planner':action==='FORWARD_TO_BPA_ENGINEER_GMDA'?'Occupancy Certificate Application Successfully Forwarded to Engineer GMDA':'Occupancy Certificate Application Successfully Submitted',
                subMessage:action==='PENDING_BPA_COMMISSIONER_APPROVAL'?'Application Approved':action==='FORWARD_TO_BPA_COMMISSIONER'?'Application Pending for BPA Commissioner Approval':action==='FORWARD_TO_BPA_ASSOCIATE_PLANNER'?'Application pending for Associate Planner Approval':action==='FORWARD_TO_BPA_ENGINEER_GMDA'?'Application pending for Engineer GMDA Approval':'Application Submitted and Pending for Engineer GMDA Approval'})
        })
        .catch(err => {
            console.error("User search error:", err);
        });
  }

  console.log('formData',formData)

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
                <div style={styles.header}>Occupancy Certificate</div>
                {formJson?.map(val=>
                <Fragment>
                    <div style={styles.title}>{val?.title??''}</div>
                    {val?.formData?.map(item=>
                
                    {
                        return (item?.type==='file'?
                            <Fragment>
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
                            </Fragment>: 
                            <Fragment>
                                <CardLabel>{item?.title}{item?.isRequired && <span className="check-page-link-button">*</span>}</CardLabel>
                                <TextInput
                                    type={item?.type}
                                    name={item?.field}
                                    placeholder={`Enter ${item.title}`}
                                    value={formData?.[item.field]??''}
                                    onChange={(e) => handleOnChange(item?.field,e.target.value)}
                                    ValidationRequired={item?.validationRequired}
                                    disabled={item?.isDisabled || pathName==='/upyog-ui/employee/obpsv2/ocbpa'}
                                    // {...{ pattern: "^[a-zA-Z ]+$", title: 'Name of Master Plan is a required field' }}
                                />
                            </Fragment>
                            )}                       
                    )}                
                </Fragment>)                
            }
            {Object.keys(attachments || {}).length>0 && 
                <Fragment>
                    <div style={styles.title}>{'Attachments'}</div>
                    <div style={styles.docPreviewWrapper}>
                        {Object.keys(attachments).map(key => {
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
         {!showModal && <footer style={styles.footer}>
                <div style={styles.inner}>
                    {isEmployee?
                        <div onClick={()=>setShowAction(!showAction)} style={{cursor:'pointer',width:'8rem', border: '1px solid #a82227', color: 'white', background: '#a82227', paddingLeft: '1rem', paddingRight: '1rem', paddingTop: '0.25rem', paddingBottom: '0.25rem',position:'relative' }}> 
                            Take Action
                            {showAction && <div style={{width:'8rem', borderRadius:'0.25rem',position:'absolute',top:'0%',border:'1px solid #d3d3d3',background:'white',top:'-180%',right:'0%',color:'black'}}>
                                                {parameter?.action?.map(val=>{
                                                    return <div style={{padding:'2px', cursor:'pointer'}} onClick={()=>onSubmitOC(val)}>{val}</div>
                                                })}
                                            </div>}
                        </div>
                            :
                            <button type='button' onClick={()=>onSubmitOC(undefined)} className='submit-bar' style={{width:'8rem', border: '1px solid #a82227', color: 'white', background: '#a82227', paddingLeft: '1rem', paddingRight: '1rem', paddingTop: '0.25rem', paddingBottom: '0.25rem' }}>
                                <header>Submit</header>
                            </button>
                    }                    
                </div>
            </footer>}

            
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
        title:'Site Address',
        formData:[
            {title:'House No',field:'siteAddressHouseNo',type:'text'},
            {title:'Address Line 1',field:'siteAddressAddressLineOne',type:'text',isRequired:true},
            {title:'Address Line 2',field:'siteAddressAddressLineTwo',type:'text'},
            {title:'State',field:'siteAddressState',type:'text'},
            {title:'District',field:'siteAddressDistrict',type:'text'},
            {title:'City/Village',field:'siteAddressCityVillage',type:'text'},
            {title:'PIN Code',field:'siteAddressPincode',type:'number'}
        ]
    },
    {
        title:'Correspondence Address',
        formData:[
            {title:'House No',field:'correspondenceAddressHouseNo',type:'text'},
            {title:'Address Line 1',field:'correspondenceAddressAddressLineOne',type:'text',isRequired:true},
            {title:'Address Line 2',field:'correspondenceAddressAddressLineTwo',type:'text'},
            {title:'State',field:'correspondenceAddressState',type:'text'},
            {title:'District',field:'correspondenceAddressDistrict',type:'text'},
            {title:'City/Village',field:'correspondenceAddressCityVillage',type:'text'},
            {title:'PIN Code',field:'correspondenceAddressPincode',type:'number'}
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
      disabled: true
    },
    {
      title: "ULB",
      field: "ulb",
      type: "text",
      isRequired: false,
      disabled: true,
    },
    {
      title: "Ward",
      field: "ward",
      type: "text",
      isRequired: false,
      disabled: true,
    },
    {
      title: "Owner Name",
      field: "ownerName",
      type: "text",
      isRequired: false,
      disabled: true,
    },
    {
      title: "Guardian Name",
      field: "guardianName",
      type: "text",
      isRequired: false,
      disabled: true,
    },
    {
      title: "Mobile Number",
      field: "phone",
      type: "text",
      isRequired: false,
      disabled: true,
    },
    {
      title: "Address",
      field: "address",
      type: "text",
      isRequired: false,
      disabled: true,
    },
    {
      title: "Property Vendor",
      field: "propertyVendor",
      type: "text",
      isRequired: false,
      disabled: true,
    },
    {
      title: "Building Use",
      field: "buildingUse",
      type: "text",
      isRequired: false,
      disabled: true,
    }
  ]
},
    {
        title:'Other Details',
        formData:[
            {title:'NOC No.',field:'nocNo',type:'text',isRequired:true},
            {title:'NOC Date',field:'nocDate',type:'date',isRequired:true},
            {title:'Name of RTP',field:'nameOfRtp',type:'text',isRequired:true},
            {title:'Registration No. of RTP',field:'registrationNoRtp',type:'text',isRequired:true},
            {title:'Proposed use of Building',field:'proposed use of building',type:'text',isRequired:true},
            {title:'No. of Floors',field:'noOfFloors',type:'number',isRequired:true}
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