package com.example.justin.verbeterjegemeente.domain;

import com.google.gson.annotations.SerializedName;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * <code>PostServiceRequestResponse</code> saves the response from a post service request
 * to an open311 interface. The class holds the id and notice of the service request that has been created
 * after the post.
 * @author Twan van Maastricht
 * @see com.example.justin.verbeterjegemeente.API.ServiceClient#postServiceRequest(RequestBody,
 * RequestBody, RequestBody, RequestBody, RequestBody, MultipartBody.Part,
 * RequestBody, RequestBody, RequestBody)
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
