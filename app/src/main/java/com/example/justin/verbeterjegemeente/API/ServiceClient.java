package com.example.justin.verbeterjegemeente.API;

import com.example.justin.verbeterjegemeente.domain.Service;
import com.example.justin.verbeterjegemeente.domain.PostServiceRequestResponse;
import com.example.justin.verbeterjegemeente.domain.ServiceRequest;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/** <code>ServiceClient</code> has several HTTP requests that are send to an API
 * with the help of annotations imported from {@http://square.github.io/retrofit/ Retrofit }.
 * @author Twan van Maastricht
 */




public interface ServiceClient {

    /** Send a get request to an already specified endpoint and returns a list of
     * <code>Service</code> objects
     * @param Language The language in which the result of the request will get returned
     * @return List of Service objects
     * @see Service
     */
    @GET ("CitySDK/services.json")
    Call<List<Service>> getServices(@Query("locale") String Language);




    /**
     * Sends a Multipart/form-data post request to an already specified endpoint
     * and returns an Arraylist of <code>PostServiceRequestResponse</code> objects
     * @param apiK The API key that is need to be able to be allowed to send a post request to the endpoint.
     *             Required.
     * @param desc The description of a Service Request. Required
     * @param sc The service code of a Service Request. Required.
     * @param lat The Latitude of the location of the Service Request. Required.
     * @param lon The longitude of the location of the Service Request. Required.
     * @param img The optional image file that can be send with the post request
     * @param email The optional email of the user that made the Service Request.
     * @param fName The optional front name of the user that made the Service Request
     * @param lName The optional last name of the user that made the Service Request
     * @return Arraylist<PostServiceRequestResponse>
     * @see PostServiceRequestResponse
     */
    @POST("CitySDK/requests.json")
    Call<ArrayList<PostServiceRequestResponse>> postServiceRequest(@Query("service_code") String service_code,
                                                                   @Query("description") String description,
                                                                   @Query("lat") Double lat,
                                                                   @Query("long") Double lon,
                                                                   @Query("address_string") String address_string,
                                                                   @Query("address_id") String address_id,
                                                                   @Body String [] attribute,
                                                                   @Query("jurisdiction_id") String jurisdiction_id,
                                                                   @Query("email") String email,
                                                                   @Query("first_name") String first_name,
                                                                   @Query("last_name") String last_name,
                                                                   @Query("media_url") String media_url);


    @GET("CitySDK/requests.json")
    Call<ArrayList<ServiceRequest>> getNearbyServiceRequests(@Query("lat") String lat,
                                                             @Query("long") String lng,
                                                             @Query("status") String status,
                                                             @Query("radius") String meters);

    @GET("CitySDK/requests.json")
    Call<ArrayList<ServiceRequest>> getSimilarServiceRequests(@Query("lat") String lat,
                                                             @Query("long") String lng,
                                                             @Query("status") String status,
                                                             @Query("radius") String meters,
                                                             @Query("service_code") String serviceCode);

    @GET ("CitySDK/request/{id}.json")
    Call<ServiceRequest> getServiceById (@Path("id") String serviceID,
                                                    @Query("jurisdiction_id") String jurisdiction_id);
}




