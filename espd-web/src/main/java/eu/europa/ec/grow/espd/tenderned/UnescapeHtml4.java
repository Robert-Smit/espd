/*
 * Copyright 2009-2016 PIANOo; TenderNed programma.
 */
package eu.europa.ec.grow.espd.tenderned;

import org.apache.commons.lang3.text.translate.AggregateTranslator;
import org.apache.commons.lang3.text.translate.CharSequenceTranslator;
import org.apache.commons.lang3.text.translate.EntityArrays;
import org.apache.commons.lang3.text.translate.LookupTranslator;
import org.apache.commons.lang3.text.translate.NumericEntityUnescaper;

/**
 *
 * @author D Hof
 * @since 26-09-2016
 */
public class UnescapeHtml4 {

    /**
     * private constructor because this class is a utility class
     */
    private UnescapeHtml4() {
    }

    /**
     * This method is written because the method StringUtils.unescapeHtml4() is also looking for
     * EntityArrays.BASIC_ESCAPE(), which we don't need for the PDF transformation.
     * @param input is a String
     * @return a String
     */
    public static final String unescapeHtml4(final String input) {
        return UNESCAPE_HTML4.translate(input);
    }

    private static final CharSequenceTranslator UNESCAPE_HTML4 =
            new AggregateTranslator(
                    new LookupTranslator(EntityArrays.ISO8859_1_UNESCAPE()),
                    new LookupTranslator(EntityArrays.HTML40_EXTENDED_UNESCAPE()),
                    new NumericEntityUnescaper()
            );

}
