package org.egov.noc.endpoint;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.egov.noc.service.AAINOCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@Endpoint
public class NocasApplicationEndpoint {

	private static final String NAMESPACE_URI = "http://egov.org/noc";

	@Autowired
	private AAINOCService nocasApplicationService;

	/**
	 * Handles the SOAP request for fetching NOCAS applications.
	 *
	 * This method receives a GetApplicationsRequest payload and returns all newly
	 * created applications as the SOAP response payload.
	 *
	 * @param request the incoming SOAP request element
	 * @return the XML element containing NOCAS application details
	 * @throws Exception if XML parsing or document creation fails
	 */
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetApplicationsRequest")
	@ResponsePayload
	public Element getApplications(@RequestPayload Element request) throws Exception {

		String applicationsXml = nocasApplicationService.generateNocasXml();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new org.xml.sax.InputSource(new java.io.StringReader(applicationsXml)));

		return doc.getDocumentElement();
	}
}