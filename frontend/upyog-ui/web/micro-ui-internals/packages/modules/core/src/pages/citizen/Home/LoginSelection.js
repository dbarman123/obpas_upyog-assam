import React from "react";
import { CardBasedOptions, LoginIcon } from "@upyog/digit-ui-react-components";
import { useHistory } from "react-router-dom";
import { useTranslation } from "react-i18next";

const LoginSelection = () => {
  const history = useHistory();
   const { t } = useTranslation();

  const handleLoginSelection = (loginType, path) => {
    Digit.SessionStorage.set("loginType", loginType);
    history.push(path);
  };

  const loginOptions = {
    header: t("SELECT_LOGIN_OPTION"),
    sideOption: {
    },
    options: [
      {
        name: t("CITIZEN_LOGIN"),
        Icon: <LoginIcon />,
        onClick: () => handleLoginSelection("CITIZEN", "/upyog-ui/citizen/login"),
      },
      {
        name: t("RTP_LOGIN"),
        Icon: <LoginIcon />,
        onClick: () => handleLoginSelection("RTP", "/upyog-ui/citizen/rtp/select-location"),
      },
      {
        name: t("EMPLOYEE_LOGIN"),
        Icon: <LoginIcon />,
        onClick: () => handleLoginSelection("EMPLOYEE", "/upyog-ui/employee/login"),
      },
    ],
    styles: { display: "flex", flexWrap: "wrap", justifyContent: "flex-start", width: "100%" },
  };

  return (
    <div className="HomePageContainer" style={{width:"100%"}}>
      <div className="HomePageWrapper">
        <div className="BannerWithSearch">
          <img src={"https://niuatt-filestore.s3.ap-south-1.amazonaws.com/pg/logo/Banner+UPYOG.jpg"} />
          <div className="ServicesSection">
            <CardBasedOptions style={{width: "100%"}} {...loginOptions} />
          </div>
        </div>
      </div>
    </div>
  );
};

export default LoginSelection;