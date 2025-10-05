import React from "react";
import { useTranslation } from "react-i18next";
import { CardSubHeader, PDFSvg } from "@upyog/digit-ui-react-components";

function DocumentsPreview({ documents, svgStyles = {}, isSendBackFlow = false, isHrLine = false, titleStyles, hideTitle = false, showAsRows = false }) {
    const { t } = useTranslation();

    return (
        <div style={{ marginTop: "19px" }}>
            <div style={{ display: "flex", flexWrap: "wrap", gap: "20px" }}>
                {documents?.map((document) => 
                    document?.values?.map((value, index) => (
                        <a target="_" href={value?.url} style={{ 
                            display: "flex", 
                            flexDirection: "column", 
                            alignItems: "center", 
                            textDecoration: "none",
                            minWidth: "100px"
                        }} key={index}>
                            <div style={{ 
                                marginBottom: "5px"
                            }}>
                                <PDFSvg />
                            </div>
                            <p style={{ 
                                textAlign: "center", 
                                fontSize: "14px", 
                                fontWeight: "bold", 
                                color: "#505A5F",
                                margin: 0
                            }}>
                                {t(value?.title)}
                            </p>
                        </a>
                    ))
                )}
            </div>
        </div>
    );
}

export default DocumentsPreview;
