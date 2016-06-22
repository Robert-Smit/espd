/*
 * Copyright 2009-2016 PIANOo; TenderNed programma.
 */
package eu.europa.ec.grow.espd.domain;

import eu.europa.ec.grow.espd.domain.enums.other.Language;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/**
 * espd - Description.
 *
 * @author D Hof
 * @since 07-06-2016
 */
@Data
public class TenderNedData {

    private String uploadURL;

    private String callbackURL;

    private String accessToken;

    private String agent;

    @Setter(AccessLevel.NONE)
    private Language lang;

    private String nameUEArequest;

    private MultipartFile xml;

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

    public void setLang(String lang) {
        this.lang = Language.getByLanguageCode(lang);
    }

}
