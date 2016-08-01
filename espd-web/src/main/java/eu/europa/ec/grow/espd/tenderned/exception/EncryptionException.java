/*
 * Copyright 2009-2016 PIANOo; TenderNed programma.
 */
package eu.europa.ec.grow.espd.tenderned.exception;

/**
 * espd - Description.
 *
 * @author D Hof
 * @since 29-07-2016
 */
public class EncryptionException extends RuntimeException {

    /**
     * @see Exception#Exception(String)
     */
    public EncryptionException(final String message) {
        super(message);
    }

    /**
     * @see Exception#Exception(Throwable)
     */
    public EncryptionException(final Throwable throwable) {
        super(throwable);
    }

    /**
     * @see Exception#Exception(String, Throwable)
     */
    public EncryptionException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

}
