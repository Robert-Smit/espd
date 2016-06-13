/*
 * Copyright 2009-2016 PIANOo; TenderNed programma.
 */
package eu.europa.ec.grow.espd.domain;

import eu.europa.ec.grow.espd.domain.enums.other.Country;
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

    private String callbackURL;

    private String accessToken;

//    private Language language;

    private String agent;

    private String tedReceptionId;

    @Setter(AccessLevel.NONE)
    private Country country;

    @Setter(AccessLevel.NONE)
    private boolean noUpload;

    @Setter(AccessLevel.NONE)
    private boolean noMergeESPDs;

    private String name;

    public void setNoUpload(String noUpload) {
        this.noUpload = Boolean.parseBoolean(noUpload);
    }

    public void setNoMergeESPDs(String noMergeESPDs) {
        this.noMergeESPDs = Boolean.parseBoolean(noMergeESPDs);
    }

    public void setCountry(String countryIso) {
        this.country = Country.findByIsoCode(countryIso);
    }

}
