/*
 * Copyright 2009-2016 PIANOo; TenderNed programma.
 */
package eu.europa.ec.grow.espd.tenderned;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * espd - TenderNed Utils.
 *
 * @author D Hof
 * @since 24-06-2016
 */
@Service
@Slf4j
public class TenderNedUtils {

    public static final String TIMESTAMP_FORMAT = "yyyyMMddHHmmss";
    public static String SHARED_ESPD_PASSWORD;
    @Value("${shared.espd.password}")
    private String SHARED_ESPD_PASSWORD_NONSTATIC;

    private TenderNedUtils() {
        //private constructor to hide the public one.
    }

    /**
     * This method is used to create the Get Parameters for the redirect to TenderNed
     *
     * @param tenderNedData is a {@link TenderNedData} object
     * @return a String with the callbackURL and get parameters
     */
    public static String createGetUrl(TenderNedData tenderNedData) {
        String time = DateTime.now().toString(TIMESTAMP_FORMAT);
        String callbackUrl = tenderNedData.getCallbackURL();

        return new UrlBuilder(callbackUrl)
                .addParameter("a", tenderNedData.getAccessToken())
                .addParameter("t", time)
                .addParameter("s", createSecurityHash(tenderNedData.getAccessToken(), time))
                .addParameter("UEA_ERROR_CODE", tenderNedData.getErrorCode())
                .build();
    }

    /**
     * This method is for adding headers to the html code that's being saved on
     * the print.jsp page to make the html valid for creating a PDF file.
     *
     * @param html is a String
     * @return a String
     * @throws IOException
     */
    public static String addHtmlHeader(String html) throws IOException {
        String newHtml = StringEscapeUtils.unescapeHtml4(html);
        return "<html><head/><body>" + newHtml + "</div></body></html>";
    }

    /**
     * Creates a security hash
     *
     * @param accessToken is a String, saved in the object {@link TenderNedData}
     * @param timestamp   is a String
     * @return the hexString
     */
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

    @PostConstruct
    public void init() {
        SHARED_ESPD_PASSWORD = SHARED_ESPD_PASSWORD_NONSTATIC;
    }

    private static class UrlBuilder {
        private final StringBuilder url;
        private String querySeparator;

        public UrlBuilder(String url) {
            this.url = new StringBuilder(url);
            querySeparator = url.contains("?") ? "&" : "?";
        }

        public UrlBuilder addParameter(String param, String value) {
            url.append(querySeparator);
            url.append(param);
            url.append("=");
            url.append(value);
            querySeparator = "&";
            return this;
        }

        public String build() {
            return url.toString();
        }
    }
}
