package com.example.justin.verbeterjegemeente.dagger2;

import android.app.Application;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;

import com.example.justin.verbeterjegemeente.app.Constants;
import com.example.justin.verbeterjegemeente.app.ServiceRequestApplication;
import com.example.justin.verbeterjegemeente.data.network.ServiceClient;
import com.example.justin.verbeterjegemeente.viewModel.ServiceViewModelFactory;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by twanv on 16-10-2017.
 */

@Module(subcomponents = ViewModelSubComponent.class)
public class AppModule {
//    private Application application;

    private static final String NAME_BASE_URL = "BREDA_API";

//    public AppModule(Application application) {
//        this.application = application;
//    }

//    @Provides
//    @Singleton
//    public Context provideContext() {
//        return application;
//    }

    @Provides
    Context provideContext(ServiceRequestApplication application) {
        return application.getApplicationContext();
    }

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
                .build();
    }

    @Provides
    @Singleton
    ServiceClient provideServiceClient(Retrofit retrofit) {
        return retrofit.create(ServiceClient.class);
    }

    @Singleton
    @Provides
    ViewModelProvider.Factory provideViewModelFactory(
            ViewModelSubComponent.Builder viewModelSubComponent) {

        return new ServiceViewModelFactory(viewModelSubComponent.build());
    }
}