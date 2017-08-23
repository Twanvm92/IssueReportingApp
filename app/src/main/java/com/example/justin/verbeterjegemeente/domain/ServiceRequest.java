package com.example.justin.verbeterjegemeente.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * <code>ServiceRequest</code> is a class that gets created after a get request for services requests has been send
 * to an open311 interface. The JSON that gets caught in the response is converted to a <code>ServiceRequest</code> class
 * by a gson converter in the <code>Retrofit</code> class
 *
 * @author Twan van Maastricht
 * @see com.example.justin.verbeterjegemeente.API.ServiceClient#getNearbyServiceRequests(String, String, String, String)
 * @see retrofit2.Retrofit
 */
public class ServiceRequest implements Serializable {

    @SerializedName("service_request_id")
    @Expose
    private String serviceRequestId;
    @SerializedName("service_code")
    @Expose
    private String serviceCode;
    @SerializedName("lat")
    @Expose
    private Double lat;
    @SerializedName("long")
    @Expose
    private Double _long;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("zipcode")
    @Expose
    private String zipcode;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("status_notes")
    @Expose
    private String statusNotes;
    @SerializedName("media_urls")
    @Expose
    private List<String> mediaUrls = null;
    @Expose
    private String requestedDatetime;
    @SerializedName("updated_datetime")
    @Expose
    private String updatedDatetime;
    @SerializedName("upvotes")
    @Expose
    private int upvotes;

    public String getServiceRequestId() {
        return serviceRequestId;
    }

    public void setServiceRequestId(String serviceRequestId) {
        this.serviceRequestId = serviceRequestId;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLong() {
        return _long;
    }

    public void setLong(Double _long) {
        this._long = _long;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusNotes() {
        return statusNotes;
    }

    public void setStatusNotes(String statusNotes) {
        this.statusNotes = statusNotes;
    }

    public String getRequestedDatetime() {
        return requestedDatetime;
    }

    public void setRequestedDatetime(String requestedDatetime) {
        this.requestedDatetime = requestedDatetime;
    }

    public String getUpdatedDatetime() {
        return updatedDatetime;
    }

    public void setUpdatedDatetime(String updatedDatetime) {
        this.updatedDatetime = updatedDatetime;
    }

    public int getUpvotes() {
        return upvotes;
    }

    public List<String> getMediaUrls() {
        return mediaUrls;
    }

    public void setMediaUrls(List<String> mediaUrls) {
        this.mediaUrls = mediaUrls;
    }
}
