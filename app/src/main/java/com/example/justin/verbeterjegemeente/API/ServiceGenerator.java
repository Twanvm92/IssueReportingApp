package com.example.justin.verbeterjegemeente.API;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * <code>ServiceGenerator</code> creates a retrofit instance with base url,
 * Gson converter and returns a Retrofit object with a service client.
 * @author Twan van Maastricht
 */

public class ServiceGenerator {
    private static final String TEST_BASE_URL = "http://dev.hel.fi/open311-test/v1/";
    public static final String TEST_API_KEY = "f1301b1ded935eabc5faa6a2ce975f6";

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(TEST_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());



    public static Retrofit retrofit = builder.build();

    private static OkHttpClient.Builder httpClient =
            new OkHttpClient.Builder();

    /**
     * Creates a retrofit object with a <code>ServiceClient</code> that is provided
     * as a parameter.
     * @param serviceClass a <code>ServiceClient</code>
     * @param <S> Service class
     * @return a Service
     */
    public static <S> S createService(
            Class<S> serviceClass) {
        return retrofit.create(serviceClass);
    }


}
