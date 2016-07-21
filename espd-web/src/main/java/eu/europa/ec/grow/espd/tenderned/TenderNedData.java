/*
 * Copyright 2009-2016 PIANOo; TenderNed programma.
 */
package eu.europa.ec.grow.espd.tenderned;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 * espd - Description.
 *
 * @author D Hof
 * @since 07-06-2016
 */
@Data
public class TenderNedData {

    private String accessToken;

    private String agent;

    private String bestandsnaam;

    private String callbackURL;

    private String errorCode;

    private String uploadURL;

    @Setter(AccessLevel.NONE)
    private String xml;

    private boolean reuseRequest;


    /**
     * Constructor for {@link TenderNedData}
     */
    public TenderNedData() {
        this.errorCode = "0";
    }

    public void setXml(String xml) {
        if("".equals(xml)) {
            this.xml = null;
        } else {
            this.xml = xml;
        }
    }

}
