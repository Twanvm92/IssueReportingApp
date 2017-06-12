package com.example.justin.verbeterjegemeente.domain;

/**
 * <code>Service</code> is a class that gets created after a get request for services has been send
 * to an open311 interface. The JSON that gets caught in the response is converted to a <code>Service</code> class
 * by a gson converter in the <code>Retrofit</code> class
 *
 * @author Twan van Maastricht
 * @see com.example.justin.verbeterjegemeente.API.ServiceClient#getServices(String)
 * @see retrofit2.Retrofit
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
