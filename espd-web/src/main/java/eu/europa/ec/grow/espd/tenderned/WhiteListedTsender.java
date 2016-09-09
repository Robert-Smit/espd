/*
 * Copyright 2009-2016 PIANOo; TenderNed programma.
 */
package eu.europa.ec.grow.espd.tenderned;

import lombok.Data;

/**
 * espd - Description.
 *
 * @author D Hof
 * @since 07-09-2016
 */

@Data
public class WhiteListedTsender {

    private String whiteListURL;
    private boolean isActive;
    private String passphrase;

    /**
     *
     * @param tenderProperty
     */
    public WhiteListedTsender(String[] tenderProperty) {
        this.whiteListURL = tenderProperty[0];
        this.isActive = "true".equalsIgnoreCase(tenderProperty[1]);
        this.passphrase = tenderProperty[2];
    }
}
