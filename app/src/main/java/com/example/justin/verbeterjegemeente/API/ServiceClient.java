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
    @GET ("services.json")
    Call<List<Service>> getServices(@Query("Locale") String Language);




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
    @Multipart
    @POST("requests.json")
    Call<ArrayList<PostServiceRequestResponse>> postServiceRequest(@Part("api_key") RequestBody apiK,
                                                                   @Part("description") RequestBody desc,
                                                                   @Part("service_code") RequestBody sc,
                                                                   @Part("lat") RequestBody lat,
                                                                   @Part("long") RequestBody lon,
                                                                   @Part MultipartBody.Part img,
                                                                   @Part("email") RequestBody email,
                                                                   @Part("first_name") RequestBody fName,
                                                                   @Part("last_name") RequestBody lName);

    @GET("requests.json")
    Call<ArrayList<ServiceRequest>> getNearbyServiceRequests(@Query("lat") String lat,
                                                             @Query("long") String lng,
                                                             @Query("status") String status,
                                                             @Query("radius") String meters);



    @GET ("requests/{id}.json")
    Call<ArrayList<ServiceRequest>> getServiceById (@Path("id") String id);
}




