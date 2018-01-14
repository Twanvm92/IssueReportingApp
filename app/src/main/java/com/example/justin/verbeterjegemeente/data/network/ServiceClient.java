package com.example.justin.verbeterjegemeente.data.network;

import com.example.justin.verbeterjegemeente.data.database.ServiceEntry;
import com.example.justin.verbeterjegemeente.service.model.PostServiceRequestResponse;
import com.example.justin.verbeterjegemeente.service.model.Service;
import com.example.justin.verbeterjegemeente.service.model.ServiceRequest;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * <code>ServiceClient</code> has several HTTP requests that are send to an API
 * with the help of annotations imported from {@http://square.github.io/retrofit/ Retrofit }.
 *
 * @author Twan van Maastricht
 */


public interface ServiceClient {

    /**
     * Send a get request to an already specified endpoint and returns a list of
     * <code>Service</code> objects
     *
     * @param Language The language in which the result of the request will get returned
     * @return List of Service objects
     * @see Service
     */

    @GET ("services.json")

    Call<List<ServiceEntry>> getServices(@Query("locale") String Language);





    /**
     * Sends a Multipart/form-data post request to an already specified endpoint
     * and returns an Arraylist of <code>PostServiceRequestResponse</code> objects
     * @param service_code The service code of a Service Request. Required.
     * @param description The description of a Service Request. Required
     * @param lat The Latitude of the location of the Service Request. Required.
     * @param lon The longitude of the location of the Service Request. Required.
     * @param address_string The address string of the Service Request. Required.
     * @param address_id The address id of the Service Request. Required.
//     * @param attribute The attributes of the location of the Service Request. Required.
     * @param jurisdiction_id The jurisdiction id of the Service Request.
     * @param media_url The optional image file that can be send with the post request
     * @param email The optional email of the user that made the Service Request.
     * @param first_name The optional front name of the user that made the Service Request
     * @param last_name The optional last name of the user that made the Service Request
     * @return Arraylist<PostServiceRequestResponse>
     * @see PostServiceRequestResponse
     */

    @Multipart
    @POST("requests.json")
    Call<ArrayList<PostServiceRequestResponse>> postServiceRequest(@Part("service_code") RequestBody service_code,
                                                                   @Part("description") RequestBody description,
                                                                   @Part("lat") RequestBody lat,
                                                                   @Part("long") RequestBody lon,
                                                                   @Part("address_string") RequestBody address_string,
                                                                   @Part("address_id") RequestBody address_id,
//                                                                   @Body String[] attribute,
                                                                   @Part("jurisdiction_id") RequestBody jurisdiction_id,
                                                                   @Part("email") RequestBody email,
                                                                   @Part("first_name") RequestBody first_name,
                                                                   @Part("last_name") RequestBody last_name,
                                                                   @Part MultipartBody.Part media_url);

    /**
     * Give priority to a service request. Should only be abe to be done once per user.
     *
     * @param serviceRequestID the id of the service request you want to upvote
     * @param extraDescription the extra description for why you want to give priority. Is mandatory
     * @return
     */
    @POST("upvoteRequest.json")
    Call<ArrayList> upvoteRequest(@Query("service_request_id") String serviceRequestID,
                                  @Query("extraDescription") String extraDescription
    );


    /**
     * Get nearby service requests based on a radius with meters and lat and long that
     * were given as parameters.
     *
     * @param lat    the lattitude of the location.
     * @param lng    the longtitude of the location.
     * @param status the status of a service request. Can be open or closed
     * @param meters the meters for the radius search
     * @return ArrayList<ServiceRequest> A list of service requests
     */

    @GET("requests.json")
    Call<ArrayList<ServiceRequest>> getNearbyServiceRequests(@Query("lat") String lat,
                                                             @Query("long") String lng,
                                                             @Query("status") String status,
                                                             @Query("radius") String meters);


    /**
     * Get nearby service requests based on a radius with meters and lat and long that
     * were given as parameters and a category.
     *
     * @param lat         the lattitude of the location.
     * @param lng         the longtitude of the location.
     * @param status      the status of a service request. Can be open or closed.
     * @param meters      the meters for the radius search.
     * @param serviceCode the service code of the category that is used as a filter
     * @return ArrayList<ServiceRequest> A list of service requests
     */
    @GET("requests.json")
    Call<ArrayList<ServiceRequest>> getNearbyServiceRequests(@Query("lat") String lat,
                                                             @Query("long") String lng,
                                                             @Query("status") String status,
                                                             @Query("radius") String meters,
                                                             @Query("service_code") String serviceCode);

    /**
     * Get nearby service requests based on a radius with meters and lat and long that
     * were given as parameters and earliest updated_datetime to include in the search.
     *
     * @param lat    the lattitude of the location.
     * @param lng    the longtitude of the location.
     * @param status the status of a service request. Can be open or closed
     * @param meters the meters for the radius search
     * @param updatedTime earliest updated_datetime of a service request.
     * @return ArrayList<ServiceRequest> A list of service requests
     */

    @GET("requests.json")
    Call<ArrayList<ServiceRequest>> getClosedNearbyServiceRequests(@Query("lat") String lat,
                                                             @Query("long") String lng,
                                                             @Query("status") String status,
                                                             @Query("radius") String meters,
                                                                   @Query("updated_after") String updatedTime);

    /**
     * Get nearby service requests based on a radius with meters and lat and long that
     * were given as parameters and earliest updated_datetime to include in the search.
     *
     * @param lat    the lattitude of the location.
     * @param lng    the longtitude of the location.
     * @param status the status of a service request. Can be open or closed
     * @param meters the meters for the radius search
     * @param updatedTime earliest updated_datetime of a service request.
     * @param serviceCode the service code of the category that is used as a filter
     * @return ArrayList<ServiceRequest> A list of service requests
     */

    @GET("requests.json")
    Call<ArrayList<ServiceRequest>> getClosedNearbyServiceRequests(@Query("lat") String lat,
                                                                   @Query("long") String lng,
                                                                   @Query("status") String status,
                                                                   @Query("radius") String meters,
                                                                   @Query("service_code") String serviceCode,
                                                                   @Query("updated_after") String updatedTime);

    /**
     * Get specific service requests that are identified by a service ID.
     *
     * @param serviceID       unique identifier for a service request
     * @param jurisdiction_id unique identifier for a jurisdiction
     * @return <code>ServiceRequest</code>
     */

    @GET ("requests.json")
    Call<ArrayList<ServiceRequest>> getServiceById (@Query("service_request_id") String serviceID,
                                         @Query("jurisdiction_id") String jurisdiction_id);
}

