package com.example.justin.verbeterjegemeente;

import java.util.List;

import io.victoralbertos.mockery.api.built_in_interceptor.Rx2Retrofit;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Single;

/**
 * Created by twanv on 5-5-2017.
 */



//@Rx2Retrofit(delay = 2500, failurePercent = 15)
public interface ServiceClient {


    /**
     * @param Language
     * @return List of Service objects
     */
@GET ("services.json")
Single<List<Service>> getServices(@Query("Locale") String Language);
}


