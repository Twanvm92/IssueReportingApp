package com.example.justin.verbeterjegemeente.service.repositories;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.example.justin.verbeterjegemeente.app.Constants;
import com.example.justin.verbeterjegemeente.data.database.ServiceDao;
import com.example.justin.verbeterjegemeente.data.database.ServiceEntry;
import com.example.justin.verbeterjegemeente.data.network.ServiceClient;
import com.example.justin.verbeterjegemeente.service.model.ServiceRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by twanv on 21-1-2018.
 */

@Singleton
public class ServiceRequestsRepository {
    private ServiceClient serviceClient;
    private boolean mInitialized = false;
    private Executor diskIO;
    private final MutableLiveData<List<ServiceRequest>> mDownloadedServiceRequests;
    private static final String LOG_TAG = "ServiceReqRepository: ";

    @Inject
    public ServiceRequestsRepository(ServiceClient serviceClient ,Executor diskIO) {
        this.serviceClient = serviceClient;
        this.diskIO = diskIO;
        mDownloadedServiceRequests = new MutableLiveData<>();

        final LiveData<List<ServiceRequest>> data = getDownloadedServices();

    }

    private void fetchServicesThroughNetwork() {
        serviceClient.getNearbyServiceRequests("51.570980", "4.768833",
                "open", "600").enqueue(new Callback<ArrayList<ServiceRequest>>() {
            @Override
            public void onResponse(Call<ArrayList<ServiceRequest>> call, Response<ArrayList<ServiceRequest>> response) {
                Log.d(LOG_TAG, "New servicerequests have been downloaded");
                for (ServiceRequest sr: response.body()
                     ) {
                    Log.d(LOG_TAG, "service request: " + sr.getDescription());

                }
//                mDownloadedServices.setValue(response.body());
                mDownloadedServiceRequests.postValue(response.body());
            }

            @Override
            public void onFailure(Call<ArrayList<ServiceRequest>> call, Throwable t) {
                // TODO better error handling in part #2 ...
//                mDownloadedServices.setValue(null);
            }
        });
    }

    private synchronized void initializeData() {
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

    public LiveData<List<ServiceRequest>> getCurrentServiceRequestList() {
        initializeData();

        Log.d(LOG_TAG, "Fetching services from DB");
//        return serviceDao.getAllServices();
        return mDownloadedServiceRequests;
    }

    public LiveData<List<ServiceRequest>> getDownloadedServices() {
        return mDownloadedServiceRequests;
    }

}
