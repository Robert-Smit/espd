/*
 * Copyright 2009-2016 PIANOo; TenderNed programma.
 */
package eu.europa.ec.grow.espd.tenderned;

import lombok.Data;

/**
 * Data of a Tsender.
 *
 * @author D Hof
 * @since 07-09-2016
 */
@Data
public class TsenderData {

    private String whiteListURL;
    private boolean isActive;
    private String passphraseFilePropertyName;

    /**
     * Constructor.
     * @param tenderProperty is an array of Strings, retrieved from {@link} WhiteListData
     */
    public TsenderData(String[] tenderProperty) {
        whiteListURL = tenderProperty[0];
        isActive = Boolean.parseBoolean(tenderProperty[1]);
        passphraseFilePropertyName = tenderProperty[2];
    }
}
