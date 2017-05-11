package com.example.justin.verbeterjegemeente.API;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by twanv on 4-5-2017.
 */

public class ServiceGenerator {
    private static final String TEST_BASE_URL = "http://dev.hel.fi/open311-test/v1/";
    public static final String TEST_API_KEY = "f1301b1ded935eabc5faa6a2ce975f6";

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(TEST_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create());



    public static Retrofit retrofit = builder.build();

    private static OkHttpClient.Builder httpClient =
            new OkHttpClient.Builder();

    public static <S> S createService(
            Class<S> serviceClass) {
        return retrofit.create(serviceClass);
    }


}
