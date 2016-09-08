/*
 * Copyright 2009-2016 PIANOo; TenderNed programma.
 */
package eu.europa.ec.grow.espd.tenderned;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * espd - Description.
 *
 * @author D Hof
 * @since 05-09-2016
 */

@Component
@Data
public class WhiteListUtils {

    private Map<String, WhiteListedTsender> whiteListMap = new HashMap<>();
    private Map<String, String> passphraseMap = new HashMap<>();

    /**
     * This method is called during startup. It sets the tsenders.properties in
     * a {@link WhiteListedTsender} object and puts these in the whitelist Map.
     *
     * @throws IOException
     */
    public void loadTsenderProperties() throws IOException {
        Properties pro = new Properties();
        String fileName = "tsenders.properties";
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(fileName);
        pro.load(in);
        Set<String> propertyNames = pro.stringPropertyNames();

        for (String propertyName : propertyNames) {
            WhiteListedTsender whiteListedTsender = new WhiteListedTsender(pro.getProperty(propertyName).split(","));
            whiteListMap.put(whiteListedTsender.getWhiteListURL(), whiteListedTsender);
        }
    }

    /**
     * This method is called during startup. It will retrieve the properties in sharedpassword.properties
     * and puts these in {@link #passphraseMap}
     *
     * @throws IOException
     */
    @NotNull
    public void loadSharedPasswordProperties() throws IOException {
        Properties pro = new Properties();
        String fileName = "sharedpassword.properties";
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(fileName);
        pro.load(in);
        Set<String> propertyNames = pro.stringPropertyNames();

        for(String propertyName : propertyNames) {
            passphraseMap.put(propertyName, pro.getProperty(propertyName));
        }
    }
}
