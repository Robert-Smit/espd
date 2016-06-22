/*
 * Copyright 2009-2016 PIANOo; TenderNed programma.
 */
package eu.europa.ec.grow.espd.controller;

import eu.europa.ec.grow.espd.domain.TenderNedData;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;

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


//    public void sendData(File file) throws Exception {

    public static void sendPostToTN(File xml, TenderNedData tnData) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();

        try {
            HttpPost httppost = new HttpPost(
                    "http://localhost:9191/httpmock/upload");
            httppost.addHeader("Content-type", "multipart/form-data;boundary=---------------------------sdjkahdj294919323195");
            httppost.addHeader("Content-type", "multipart/form-data");
            FileBody fileBodyXML = new FileBody(xml, ContentType.MULTIPART_FORM_DATA);
//            FileBody fileBodyPDF = new FileBody(pdf, ContentType.APPLICATION_ATOM_XML);
//            StringBody accessToken = new StringBody(tnData.getAccessToken(), ContentType.TEXT_PLAIN);
            StringBody accessToken = new StringBody("Lalala", ContentType.TEXT_PLAIN);


            HttpEntity reqEntity = MultipartEntityBuilder.create()
                    .addPart("xml", fileBodyXML)
//                    .addPart("pdf", fileBodyPDF)
                    .addPart("accessToken", accessToken)
                    //TODO time
                    .addPart("time", new StringBody("time"))
//                    .addPart("security", new StringBody("security"))
                    .build();
            System.out.println(reqEntity);

            httppost.addHeader("Location", "http://localhost:9191/httpmock/upload");
            httppost.setEntity(reqEntity);
            System.out.println("executing request " + httppost.getRequestLine());
            CloseableHttpResponse response = httpclient.execute(httppost);
            try {
                System.out.println("----------------------------------------");
                System.out.println(response.getStatusLine());
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    System.out.println("Response content length: " + resEntity.getContentLength());
                } else {
                    throw new IOException("Request is not OK");
                }
                EntityUtils.consume(resEntity);
            } finally
            {
                response.close();
            }
        } finally {
            httpclient.close();
        }
    }
}
