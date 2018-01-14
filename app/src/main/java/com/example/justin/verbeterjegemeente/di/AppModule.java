package com.example.justin.verbeterjegemeente.di;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.persistence.room.Database;
import android.content.Context;

import com.example.justin.verbeterjegemeente.app.Constants;
import com.example.justin.verbeterjegemeente.app.ServiceRequestApplication;
import com.example.justin.verbeterjegemeente.data.network.ServiceClient;
import com.example.justin.verbeterjegemeente.viewModel.ServiceViewModelFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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

    @Provides
    Context provideContext(ServiceRequestApplication application) {
        return application.getApplicationContext();
    }

    @Singleton
    @Provides
    ViewModelProvider.Factory provideViewModelFactory(
            ViewModelSubComponent.Builder viewModelSubComponent) {

        return new ServiceViewModelFactory(viewModelSubComponent.build());
    }

}