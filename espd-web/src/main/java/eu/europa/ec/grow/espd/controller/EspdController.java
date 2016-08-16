/*
 *
 * Copyright 2016 EUROPEAN COMMISSION
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/community/eupl/og_page/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 *
 */

package eu.europa.ec.grow.espd.controller;

import com.google.common.base.Optional;
import eu.europa.ec.grow.espd.domain.EconomicOperatorImpl;
import eu.europa.ec.grow.espd.domain.EspdDocument;
import eu.europa.ec.grow.espd.domain.PartyImpl;
import eu.europa.ec.grow.espd.domain.enums.other.Country;
import eu.europa.ec.grow.espd.ted.TedRequest;
import eu.europa.ec.grow.espd.ted.TedResponse;
import eu.europa.ec.grow.espd.ted.TedService;
import eu.europa.ec.grow.espd.tenderned.*;
import eu.europa.ec.grow.espd.tenderned.exception.PdfRenderingException;
import eu.europa.ec.grow.espd.xml.EspdExchangeMarshaller;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.output.CountingOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@SessionAttributes(value = {"espd", "tenderned"})
@Slf4j class EspdController {

    private static final String WELCOME_PAGE = "welcome";
    private static final String REQUEST_CA_PROCEDURE_PAGE = "request/ca/procedure";
    private static final String RESPONSE_EO_PROCEDURE_PAGE = "response/eo/procedure";
    private static final String PRINT_PAGE = "response/eo/print";
    private static final String SESSION_EXPIRED = "sessionexpired";

    private final EspdExchangeMarshaller exchangeMarshaller;

    private final TedService tedService;

    private final TenderNedUtils utils;

    @Autowired
    EspdController(EspdExchangeMarshaller exchangeMarshaller, TedService tedService, TenderNedUtils utils) {
        this.exchangeMarshaller = exchangeMarshaller;
        this.tedService = tedService;
        this.utils = utils;
    }

    private static String redirectToPage(String pageName) {
        return "redirect:/" + pageName;
    }

    private static String redirectToTN(String callbackUrl) {
        return "redirect:" + callbackUrl;
    }

    @ModelAttribute("espd")
    public EspdDocument newDocument() {
        return new EspdDocument();
    }

    @ModelAttribute("tenderned")
    public TenderNedData tenderNedData() {
        return new TenderNedData();
    }

    @RequestMapping("/")
    public String index() {
        return WELCOME_PAGE;
    }

    @RequestMapping("/{page:filter|contact}")
    public String getPage(@PathVariable String page) {
        return page;
    }

    @RequestMapping(value = "/welcome")
    public String cancel(SessionStatus status) {
        try {
            return WELCOME_PAGE;
        } finally {
            status.setComplete();
        }
    }

    @RequestMapping(value = "/filterESPD", params = "action", method = POST)
    public String whoAreYouScreen(
            @RequestParam("authority.country") Country country,
            @RequestParam String action,
            @ModelAttribute("espd") EspdDocument document,
            @ModelAttribute("tenderned") TenderNedData tenderNedData,
            Model model,
            BindingResult result) throws IOException {

        if ("ca_create_espd_request".equals(action)) {
            return createNewRequestAsCA(country, document, tenderNedData.getNationaalOfEuropeesCode());
        } else if ("ca_reuse_espd_request".equals(action)) {
            return redirectToPage(REQUEST_CA_PROCEDURE_PAGE);
        } else if ("eo_import_espd".equals(action)) {
            return redirectToPage(RESPONSE_EO_PROCEDURE_PAGE);
        }
        return "filter";
    }

    @RequestMapping(value = "/rest", method = POST)
    public String tenderNedData(
            @RequestParam(value = "uploadURL", required = false) String uploadURL,
            @RequestParam(value = "callbackURL", required = false) String callbackURL,
            @RequestParam(value = "accessToken", required = false) String accessToken,
            @RequestParam(value = "lang") String lang,
            @RequestParam(value = "agent", required = true) String agent,
            @RequestParam(value = "tedReceptionId", required = false) String tedReceptionId,
            @RequestParam(value = "ojsNumber", required = false) String ojsNumber,
            @RequestParam(value = "country", required = false) String countryIso,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "procedureTitle", required = false) String procedureTitle,
            @RequestParam(value = "procedureShortDesc", required = false) String procedureShortDescr,
            @RequestParam(value = "fileRefByCA", required = false) String fileRefByCa,
            @RequestParam(value = "adres", required = false) String adres,
            @RequestParam(value = "postcode", required = false) String postcode,
            @RequestParam(value = "plaats", required = false) String plaats,
            @RequestParam(value = "internetadres", required = false) String internetadres,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "telefoonnummer", required = false) String telefoonnummer,
            @RequestParam(value = "btwNummer", required = false) String btwNummer,
            @RequestParam(value = "kvkNummer", required = false) String kvkNummer,
            @RequestParam(value = "nationaalOfEuropeesCode", required = false) String nationaalOfEuropeesCode,
            @RequestParam(value = "isNewResponse", required = false) String isNewResponse,
            @RequestParam(value = "bestandsnaam", required = false) String bestandsnaam,
            @RequestParam(value = "xml", required = false) String xml,
            @ModelAttribute("tenderned") TenderNedData tenderNedData,
            Model model,
            BindingResult result) throws IOException {

        Country country = Country.findByIso2Code(countryIso);
        EspdDocument espd = new EspdDocument();
        boolean reuseRequest = false;

        if ("eo".equals(agent)) {
            espd = importEspdAsEo(country, tenderNedData.getXml());

            if ("true".equals(isNewResponse)) {
                EconomicOperatorImpl economicOperator = new EconomicOperatorImpl();
                PartyImpl party = new PartyImpl();
                party.setName(name);
                party.setWebsite(internetadres);
                party.setVatNumber(btwNummer);
                party.setAnotherNationalId(kvkNummer);
                party.setStreet(adres);
                party.setPostalCode(postcode);
                party.setCity(plaats);
                party.setCountry(country);
                party.setContactEmail(email);
                party.setContactPhone(telefoonnummer);
                economicOperator.copyProperties(party);
                espd.setEconomicOperator(economicOperator);
            }
        } else if (StringUtils.isNotEmpty(tenderNedData.getXml())) {
            espd = reuseRequestAsCA(tenderNedData.getXml(), tedReceptionId, ojsNumber, procedureTitle,
                    procedureShortDescr, fileRefByCa);
            reuseRequest = true;
        } else {
            espd.setTedReceptionId(tedReceptionId);
            espd.setOjsNumber(ojsNumber);
            espd.setProcedureTitle(procedureTitle);
            espd.setProcedureShortDesc(procedureShortDescr);
            espd.setFileRefByCA(fileRefByCa);
            PartyImpl party = new PartyImpl();
            party.setName(name);
            party.setCountry(country);
            espd.setAuthority(party);
        }
        tenderNedData.setReuseRequest(reuseRequest);

        model.addAttribute("tenderned", tenderNedData);
        model.addAttribute("espd", espd);
        model.addAttribute("authority.country", country);
        return redirectToPage("filter");
    }

    private EspdDocument importEspdAsEo(Country country, String attachment) throws IOException {
        EspdDocument espd = new EspdDocument();
        InputStream inputStream = new ByteArrayInputStream(attachment.getBytes(StandardCharsets.UTF_8));
        Optional<EspdDocument> wrappedEspd = exchangeMarshaller.importAmbiguousEspdFile(inputStream);

        if (wrappedEspd != null && wrappedEspd.isPresent()) {
            espd = wrappedEspd.get();

            if (espd.getEconomicOperator() == null) {
                espd.setEconomicOperator(new EconomicOperatorImpl());
            }
            espd.getEconomicOperator().setCountry(country);
        }
        return espd;
    }

    private boolean needsToLoadProcurementProcedureInformation(EspdDocument espdDocument) {
        return isBlank(espdDocument.getOjsNumber()) && isNotBlank(espdDocument.getTedReceptionId());
    }

    private void copyTedInformation(EspdDocument document) {
        TedResponse tedResponse = tedService
                .getContractNoticeInformation(TedRequest.builder().receptionId(document.getTedReceptionId()).build());
        document.setOjsNumber(tedResponse.getNoDocOjs());
        TedResponse.TedNotice notice = tedResponse.getFirstNotice();
        document.getAuthority().setName(notice.getOfficialName());
        document.setProcedureTitle(notice.getTitle());
        document.setProcedureShortDesc(notice.getShortDescription());
        document.setFileRefByCA(notice.getReferenceNumber());
        document.setTedUrl(notice.getTedUrl());
    }

    private EspdDocument reuseRequestAsCA(String attachment, String tedReceptionId, String ojsNumber,
            String procedureTitle, String procedureShortDescr, String fileRefByCa) {

        InputStream is = new ByteArrayInputStream(attachment.getBytes(StandardCharsets.UTF_8));
        Optional<EspdDocument> optional = exchangeMarshaller.importEspdRequest(is);
        EspdDocument espdDocument = optional.get();

        if (StringUtils.isEmpty(espdDocument.getTedReceptionId()) && StringUtils.isNotEmpty(tedReceptionId)) {
            espdDocument.setTedReceptionId(tedReceptionId);
        }

        if (StringUtils.isEmpty(espdDocument.getOjsNumber()) && StringUtils.isNotEmpty(ojsNumber)) {
            espdDocument.setOjsNumber(ojsNumber);
        }

        if (StringUtils.isEmpty(espdDocument.getProcedureTitle()) && StringUtils.isNotEmpty(procedureTitle)) {
            espdDocument.setProcedureTitle(procedureTitle);
        }

        String procedureShortDescrEspd = espdDocument.getProcedureShortDesc();
        if ((StringUtils.isEmpty(procedureShortDescrEspd) || "-".equals(procedureShortDescrEspd)) && StringUtils.isNotEmpty(procedureShortDescr)) {
            espdDocument.setProcedureShortDesc(procedureShortDescr);
        }

        if (StringUtils.isEmpty(espdDocument.getFileRefByCA()) && StringUtils.isNotEmpty(fileRefByCa)) {
            espdDocument.setFileRefByCA(fileRefByCa);
        }
        return espdDocument;
    }

    private String createNewRequestAsCA(Country country, EspdDocument document, String nationaalOfEuropeesCode) {
        document.getAuthority().setCountry(country);
        document.selectCAExclusionCriteria(nationaalOfEuropeesCode);
        return redirectToPage(REQUEST_CA_PROCEDURE_PAGE);
    }

    private String reviewResponseAsCA(MultipartFile attachment, Model model,
            BindingResult result) throws IOException {

        try (InputStream is = attachment.getInputStream()) {
            Optional<EspdDocument> espd = exchangeMarshaller.importEspdResponse(is);

            if (espd.isPresent()) {
                model.addAttribute("espd", espd.get());
                return redirectToPage(PRINT_PAGE);
            }
        }

        result.rejectValue("attachments", "espd_upload_response_error");
        return "filter";
    }

    private String mergeTwoEspds(List<MultipartFile> attachments, Model model, BindingResult result)
            throws IOException {

        try (InputStream reqIs = attachments.get(1).getInputStream();
                InputStream respIs = attachments.get(2).getInputStream()) {

            Optional<EspdDocument> wrappedEspd = exchangeMarshaller.mergeEspdRequestAndResponse(reqIs, respIs);

            if (wrappedEspd.isPresent()) {
                model.addAttribute("espd", wrappedEspd.get());
                return redirectToPage(RESPONSE_EO_PROCEDURE_PAGE);
            }
        }

        result.rejectValue("attachments", "espd_upload_error");
        return "filter";
    }

    private String createNewResponseAsEO(Country country, EspdDocument document) {

        if (document.getEconomicOperator() == null) {
            document.setEconomicOperator(new EconomicOperatorImpl());
        }
        document.getEconomicOperator().setCountry(country);
        document.giveLifeToAllExclusionCriteria();
        document.giveLifeToAllSelectionCriteria();
        return redirectToPage(RESPONSE_EO_PROCEDURE_PAGE);
    }

    @RequestMapping("/{flow:request|response}/{agent:ca|eo}/{step:procedure|exclusion|selection|finish|print}")
    public String view(
            @PathVariable String flow,
            @PathVariable String agent,
            @PathVariable String step,
            @ModelAttribute("espd") EspdDocument espd,
            @ModelAttribute("tenderned") TenderNedData tenderNedData) {

        return flow + "_" + agent + "_" + step;
    }

    @RequestMapping(value = "/{flow:request|response}/{agent:ca|eo}/{step:procedure|exclusion|selection|finish|print}", method = POST, params = "prev")
    public String previous(
            @PathVariable String flow,
            @PathVariable String agent,
            @PathVariable String step,
            @RequestParam String prev,
            @ModelAttribute("espd") EspdDocument espd,
            @ModelAttribute("tenderned") TenderNedData tenderNedData,
            BindingResult bindingResult) {

        return bindingResult.hasErrors() ?
                flow + "_" + agent + "_" + step : redirectToPage(flow + "/" + agent + "/" + prev);
    }

    @RequestMapping(value = "/{flow:request|response}/{agent:ca|eo}/{step:procedure|exclusion|selection|finish|print}", method = POST, params = "print")
    public String print(
            @PathVariable String flow,
            @PathVariable String agent,
            @PathVariable String step,
            @RequestParam String print,
            @ModelAttribute("espd") EspdDocument espd,
            @ModelAttribute("tenderned") TenderNedData tenderNedData,
            BindingResult bindingResult) {

        return bindingResult.hasErrors() ?
                flow + "_" + agent + "_" + step : redirectToPage(flow + "/" + agent + "/print");
    }

    @RequestMapping(value = "/{flow:request|response}/{agent:ca|eo}/{step:procedure|exclusion|selection|finish|generate|print|savePrintHtml|null}",
            method = POST, params = "next")
    public String next(
            @PathVariable String flow,
            @PathVariable String agent,
            @PathVariable String step,
            @RequestParam String next,
            @ModelAttribute("espd") EspdDocument espd,
            @ModelAttribute("tenderned") TenderNedData tenderNedData,
            HttpServletRequest request,
            HttpServletResponse response,
            BindingResult bindingResult,
            SessionStatus status,
            Model model) throws PdfRenderingException, IOException {

        if (bindingResult.hasErrors()) {
            return flow + "_" + agent + "_" + step;
        }

        if ("savePrintHtml".equals(next)) {
            espd.setHtml(utils.addHtmlHeader(espd.getHtml()));
            sendTenderNedData(espd, tenderNedData);
            String callbackUrl = utils.createGetUrl(tenderNedData);
            //tijdelijk voor het opslaan van html
            HtmlToPdfTransformer.saveHtml(espd.getHtml());

            try {
                return redirectToTN(callbackUrl);
            } finally {
                SessionUtils.removeCookies(request, response);
                status.setComplete();
            }
        }
        return redirectToPage(flow + "/" + agent + "/" + next);
    }

    private void downloadEspdFile(
            @PathVariable String agent,
            @ModelAttribute("espd") EspdDocument espd,
            HttpServletResponse response) throws IOException {

        try (CountingOutputStream out = new CountingOutputStream(response.getOutputStream())) {
            response.setContentType(APPLICATION_XML_VALUE);

            if ("eo".equals(agent)) {
                response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"espd-response.xml\"");
                exchangeMarshaller.generateEspdResponse(espd, out);
            } else {
                response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"espd-request.xml\"");
                exchangeMarshaller.generateEspdRequest(espd, out);
            }
            response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(out.getByteCount()));
            out.flush();
        }
    }

    /**
     * This method creates a xml and pdf files and will be send back to TenderNed.
     * If something goes wrong while creating these files, the callback URL
     * to TenderNed will be called without sending a post.
     *
     * @param espd   is a {@link EspdDocument} object
     * @param tnData is a {@link TenderNedData} object
     * @throws IOException
     */
    private void sendTenderNedData(EspdDocument espd, TenderNedData tnData)
            throws IOException {

        boolean errorOccured = false;
        ByteArrayOutputStream xml;

        if ("ca".equals(tnData.getAgent())) {
            xml = (ByteArrayOutputStream) exchangeMarshaller.generateEspdRequestCa(espd);
        } else {
            xml = (ByteArrayOutputStream) exchangeMarshaller.generateEspdResponse(espd);
        }

        log.debug("Created XML: {}", xml);
        ByteArrayOutputStream pdf = null;

        try {
            HtmlToPdfTransformer pdfTransformer = new HtmlToPdfTransformer();
            pdf = pdfTransformer.convertToPDF(espd.getHtml(), tnData.getAgent());
        } catch (PdfRenderingException e) {
            errorOccured = true;
            log.error("Error rendering PDF: ", e);
        }

        // if the pdf has a length of 15, an error occurred while transforming HTML to PDF.
        if (pdf != null && pdf.size() == 15) {
            errorOccured = true;
            log.error("Error transforming HTML to PDF");
        }

        if (errorOccured) {
            tnData.setErrorCode(TenderNedData.ERROR_CODE_NOK);
        } else {
            log.debug("Successfully created XML and PDF");
            ClientMultipartFormPost formPost = new ClientMultipartFormPost();
            formPost.sendPosttoTN(xml, pdf, tnData, utils.getEncryption());
        }
    }

    @InitBinder
    private void dateBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        CustomDateEditor editor = new CustomDateEditor(dateFormat, true);
        binder.registerCustomEditor(Date.class, editor);
    }

    /**
     * Method created when clicking on the cancel button.
     * The user will return to TenderNed and the session and cookies will be removed.
     *
     * @param tenderNedData is a {@link TenderNedData} object
     * @param status        is a {@link SessionStatus} object
     * @param request       is a {@link HttpServletRequest} object
     * @param response      is a {@link HttpServletResponse} object
     * @return a String, which is the callback URL to TenderNed
     * before returning to TenderNed, the session is set to complete
     * and the cookies will be deleted.
     */
    @RequestMapping(value = "/cancel")
    public String cancel(
            @ModelAttribute("tenderned") TenderNedData tenderNedData,
            SessionStatus status,
            HttpServletRequest request,
            HttpServletResponse response) {

        try {
            String callbackUrl = tenderNedData.getCallbackURL();
            return "redirect:" + callbackUrl;
        } finally {
            SessionUtils.removeCookies(request, response);
            status.setComplete();
        }
    }

    /**
     * If we have a value 'null' as a path variable we can assume the session was expired.
     *
     * @return
     */
    @RequestMapping("**/null/**")
    public String getPage() {
        return SESSION_EXPIRED;
    }
}
