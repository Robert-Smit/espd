/*
 * Copyright 2009-2016 PIANOo; TenderNed programma.
 */
package eu.europa.ec.grow.espd.tenderned;

import eu.europa.ec.grow.espd.tenderned.exception.EncryptionException;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static org.apache.commons.lang3.CharEncoding.UTF_8;

/**
 * espd - TenderNedEspdEncryption.
 * This class is used for reading the secretSharedPassword.
 * The init method is called when starting the application, this happens in
 * class {@link eu.europa.ec.grow.espd.config.EspdApplication}.
 *
 * @author D Hof
 * @since 29-07-2016
 */

@Component
public class TenderNedEspdEncryption {

    private String sharedTenderNedPassword;

    private byte[] secretSharedPassword;

    public void init() {
        try {
            String secretSharedPasswd = readPasswordFromFile(sharedTenderNedPassword);
            secretSharedPassword = secretSharedPasswd.getBytes(UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Error in reading password file.", e);
        }
    }

    private String readPasswordFromFile(String passwordFileProperty) {
        try {
            // Password for private encryption.
            final File passwordFile = new File(passwordFileProperty);

            // Chomp to remove line ending characters introduced by the editors.
            final String passwordHex = StringUtils.chomp(FileUtils.readFileToString(passwordFile));

            // Passwords are hex encoded, perform decoding.
            return new String(Hex.decodeHex(passwordHex.toCharArray()));
        } catch (final IOException ioException) {
            throw new EncryptionException("Error in reading password file.", ioException);
        } catch (DecoderException decoderException) {
            throw new EncryptionException("Password was not hex encoded.", decoderException);
        }
    }

    public void setSharedTenderNedPassword(String password) {
        this.sharedTenderNedPassword = password;
    }

    public byte[] getSecretSharedPassword() {
        return secretSharedPassword;
    }
}
