package com.example.justin.verbeterjegemeente.domain;

/**
 * Created by twanv on 5-5-2017.
 */

public class Service {

    private String service_code;
    private String service_name;
    private String description;
    private boolean metadeta;
    private String type;
    private String keywords;
    private String group;

    public String getService_code() {
        return service_code;
    }

    public String getService_name() {
        return service_name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isMetadeta() {
        return metadeta;
    }

    public String getType() {
        return type;
    }

    public String getKeywords() {
        return keywords;
    }

    public String getGroup() {
        return group;
    }
}
