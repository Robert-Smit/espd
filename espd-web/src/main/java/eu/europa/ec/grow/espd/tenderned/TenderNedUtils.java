/*
 * Copyright 2009-2016 PIANOo; TenderNed programma.
 */
package eu.europa.ec.grow.espd.tenderned;

import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * espd - Description.
 *
 * @author D Hof
 * @since 24-06-2016
 */
@Service
@Slf4j
public class TenderNedUtils {

    public static String SHARED_ESPD_PASSWORD;

    @PostConstruct
    public void init(){
        SHARED_ESPD_PASSWORD = SHARED_ESPD_PASSWORD_NONSTATIC;
    }

    @Value("${shared.espd.password}")
    private String SHARED_ESPD_PASSWORD_NONSTATIC;

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
                .concat("&UEA_ERROR_CODE=")
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
            log.error(e.getMessage(), e);
        }
        return hexString.toString();
    }
}
