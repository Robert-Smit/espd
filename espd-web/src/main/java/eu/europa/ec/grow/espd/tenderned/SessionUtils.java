/*
 * Copyright 2009-2016 PIANOo; TenderNed programma.
 */
package eu.europa.ec.grow.espd.tenderned;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * espd - Description.
 *
 * @author D Hof
 * @since 19-07-2016
 */

public class SessionUtils {

    /**
     * private constructor to hide the public one
     */
    private SessionUtils() {
        throw new IllegalAccessError("Utility class");
    }

    /**
     * Method used for removing cookies from ESPD
     * @param request is a {@link HttpServletRequest} object
     * @param response is a {@link HttpServletResponse} object
     */
    public static void removeCookies(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }
    }
}
