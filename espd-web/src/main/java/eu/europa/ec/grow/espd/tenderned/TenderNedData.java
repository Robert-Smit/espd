/*
 * Copyright 2009-2016 PIANOo; TenderNed programma.
 */
package eu.europa.ec.grow.espd.tenderned;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 * espd - Data received from caller.
 *
 * @author D Hof
 * @since 07-06-2016
 */
@Data
public class TenderNedData {

    /** Error code: operation was successful and the documents sent to TenderNed were saved. */
    public static final String ERROR_CODE_OK = "0";

    /** Error code: an error has occured. */
    public static final String ERROR_CODE_NOK = "1";

    private final List<String> inschrijffaseProcedures = new ArrayList<>(Arrays.asList(
            "OPE", "OHP", "OZB"));

    private String accessToken;
    private String agent;
    private String fileName;
    private String callbackURL;
    private String errorCode;
    private String uploadURL;
    private String xml;
    private String nationalOrEuropeanCode;
    private String refererURL;

    @Setter(AccessLevel.NONE)
    private boolean isInschrijffase;

    @Setter(AccessLevel.NONE)
    private boolean isNewResponse;

    private boolean reuseRequest;

    private String contrAuthType;

    /**
     * Constructor for {@link TenderNedData}
     */
    public TenderNedData() {
        super();
    }

    public void setInschrijffase(String typeProcedure) {
        if (inschrijffaseProcedures.contains(typeProcedure)) {
            this.isInschrijffase = true;
        } else {
            this.isInschrijffase = false;
        }
    }

    public void setIsNewResponse(Boolean isNewResponse) {
        this.isNewResponse = isNewResponse;
    }
}
