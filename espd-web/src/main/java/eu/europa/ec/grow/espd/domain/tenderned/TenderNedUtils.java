/*
 * Copyright 2009-2016 PIANOo; TenderNed programma.
 */
package eu.europa.ec.grow.espd.domain.tenderned;

import org.joda.time.DateTime;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * espd - Description.
 *
 * @author D Hof
 * @since 24-06-2016
 */
public class TenderNedUtils {

    private static String SHARED_ESPD_PASSWORD = "password";

    private TenderNedUtils() {
        //private constructor to hide the public one.
    }

    /**
     * This method is used to create the Get Parameters for the redirect to TenderNed
     * @param tenderNedData is a {@link TenderNedData} object
     * @return a String
     */
    public static String createGetUrl(TenderNedData tenderNedData) {
        String time = DateTime.now().toString("yyyyMMddHHmmss");
        String callbackUrl = tenderNedData.getCallbackURL();

        return callbackUrl
                .concat("&a=")
                .concat(tenderNedData.getAccessToken())
                .concat("&t=")
                .concat(time)
                .concat("&s=")
                .concat(createSecurityHash(tenderNedData.getAccessToken(), time))
                .concat("&UEU_ERROR_CODE=")
                .concat(tenderNedData.getErrorCode());
    }

    public static String createSecurityHash(String accessToken, String timestamp) {
        StringBuffer hexString = new StringBuffer();

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(accessToken.getBytes());
            md.update(timestamp.getBytes());
            md.update(SHARED_ESPD_PASSWORD.getBytes());

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
