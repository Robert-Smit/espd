/*
 * Copyright 2009-2016 PIANOo; TenderNed programma.
 */
package eu.europa.ec.grow.espd.tenderned;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.joda.time.DateTime;

import eu.europa.ec.grow.espd.util.EspdConfiguration;
import lombok.extern.slf4j.Slf4j;

/**
 * Contains functionality to post multipart forms.
 *
 * @author D Hof
 * @since 16-06-2016
 */
@Slf4j
public class ClientMultipartFormPost {

    public static final String FILENAME_XML = "uea_output.xml";

    public static final String FILENAME_PDF = "uea_output.pdf";

    /**
     * Used to send a POST request to TenderNed.
     *
     * @param xml    The XML to send
     * @param pdf    The PDF to send
     * @param tnData is a {@link TenderNedData} object
     * @throws IOException Thrown if an I/O error occurs
     */
    public void sendPosttoTN(ByteArrayOutputStream xml, ByteArrayOutputStream pdf,
            TenderNedData tnData, TenderNedUtils utils) throws IOException {
        log.info("Sending POST data to {}", tnData.getUploadURL());
        EspdConfiguration espdConfig = utils.getEspdConfiguration();

        // Proxy settings
        HttpHost proxy = new HttpHost(espdConfig.getProxyHostname(), espdConfig.getProxyPort(), HttpHost.DEFAULT_SCHEME_NAME);
        RequestConfig proxyConfig = RequestConfig.custom().setProxy(proxy).build();

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(tnData.getUploadURL());
        httpPost.setEntity(getHttpEntity(xml, pdf, tnData, utils));

        String tenderNedURL = espdConfig.getTendernedURL();

        // use proxy for TSenders
        if (!tnData.getRefererURL().contains(tenderNedURL)) {
            httpPost.setConfig(proxyConfig);
        }

        HttpResponse response = httpClient.execute(httpPost);
        final StatusLine statusLine = response.getStatusLine();
        final int statusCode = statusLine.getStatusCode();
        httpClient.close();

        if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED) {
            log.info("Status {} returned from POST: {}", statusCode, statusLine);

            if (statusCode == HttpStatus.SC_CREATED) {
                tnData.setErrorCode(TenderNedData.ERROR_CODE_OK);
            }
        } else {
            tnData.setErrorCode(TenderNedData.ERROR_CODE_NOK);
            log.error("Status {} returned from POST: {}", statusCode, statusLine);
        }
    }

    private HttpEntity getHttpEntity(ByteArrayOutputStream xml, ByteArrayOutputStream pdf, TenderNedData tnData, TenderNedUtils utils) {
        ByteArrayBody fileBodyXml = new ByteArrayBody(xml.toByteArray(), ContentType.APPLICATION_XML, FILENAME_XML);
        ByteArrayBody fileBodyPdf = new ByteArrayBody(pdf.toByteArray(), ContentType.create("application/pdf"), FILENAME_PDF);

        String time = DateTime.now().toString(TenderNedUtils.TIMESTAMP_FORMAT);
        return MultipartEntityBuilder.create()
                .addPart("xml", fileBodyXml)
                .addPart("pdf", fileBodyPdf)
                .addTextBody("accessToken", tnData.getAccessToken())
                .addTextBody("time", time)
                .addTextBody("security", utils.createSecurityHash(tnData.getAccessToken(), time,
                        tnData.getTsenderData().getPassphraseFilePropertyName()))
                .build();
    }
}
