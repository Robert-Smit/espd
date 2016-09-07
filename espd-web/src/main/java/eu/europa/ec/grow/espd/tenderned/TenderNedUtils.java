/*
 * Copyright 2009-2016 PIANOo; TenderNed programma.
 */
package eu.europa.ec.grow.espd.tenderned;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * espd - TenderNed Utils.
 *
 * @author D Hof
 * @since 24-06-2016
 */
@Component
@Slf4j
public class TenderNedUtils {

    private final TenderNedEspdEncryption encryption;

    private final WhiteListUtils whiteList;

    public static final String TIMESTAMP_FORMAT = "yyyyMMddHHmmss";

    /**
     * Constructor for TenderNedUtils
     * @param encryption is a {@link TenderNedEspdEncryption} object, this object is initialized when starting the application
     * @param whiteList is a {@link WhiteListUtils} object
     */
    @Autowired
    public TenderNedUtils(TenderNedEspdEncryption encryption, WhiteListUtils whiteList) {
        this.encryption = encryption;
        this.whiteList = whiteList;
    }

    public TenderNedEspdEncryption getEncryption() {
        return encryption;
    }

    /**
     * This method is used to create the Get Parameters for the redirect to TenderNed
     *
     * @param tenderNedData is a {@link TenderNedData} object
     * @return a String with the callbackURL and get parameters
     */
    public String createGetUrl(TenderNedData tenderNedData) {
        String callbackUrl = tenderNedData.getCallbackURL();

        UrlBuilder urlBuilder = new UrlBuilder(callbackUrl);

        if (tenderNedData.getErrorCode() != null) {
            urlBuilder.addParameter("UEA_ERROR_CODE", tenderNedData.getErrorCode());
        }

        return urlBuilder.build();
    }

    /**
     * This method is for adding headers to the html code that's being saved on
     * the print.jsp page to make the html valid for creating a PDF file.
     *
     * @param html is a String
     * @return a String
     * @throws IOException
     */
    public String addHtmlHeader(String html) throws IOException {
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
    public String createSecurityHash(String accessToken, String timestamp) {
        StringBuffer hexString = new StringBuffer();

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(accessToken.getBytes());
            md.update(timestamp.getBytes());
            md.update(encryption.getSecretSharedPassword());
            byte[] mdbytes = md.digest();

            for (int i = 0; i < mdbytes.length; i++) {
                hexString.append(Integer.toHexString(0xFF & mdbytes[i]));
            }
        } catch (NoSuchAlgorithmException e) {
            log.error(e.getMessage(), e);
        }
        return hexString.toString();
    }

    private class UrlBuilder {
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

    /**
     * Boolean to check if the tender who send params to rest is on the whitelist.
     * The uploadURL, the callbackURL and the requestURL, all need to contain the same whiteListURL
     * @param uploadURL is a String, a parameter send by tender
     * @param callbackURL is a String, a parameter send by tender
     * @param requestURL
     * @return a boolean if tender is on the white list or not
     */
    public boolean tenderIsOnWhiteList(String uploadURL, String callbackURL, String requestURL) {
        List<String> whiteListURLS = whiteList.getWhiteList();
        for(String whiteListURL : whiteListURLS) {
            if(uploadURL.contains(whiteListURL)
                    && callbackURL.contains(whiteListURL)
                    && requestURL.contains(whiteListURL)) {
                return true;
            }
        }
        return false;
    }
}
