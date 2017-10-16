package com.example.justin.verbeterjegemeente.model;

import com.google.gson.annotations.SerializedName;

/**
 * <code>PostServiceRequestResponse</code> saves the response from a post service request
 * to an open311 interface. The class holds the id and notice of the service request that has been created
 * after the post.
 *
 * @author Twan van Maastricht
 */

public class PostServiceRequestResponse {
    @SerializedName("service_request_id")
    private String id;
    @SerializedName("service_notice")
    private String notice;

    public String getId() {
        return id;
    }

    public String getNotice() {
        return notice;
    }
}
