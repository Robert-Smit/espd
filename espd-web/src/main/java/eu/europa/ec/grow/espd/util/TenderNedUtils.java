/*
 * Copyright 2009-2016 PIANOo; TenderNed programma.
 */
package eu.europa.ec.grow.espd.util;

import org.joda.time.DateTime;

/**
 * espd - Description.
 *
 * @author D Hof
 * @since 24-06-2016
 */
public class TenderNedUtils {

    public static String createGetUrl(String accessToken, String errorCode) {
        String time = DateTime.now().toString("yyyyMMddHHmmss");
        String getParameters = "";

        return getParameters.concat("?a=")
                .concat(accessToken)
                .concat("&t=")
                .concat(time)
                .concat("&s=")
                .concat("hashcode")
                .concat("&UEU_ERROR_CODE=")
                .concat(errorCode);
    }
}
