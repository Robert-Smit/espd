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
 * Holds whitelist data.
 *
 * @author D Hof
 * @since 05-09-2016
 */
@Component
@Data
public class WhiteListData {

    private Map<String, TsenderData> tsenderDataMap = new HashMap<>();
    private Map<String, String> passphraseFilePathnameMap = new HashMap<>();

    /**
     * Performs initialisation.
     * @throws IOException Thrown if an I/O error occurs
     */
    public void init() throws IOException {
        loadSharedPasswordProperties();
        loadTsenderProperties();
    }

    /**
     * This method is called during startup. It sets the tsenders.properties in
     * a {@link TsenderData} object and puts these in the whitelist Map.
     *
     * @throws IOException
     */
    private void loadTsenderProperties() throws IOException {
        Properties properties = loadProperties("tsenders.properties");
        Set<String> propertyNames = properties.stringPropertyNames();

        for (String propertyName : propertyNames) {

            try {
                TsenderData tsenderData = new TsenderData(properties.getProperty(propertyName).split(","));

                String passphraseFilePropertyName = tsenderData.getPassphraseFilePropertyName();
                if (!passphraseFilePathnameMap.containsKey(passphraseFilePropertyName)) {
                    throw new RuntimeException("Shared password property '" + passphraseFilePropertyName + "' not found");
                }

                tsenderDataMap.put(tsenderData.getWhiteListURL(), tsenderData);
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new RuntimeException("Tsender property not properly configured: '" + propertyName + '\'');
            }
        }
    }

    private Properties loadProperties(String fileName) throws IOException {
        Properties properties = new Properties();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
        properties.load(inputStream);
        return properties;
    }

    /**
     * This method is called during startup. It will retrieve the properties in sharedpassword.properties
     * and puts these in {@link #passphraseFilePathnameMap}
     *
     * @throws IOException
     */
    private void loadSharedPasswordProperties() throws IOException {
        Properties properties = loadProperties("sharedpassword.properties");
        Set<String> propertyNames = properties.stringPropertyNames();

        for (String propertyName : propertyNames) {
            passphraseFilePathnameMap.put(propertyName, properties.getProperty(propertyName));
        }
    }
}
