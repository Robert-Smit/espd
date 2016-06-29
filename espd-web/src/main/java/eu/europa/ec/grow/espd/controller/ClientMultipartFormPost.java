/*
 * Copyright 2009-2016 PIANOo; TenderNed programma.
 */
package eu.europa.ec.grow.espd.controller;

import eu.europa.ec.grow.espd.domain.TenderNedData;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.joda.time.DateTime;

import java.io.IOException;

/**
 * espd - Description.
 *
 * @author D Hof
 * @since 16-06-2016
 */

public class ClientMultipartFormPost {

    private String errorCode = "1";


    /**
     *  used to send a POST request to TenderNed
     * @param xml is a byte[]
     * @param tnData is a {@link TenderNedData} object
     * @return a {@link #errorCode} String.
     * @throws IOException
     */
    public String sendPosttoTN(byte[] xml, TenderNedData tnData) throws IOException {

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost uploadFile = new HttpPost("http://localhost:8080/espd-mock/upload");

        HttpEntity entity = MultipartEntityBuilder.create()
                .addBinaryBody("xml", xml)
//                .addBinaryBody("pdf", pdf)
                .addTextBody("accessToken", tnData.getAccessToken())
                .addTextBody("time", DateTime.now().toString("yyyyMMddHHmmss"))
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

}
