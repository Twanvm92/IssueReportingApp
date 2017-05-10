package com.example.justin.verbeterjegemeente.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by twanv on 10-5-2017.
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
