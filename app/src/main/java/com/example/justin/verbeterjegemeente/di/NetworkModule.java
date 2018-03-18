package com.example.justin.verbeterjegemeente.di;

import com.example.justin.verbeterjegemeente.app.Constants;
import com.example.justin.verbeterjegemeente.app.utils.LiveDataCallAdapterFactory;
import com.example.justin.verbeterjegemeente.data.network.ServiceClient;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by twanv on 17-12-2017.
 */
@Module
public class NetworkModule {
    private static final String NAME_BASE_URL = "BREDA_API";

    @Provides
    @Named(NAME_BASE_URL)
    String provideBaseUrlString() {
        return Constants.BASE_URL;
    }

    @Provides
    @Singleton
    Converter.Factory provideGsonConverter() {
        return GsonConverterFactory.create();
    }

    @Provides
    @Singleton
    Retrofit provideRetrofit(Converter.Factory converter, @Named(NAME_BASE_URL) String baseUrl) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(converter)
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .build();
    }

    @Provides
    @Singleton
    ServiceClient provideServiceClient(Retrofit retrofit) {
        return retrofit.create(ServiceClient.class);
    }
}
