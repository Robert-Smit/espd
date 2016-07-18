/*
 * Copyright 2009-2016 PIANOo; TenderNed programma.
 */
package eu.europa.ec.grow.espd.tenderned;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.joda.time.DateTime;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * espd - Description.
 *
 * @author D Hof
 * @since 16-06-2016
 */
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
@Slf4j
public class ClientMultipartFormPost {

    public static final String FILENAME_XML = "uea_output.xml";

    public static final String FILENAME_PDF = "uea_output.pdf";

    /**
     * Used to send a POST request to TenderNed.
     *
     * @param xml    is a byte[]
     * @param tnData is a {@link TenderNedData} object
     * @throws IOException Thrown if an I/O error occurs
     */
    public void sendPosttoTN(byte[] xml, byte[] pdf, TenderNedData tnData) throws IOException {

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(tnData.getUploadURL());

        File xmlFile = new File(FILENAME_XML);
        FileUtils.writeByteArrayToFile(xmlFile, xml);
        FileBody fileBodyXml = new FileBody(xmlFile, ContentType.APPLICATION_XML, FILENAME_XML);
        File pdfFile = new File(FILENAME_XML);
        FileUtils.writeByteArrayToFile(pdfFile, pdf);
        FileBody fileBodyPdf = new FileBody(pdfFile, ContentType.create("application/pdf"), FILENAME_PDF);

        String time = DateTime.now().toString("yyyyMMddHHmmss");

        HttpEntity entity = MultipartEntityBuilder.create()
                .addPart("xml", fileBodyXml)
                .addPart("pdf", fileBodyPdf)
                .addTextBody("accessToken", tnData.getAccessToken())
                .addTextBody("time", time)
                .addTextBody("security", TenderNedUtils.createSecurityHash(tnData.getAccessToken(), time))
                .build();

        httpPost.setEntity(entity);

        log.info("Sending POST");
        HttpResponse response = httpClient.execute(httpPost);
        final int statusCode = response.getStatusLine().getStatusCode();
        httpClient.close();

        if (statusCode != HttpStatus.SC_OK) {
            tnData.setErrorCode("1");
            log.error("Error returned from POST, HTTP status: " + statusCode);
        }
    }

    public String getInputStream(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuilder value = new StringBuilder();
        char[] buffer = new char[1024];
        for (int length = 0; (length = reader.read(buffer)) > 0; ) {
            value.append(buffer, 0, length);
        }
        return  value.toString();
    }

}
