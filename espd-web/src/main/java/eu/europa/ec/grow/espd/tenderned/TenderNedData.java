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

    public TenderNedData() {
        this.errorCode = "0";
    }

    private String uploadURL;

    private String callbackURL;

    private String accessToken;

    private String agent;

    private String bestandsnaam;

    @Setter(AccessLevel.NONE)
    private String xml;

    @Setter(AccessLevel.NONE)
    private boolean noUpload;

    @Setter(AccessLevel.NONE)
    private boolean noMergeESPDs;

    private String errorCode;

    public void setNoUpload(String noUpload) {
        this.noUpload = Boolean.parseBoolean(noUpload);
    }

    public void setNoMergeESPDs(String noMergeESPDs) {
        this.noMergeESPDs = Boolean.parseBoolean(noMergeESPDs);
    }

    public void setXml(String xml) {
        if("".equals(xml)) {
            this.xml = null;
        } else {
            this.xml = xml;
        }
    }

}
