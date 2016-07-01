/*
 * Copyright 2009-2016 PIANOo; TenderNed programma.
 */
package eu.europa.ec.grow.espd.controller;

import eu.europa.ec.grow.espd.domain.tenderned.TenderNedData;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.joda.time.DateTime;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
public class ClientMultipartFormPost {

    private String errorCode = "1";
    private static String SHARED_ESPD_PASSWORD = "password";


    /**
     *  used to send a POST request to TenderNed
     * @param xml is a byte[]
     * @param tnData is a {@link TenderNedData} object
     * @return a {@link #errorCode} String.
     * @throws IOException
     */
    public String sendPosttoTN(byte[] xml, TenderNedData tnData) throws IOException {

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost uploadFile = new HttpPost(tnData.getUploadURL());

        String time =DateTime.now().toString("yyyyMMddHHmmss");
        HttpEntity entity = MultipartEntityBuilder.create()
                .addBinaryBody("xml", xml)
//                .addBinaryBody("pdf", pdf)
                .addTextBody("accessToken", tnData.getAccessToken())
                .addTextBody("time", time)
                .addTextBody("securityHash", createSecurityHash(tnData.getAccessToken(), time, SHARED_ESPD_PASSWORD))
                .build();

        uploadFile.setEntity(entity);


        HttpResponse response = httpClient.execute(uploadFile);
        final int statusCode = response.getStatusLine().getStatusCode();
        httpClient.close();

        if (statusCode == HttpStatus.SC_OK) {
            errorCode = "0";
        }
        return errorCode;
    }

    public static String createSecurityHash(String accessToken, String timestamp, String sharedEspdPassword) {
        StringBuffer hexString = new StringBuffer();

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(accessToken.getBytes());
            md.update(timestamp.getBytes());
            md.update(sharedEspdPassword.getBytes());

            byte[] mdbytes = md.digest();

            for (int i = 0; i < mdbytes.length; i++) {
                hexString.append(Integer.toHexString(0xFF & mdbytes[i]));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hexString.toString();
    }




}
