/*
 * Copyright 2009-2016 PIANOo; TenderNed programma.
 */
package eu.europa.ec.grow.espd.tenderned;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * espd - Description.
 *
 * @author D Hof
 * @since 05-09-2016
 */

@Configuration
@Component
@PropertySource("classpath:whitelist.properties")
public class WhiteListUtils {

    @Value("${whitelist.url}")
    private String whiteList;

    @Bean
    public static PropertyPlaceholderConfigurer properties(){
        PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
        Resource[] resources = new ClassPathResource[ ]
                { new ClassPathResource( "whitelist.properties" ) };
        ppc.setLocations( resources );
        ppc.setIgnoreUnresolvablePlaceholders( true );
        return ppc;
    }

    public void setWhiteList(String whiteList) {
        this.whiteList = whiteList;
    }

    public List<String> getWhiteList() {
        ArrayList<String> urlArrayList = new ArrayList<>();
        for(String url : whiteList.split(",")) {
            urlArrayList.add(url);
        }
        return urlArrayList;
    }

}
