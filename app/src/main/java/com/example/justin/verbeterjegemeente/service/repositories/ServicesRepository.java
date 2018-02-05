package com.example.justin.verbeterjegemeente.service.repositories;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.justin.verbeterjegemeente.app.AppExecutors;
import com.example.justin.verbeterjegemeente.app.Constants;
import com.example.justin.verbeterjegemeente.data.database.ServiceDao;
import com.example.justin.verbeterjegemeente.data.database.ServiceEntry;
import com.example.justin.verbeterjegemeente.data.network.ApiResponse;
import com.example.justin.verbeterjegemeente.data.network.Resource;
import com.example.justin.verbeterjegemeente.data.network.ServiceClient;
import com.example.justin.verbeterjegemeente.service.model.Service;

import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by twanv on 26-11-2017.
 */

@Singleton
public class ServicesRepository {

    private ServiceClient serviceClient;
    private ServiceDao serviceDao;
    private AppExecutors appExecutors;
    private boolean mInitialized = false;
//    private final MutableLiveData<List<ServiceEntry>> mDownloadedServices;
    private static final String LOG_TAG = "ServiceRepository: ";

    @Inject
    public ServicesRepository(ServiceClient serviceClient, ServiceDao serviceDao,
                              AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
        this.serviceDao = serviceDao;
        this.serviceClient = serviceClient;
//        mDownloadedServices = new MutableLiveData<>();
//
//        final LiveData<List<ServiceEntry>> data = getDownloadedServices();
//
//        data.observeForever(newServices -> appExecutors.diskIO().execute(() -> {
//            // Deletes old historical data
//            serviceDao.deleteAllServices();
//            Timber.d("Old services deleted");
//
//            // Insert our new weather data into Sunshine's database
//            serviceDao.bulkInsert(newServices);
//            Timber.d("New services inserted");
//        }));
    }

//    private synchronized void initializeData() {
//
//        // Only perform initialization once per app lifetime. If initialization has already been
//        // performed, we have nothing to do in this method.
//        if (mInitialized) return;
//        mInitialized = true;
//
//        // This method call triggers Sunshine to create its task to synchronize weather data
//        // periodically.
////        mWeatherNetworkDataSource.scheduleRecurringFetchWeatherSync();
//        Timber.d("Fetching new services from network");
//
//        appExecutors.diskIO().execute(this::fetchServicesThroughNetwork);
//
//    }
//
//    private void fetchServicesThroughNetwork() {
//        serviceClient.getServices(Constants.LANG_EN).enqueue(new Callback<List<ServiceEntry>>() {
//            @Override
//            public void onResponse(Call<List<ServiceEntry>> call, Response<List<ServiceEntry>> response) {
//                Timber.d("New services have been downloaded");
////                mDownloadedServices.setValue(response.body());
//                mDownloadedServices.setValue(response.body());
//            }
//
//            @Override
//            public void onFailure(Call<List<ServiceEntry>> call, Throwable t) {
//                // TODO better error handling in part #2 ...
////                mDownloadedServices.setValue(null);
//            }
//        });
//    }
//
//    public LiveData<List<ServiceEntry>> getCurrentServiceList() {
//        initializeData();
//
//        Timber.d("Fetching services from DB");
//        return serviceDao.getAllServices();
//    }
//
//    public LiveData<List<ServiceEntry>> getDownloadedServices() {
//        return mDownloadedServices;
//    }

    public LiveData<Resource<ServiceEntry>> loadUser(String login) {
        return new NetworkBoundResource<ServiceEntry,ServiceEntry>(appExecutors) {
            @Override
            protected void saveCallResult(@NonNull ServiceEntry item) {
                userDao.insert(item);
            }

            @Override
            protected boolean shouldFetch(@Nullable ServiceEntry data) {
                return data == null;
            }

            @NonNull
            @Override
            protected LiveData<ServiceEntry> loadFromDb() {
                return userDao.findByLogin(login);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ServiceEntry>> createCall() {
                return serviceClient.getServices(Constants.LANG_EN);
            }
        }.asLiveData();
    }
}
