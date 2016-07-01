/*
 * Copyright 2009-2016 PIANOo; TenderNed programma.
 */
package eu.europa.ec.grow.espd.domain.tenderned;

import org.joda.time.DateTime;

/**
 * espd - Description.
 *
 * @author D Hof
 * @since 24-06-2016
 */
public class TenderNedUtils {

    private TenderNedUtils() {
        //private constructor to hide the public one.
    }



    /**
     * This method is used to create the Get Parameters for the redirect to TenderNed
     * @param tenderNedData is a {@link TenderNedData} object
     * @return a String
     */
    public static String createGetUrl(TenderNedData tenderNedData) {
        String time = DateTime.now().toString("yyyyMMddHHmmss");
        String callbackUrl = tenderNedData.getCallbackURL();

        return callbackUrl
                .concat("?&a=")
                .concat(tenderNedData.getAccessToken())
                .concat("&t=")
                .concat(time)
                .concat("&s=")
                .concat("hashcode")
                .concat("&UEU_ERROR_CODE=")
                .concat(tenderNedData.getErrorCode());
    }
}
