/*
 * Copyright 2009-2016 PIANOo; TenderNed programma.
 */
package eu.europa.ec.grow.espd.tenderned;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 * espd - Data received from caller.
 *
 * @author D Hof
 * @since 07-06-2016
 */
@Data
public class TenderNedData {
    public static final String ERROR_CODE_OK = "0";
    public static final String ERROR_CODE_NOK = "1";

    private String accessToken;
    private String agent;
    private String bestandsnaam;
    private String callbackURL;
    private String errorCode;
    private String uploadURL;
    private String xml;

    @Setter(AccessLevel.NONE)
    private boolean isInternationalCode;

    private boolean reuseRequest;

    public void setIsInternationalCode(String nationaalOfEuropeesCode) {
        if (("EU").equalsIgnoreCase(nationaalOfEuropeesCode)) {
            this.isInternationalCode = true;
        }
    }

    /**
     * Constructor for {@link TenderNedData}
     */
    public TenderNedData() {
        this.errorCode = ERROR_CODE_OK;
    }
}
