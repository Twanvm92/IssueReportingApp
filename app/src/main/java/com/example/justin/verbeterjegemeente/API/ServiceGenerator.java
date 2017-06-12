package com.example.justin.verbeterjegemeente.API;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * <code>ServiceGenerator</code> creates a retrofit instance with base url,
 * Gson converter and returns a Retrofit object with a service client.
 *
 * @author Twan van Maastricht
 */

public class ServiceGenerator {

    public static String baseUrl = "http://37.34.59.50/breda/CitySDK/";
    // testing get service requests based on radius
//    public static String baseUrl = "https://asiointi.hel.fi/palautews/rest/v1/";


    private static Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    private static Retrofit builder =
            new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create(gson)).build();

    private static OkHttpClient.Builder httpClient =
            new OkHttpClient.Builder();

    public static void changeApiBaseUrl(String newApiBaseUrl) {
        baseUrl = newApiBaseUrl;

        builder = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseUrl).build();
    }

    /**
     * Creates a retrofit object with a <code>ServiceClient</code> that is provided
     * as a parameter.
     *
     * @param serviceClass a <code>ServiceClient</code>
     * @param <S>          Service class
     * @return a Service
     */
    public static <S> S createService(
            Class<S> serviceClass) {
        return builder.create(serviceClass);
    }


}
