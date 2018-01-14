package com.example.justin.verbeterjegemeente.service.repositories;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.example.justin.verbeterjegemeente.app.Constants;
import com.example.justin.verbeterjegemeente.data.database.ServiceDao;
import com.example.justin.verbeterjegemeente.data.database.ServiceEntry;
import com.example.justin.verbeterjegemeente.data.network.ServiceClient;
import com.example.justin.verbeterjegemeente.service.model.Service;

import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by twanv on 26-11-2017.
 */

@Singleton
public class ServicesRepository {

    private ServiceClient serviceClient;
    private ServiceDao serviceDao;
    private Executor diskIO;
    private boolean mInitialized = false;
    private final MutableLiveData<List<ServiceEntry>> mDownloadedServices;
    private static final String LOG_TAG = "ServiceRepository: ";

    @Inject
    public ServicesRepository(ServiceClient serviceClient, ServiceDao serviceDao,
                              Executor diskIO) {
        this.diskIO = diskIO;
        this.serviceDao = serviceDao;
        this.serviceClient = serviceClient;
        mDownloadedServices = new MutableLiveData<>();

        final LiveData<List<ServiceEntry>> data = getDownloadedServices();

        data.observeForever(newServices -> diskIO.execute(() -> {
            // Deletes old historical data
            serviceDao.deleteAllServices();
            Log.d(LOG_TAG, "Old services deleted");

            // Insert our new weather data into Sunshine's database
            serviceDao.bulkInsert(newServices);
            Log.d(LOG_TAG, "New services inserted");
        }));
    }

    public synchronized void initializeData() {

        // Only perform initialization once per app lifetime. If initialization has already been
        // performed, we have nothing to do in this method.
        if (mInitialized) return;
        mInitialized = true;

        // This method call triggers Sunshine to create its task to synchronize weather data
        // periodically.
//        mWeatherNetworkDataSource.scheduleRecurringFetchWeatherSync();
        Log.d(LOG_TAG, "Fetching new services from network");

        diskIO.execute(this::fetchServicesThroughNetwork);

    }

    void fetchServicesThroughNetwork() {
        serviceClient.getServices(Constants.LANG_EN).enqueue(new Callback<List<ServiceEntry>>() {
            @Override
            public void onResponse(Call<List<ServiceEntry>> call, Response<List<ServiceEntry>> response) {
                Log.d(LOG_TAG, "New services have been downloaded");
//                mDownloadedServices.setValue(response.body());
                mDownloadedServices.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<ServiceEntry>> call, Throwable t) {
                // TODO better error handling in part #2 ...
//                mDownloadedServices.setValue(null);
            }
        });
    }

    public LiveData<List<ServiceEntry>> getCurrentServiceList() {
        initializeData();

        Log.d(LOG_TAG, "Fetching services from DB");
        return serviceDao.getAllServices();
    }

    public LiveData<List<ServiceEntry>> getDownloadedServices() {
        return mDownloadedServices;
    }
}
