/*
 * Copyright 2009-2016 PIANOo; TenderNed programma.
 */
package eu.europa.ec.grow.espd.tenderned;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.europa.ec.grow.espd.tenderned.exception.EncryptionException;
import eu.europa.ec.grow.espd.util.EspdConfiguration;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * espd - TenderNed Utils.
 *
 * @author D Hof
 * @since 24-06-2016
 */
@Component
@Data
@Slf4j
public class TenderNedUtils {

    private final WhiteListUtils whiteList;

    public static final String TIMESTAMP_FORMAT = "yyyyMMddHHmmss";

    private final EspdConfiguration espdConfiguration;

    /**
     * Constructor for TenderNedUtils
     * @param whiteList is a {@link WhiteListUtils} object
     * @param espdConfiguration is a {@link EspdConfiguration} object
     */
    @Autowired
    public TenderNedUtils(WhiteListUtils whiteList,
                          EspdConfiguration espdConfiguration) {
        this.whiteList = whiteList;
        this.espdConfiguration = espdConfiguration;
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
    public String createSecurityHash(String accessToken, String timestamp, String passphrase) {
        StringBuffer hexString = new StringBuffer();

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(accessToken.getBytes());
            md.update(timestamp.getBytes());
            byte[] input = readPasswordFromFile(passphrase);
            md.update(input);

            Arrays.fill(input, (byte) 0);

            byte[] mdbytes = md.digest();

            for (int i = 0; i < mdbytes.length; i++) {
                hexString.append(Integer.toHexString(0xFF & mdbytes[i]));
            }
        } catch (NoSuchAlgorithmException e) {
            log.error(e.getMessage(), e);
        }
        return hexString.toString();
    }

    private byte[] readPasswordFromFile(String passwdFileProperty) {
        String passwordFileName = whiteList.getPassphraseMap().get(passwdFileProperty);
        try {
            // Password for private encryption.
            final File passwordFile = new File(passwordFileName);

            // Chomp to remove line ending characters introduced by the editors.
            final String passwordHex = StringUtils.chomp(FileUtils.readFileToString(passwordFile));

            // Passwords are hex encoded, perform decoding.
            return Hex.decodeHex(passwordHex.toCharArray());
        } catch (final IOException ioException) {
            throw new EncryptionException("Error in reading password file.", ioException);
        } catch (DecoderException decoderException) {
            throw new EncryptionException("Password was not hex encoded.", decoderException);
        }
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
     * The uploadURL, the callbackURL and the refererURL, all need to contain the same whiteListURL
     * @param uploadURL is a String, a parameter send by tender
     * @param callbackURL is a String, a parameter send by tender
     * @param refererURL is a String, the URL where the request comes from.
     * @return a {@link WhiteListedTsender} object. When no WhitelistedTsender is found on the whitelist,
     *         the return will be null.
     */
    public WhiteListedTsender tenderIsOnWhiteList(String uploadURL, String callbackURL, String refererURL) {
        for(Map.Entry entry : whiteList.getWhiteListMap().entrySet()) {
            String key = entry.getKey().toString();
            if(uploadURL.contains(key)
                    && callbackURL.contains(key)
                    && refererURL.contains(key)) {
                return (WhiteListedTsender) entry.getValue();
            }
        }
        return null;
    }
}
