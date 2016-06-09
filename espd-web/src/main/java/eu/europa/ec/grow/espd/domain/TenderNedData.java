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

    private String languageCode;

    private String agent;

    @Setter(AccessLevel.NONE)
    private Country country;

    private String tedReceptionId;

    private Boolean containsXml;

    public void setCountry(String countryCode) {
        Country.findByIsoCode(countryCode);
    }
}
