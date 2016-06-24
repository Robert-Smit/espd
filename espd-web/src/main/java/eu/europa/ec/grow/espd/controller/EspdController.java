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
import eu.europa.ec.grow.espd.domain.TenderNedData;
import eu.europa.ec.grow.espd.domain.enums.other.Country;
import eu.europa.ec.grow.espd.ted.TedRequest;
import eu.europa.ec.grow.espd.ted.TedResponse;
import eu.europa.ec.grow.espd.ted.TedService;
import eu.europa.ec.grow.espd.util.TenderNedUtils;
import eu.europa.ec.grow.espd.xml.EspdExchangeMarshaller;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.output.CountingOutputStream;
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

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@SessionAttributes(value = {"espd", "tenderned"})
@Slf4j
class EspdController {

    private static final String WELCOME_PAGE = "welcome";
    private static final String REQUEST_CA_PROCEDURE_PAGE = "request/ca/procedure";
    private static final String RESPONSE_EO_PROCEDURE_PAGE = "response/eo/procedure";
    private static final String PRINT_PAGE = "response/eo/print";

    private final EspdExchangeMarshaller exchangeMarshaller;

    private final TedService tedService;

    @Autowired
    EspdController(EspdExchangeMarshaller exchangeMarshaller, TedService tedService) {
        this.exchangeMarshaller = exchangeMarshaller;
        this.tedService = tedService;
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
            @RequestPart List<MultipartFile> attachments,
            @ModelAttribute("espd") EspdDocument document,
            @ModelAttribute("tenderned") TenderNedData tenderNedData,
            Model model,
            BindingResult result) throws IOException {
        if ("ca_create_espd_request".equals(action)) {
            return createNewRequestAsCA(country, document);
        }
//        else if ("ca_reuse_espd_request".equals(action)) {
//            return reuseRequestAsCa(document);
//        } else if ("ca_review_espd_response".equals(action)) {
//            return reuseRequestAsCa(document);
//        }
//        } else if ("eo_import_espd".equals(action)) {
//            return importEspdAsEo(country, attachments.get(0), model, result);
//        } else if ("eo_merge_espds".equals(action)) {
//            return mergeTwoEspds(attachments, model, result);
//        }
        return "filter";
    }

    @RequestMapping(value = "/rest", method = POST)
    public String tenderNedData(
            @RequestParam(value = "uploadURL", required = false) String uploadURL,
            @RequestParam(value = "callbackURL", required = false) String callbackURL,
            @RequestParam(value = "accessToken", required = false) String accessToken,
            @RequestParam(value = "lang") String lang,
            @RequestParam(value = "agent", required = true) String agent,
            @RequestParam(value = "tedReceptionId", required = false) String receptionId,
            @RequestParam(value = "ojsNumber", required = false) String ojsNumber,
            @RequestParam(value = "country", required = false) String countryIso,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "procedureTitle", required = false) String procedureTitle,
            @RequestParam(value = "procedureShortDesc", required = false) String procedureShortDescr,
            @RequestParam(value = "fileRefByCA", required = false) String fileRefByCa,
            @RequestParam(value = "noUpload", required = false) String noUpload,
            @RequestParam(value = "noMergeESPDs", required = false) String noMergeESPDs,
//            @RequestPart (value="xml", required = false) MultipartFile attachment,
            @ModelAttribute("tenderned") TenderNedData tenderNedData,
            Model model,
            BindingResult result) throws IOException {

        EspdDocument espd = new EspdDocument();
//        if ("true".equals(noUpload)) {
//            reuseRequestAsCA(attachment, model, tenderNedData, result);
//        } else {
            espd.setTedReceptionId(receptionId);
            espd.setOjsNumber(ojsNumber);
            espd.setProcedureTitle(procedureTitle);
            espd.setProcedureShortDesc(procedureShortDescr);
            espd.setFileRefByCA(fileRefByCa);

            Country country = Country.findByIsoCode(countryIso);
            PartyImpl party = new PartyImpl();
            party.setName(name);
            party.setCountry(country);
            espd.setAuthority(party);
            model.addAttribute("tenderned", tenderNedData);
            model.addAttribute("espd", espd);
            model.addAttribute("authority.country", country);
        return redirectToPage("filter");

//        return redirectToPage("filter?lang=" + lang);
    }


    private String createNewRequestAsCA(Country country, EspdDocument document) {
        document.getAuthority().setCountry(country);
        return redirectToPage(REQUEST_CA_PROCEDURE_PAGE);
    }

    private String reuseRequestAsCa(EspdDocument document) {
        return redirectToPage(REQUEST_CA_PROCEDURE_PAGE);
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

    private String reuseRequestAsCA(MultipartFile attachment, Model model,
            BindingResult result) throws IOException {
        try (InputStream is = attachment.getInputStream()) {
            Optional<EspdDocument> espd = exchangeMarshaller.importEspdRequest(is);
            if (espd.isPresent()) {
                model.addAttribute("espd", espd.get());
                return redirectToPage(REQUEST_CA_PROCEDURE_PAGE);
            }
        }

        result.rejectValue("attachments", "espd_upload_request_error");
        return "filter";
    }

    private String reuseRequestAsCA(MultipartFile attachment, Model model, TenderNedData tenderNedData,
            BindingResult result) throws IOException {
        try (InputStream is = attachment.getInputStream()) {
            Optional<EspdDocument> espd = exchangeMarshaller.importEspdRequest(is);
            if (espd.isPresent()) {
                tenderNedData.setNameUEArequest(attachment.getOriginalFilename());
                model.addAttribute("tenderned", tenderNedData);
                model.addAttribute("espd", espd.get());
                return redirectToPage("filter");
            }
        }
        result.rejectValue("attachments", "espd_upload_request_error");
        return "filter";
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

    private String importEspdAsEo(Country country, MultipartFile attachment, Model model, BindingResult result)
            throws IOException {
        try (InputStream is = attachment.getInputStream()) {
            Optional<EspdDocument> wrappedEspd = exchangeMarshaller.importAmbiguousEspdFile(is);

            // how can wrappedEspd be null???
            if (wrappedEspd != null && wrappedEspd.isPresent()) {
                EspdDocument espd = wrappedEspd.get();
                if (espd.getEconomicOperator() == null) {
                    espd.setEconomicOperator(new EconomicOperatorImpl());
                }
                if (needsToLoadProcurementProcedureInformation(espd)) {
                    // in this case we need to contact TED again to load the procurement information
                    copyTedInformation(espd);
                }
                espd.getEconomicOperator().setCountry(country);
                model.addAttribute("espd", espd);
                return redirectToPage(RESPONSE_EO_PROCEDURE_PAGE);
            }
        }

        result.rejectValue("attachments", "espd_upload_error");
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

    private boolean needsToLoadProcurementProcedureInformation(EspdDocument espdDocument) {
        return isBlank(espdDocument.getOjsNumber()) && isNotBlank(espdDocument.getTedReceptionId());
    }

    @RequestMapping("/{flow:request|response}/{agent:ca|eo}/{step:procedure|exclusion|selection|finish|print}")
    public String view(
            @PathVariable String flow,
            @PathVariable String agent,
            @PathVariable String step,
            @ModelAttribute("espd") EspdDocument espd,
            @ModelAttribute("tenderned") TenderNedData tenderNedData
    ) {
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
    
    @RequestMapping(value = "/{flow:request|response}/{agent:ca|eo}/{step:procedure|exclusion|selection|finish|generate|print|sendtotenderned}", method = POST, params = "next")
    public String next(
            @PathVariable String flow,
            @PathVariable String agent,
            @PathVariable String step,
            @RequestParam String next,
            @ModelAttribute("espd") EspdDocument espd,
            @ModelAttribute("tenderned") TenderNedData tenderNedData,
            HttpServletResponse response,
            BindingResult bindingResult) throws IOException {
        if (bindingResult.hasErrors()) {
            return flow + "_" + agent + "_" + step;
        }
        if ("sendtotenderned".equals(next)) {
            try {
                sendTenderNedData(agent, espd, tenderNedData, response);
                //TODO add errorcode
                String parameters = TenderNedUtils
                        .createGetString(tenderNedData.getAccessToken(), "0");
                return redirectToTN(tenderNedData.getCallbackURL(), parameters);
            } catch (IOException e) {
                throw new RuntimeException("Error", e);
            } catch (Exception e) {
                throw new RuntimeException("Error", e);
            }
        } else if (!"generate".equals(next)) {
            return redirectToPage(flow + "/" + agent + "/" + next);
        }

        downloadEspdFile(agent, espd, response);

        return null;
    }

    private static String redirectToPage(String pageName) {
        return "redirect:/" + pageName;
    }

    private static String redirectToTN(String callbackUrl, String parameters) {
        return "redirect:" + callbackUrl + parameters;
    }

    private void downloadEspdFile(@PathVariable String agent, @ModelAttribute("espd") EspdDocument espd,
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


    public void sendTenderNedData(String agent, EspdDocument espd, TenderNedData tnData, HttpServletResponse response) throws Exception {
        if ("ca".equals(agent)) {
            byte[] xmlString = exchangeMarshaller.generateEspdRequestCa(espd);
            ClientMultipartFormPost formPost = new ClientMultipartFormPost();
            formPost.sendPosttoTN(xmlString, tnData);
        }
    }

    @InitBinder
    private void dateBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        CustomDateEditor editor = new CustomDateEditor(dateFormat, true);
        binder.registerCustomEditor(Date.class, editor);
    }
}
