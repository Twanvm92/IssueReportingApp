package com.example.justin.verbeterjegemeente.API;

import com.example.justin.verbeterjegemeente.domain.Service;
import com.example.justin.verbeterjegemeente.domain.PostServiceRequestResponse;

import java.util.ArrayList;
import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by twanv on 5-5-2017.
 */




public interface ServiceClient {

    public static final String LANG_EN = "en";
    public static final String LANG_NL = "nl";


    /**
     * @param Language
     * @return List of Service objects
     */
@GET ("services.json")
Call<List<Service>> getServices(@Query("Locale") String Language);

@Multipart
@POST("requests.json")
Call<ArrayList<PostServiceRequestResponse>> postServiceRequest(@Part("api_key") RequestBody apiK,
                                                               @Part("description") RequestBody desc,
                                                               @Part("service_code") RequestBody sc,
                                                               @Part("lat") RequestBody lat,
                                                               @Part("long") RequestBody lon);
}




