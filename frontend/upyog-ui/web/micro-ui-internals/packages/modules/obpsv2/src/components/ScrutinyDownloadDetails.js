import React, { useEffect, useState } from "react";
import DocumentsPreview from "../../../templates/ApplicationDetails/components/DocumentsPreview";
import { Loader } from "@upyog/digit-ui-react-components";

const ScrutinyDownloadDetails = ({ edcrNumber }) => {
  const [scrutinyData, setScrutinyData] = useState(null);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    const fetchScrutinyDetails = async () => {
      try {
        setIsLoading(true);

        const response = await Digit.OBPSV2Services.scrutinySearch(
          "assam",
          edcrNumber
        );

        const detail = response?.edcrDetail?.[0] || null;
        setScrutinyData(detail);
      } catch (error) {
        console.error("Scrutiny Details API Error:", error);
        setScrutinyData(null);
      } finally {
        setIsLoading(false);
      }
    };

    if (edcrNumber) {
      fetchScrutinyDetails();
    } else {
      setScrutinyData(null);
    }
  }, [edcrNumber]);

  if (isLoading) {
    return <Loader />;
  }

  if (!edcrNumber || !scrutinyData) {
    return <div>No Record Found</div>;
  }

  const documents = [];

  if (scrutinyData?.dxfFile) {
    documents.push({
      title: "DXF File",
      url: scrutinyData.dxfFile,
      documentType: "DXF",
    });
  }

  if (scrutinyData?.planReport) {
    documents.push({
      title: "Plan Report",
      url: scrutinyData.planReport,
      documentType: "PLAN_REPORT",
    });
  }

  if (scrutinyData?.updatedDxfFile) {
    documents.push({
      title: "Updated DXF File",
      url: scrutinyData.updatedDxfFile,
      documentType: "UPDATED_DXF",
    });
  }

  if (documents.length === 0) {
    return <div>No Record Found</div>;
  }

  return (
    <DocumentsPreview
      documents={[
        {
          values: documents,
        },
      ]}
    />
  );
};

export default ScrutinyDownloadDetails;