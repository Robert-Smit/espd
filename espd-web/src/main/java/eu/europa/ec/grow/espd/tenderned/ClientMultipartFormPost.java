/*
 * Copyright 2009-2016 PIANOo; TenderNed programma.
 */
package eu.europa.ec.grow.espd.tenderned;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.joda.time.DateTime;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
    public void sendPosttoTN(ByteArrayOutputStream xml, ByteArrayOutputStream pdf, TenderNedData tnData, TenderNedUtils utils) throws IOException {
        log.info("Sending POST data to {}", tnData.getUploadURL());

        // Proxy settings
        // TODO: 5-09-2016 TNR-9465 Make proxy server configurable
        HttpHost proxy = new HttpHost("localhost", 8085, HttpHost.DEFAULT_SCHEME_NAME);
        RequestConfig config = RequestConfig.custom().setProxy(proxy).build();

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(tnData.getUploadURL());
        httpPost.setEntity(getHttpEntity(xml, pdf, tnData, utils));

        // TODO: 5-09-2016  TNR-9466 onderscheid TSender/TenderNed voor uitgaande proxy
        boolean useProxy = false;
        if (useProxy) {
            httpPost.setConfig(config);
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
                .addTextBody("security", utils.createSecurityHash(tnData.getAccessToken(), time))
                .build();
    }
}
