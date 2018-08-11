package com.example.justin.verbeterjegemeente.service.repositories;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
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
import com.example.justin.verbeterjegemeente.service.model.ServiceRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by twanv on 21-1-2018.
 */

@Singleton
public class ServiceRequestsRepository {
    private ServiceClient serviceClient;
    private AppExecutors appExecutors;

    @Inject
    public ServiceRequestsRepository(ServiceClient serviceClient ,AppExecutors appExecutors) {
        this.serviceClient = serviceClient;
        this.appExecutors = appExecutors;
    }

    public LiveData<Resource<List<ServiceRequest>>> refreshServiceRequests(
            @NonNull MediatorLiveData<Resource<List<ServiceRequest>>> dataToUpdate,
            @NonNull String status, @NonNull String serviceCode) {
        return
                new NetworkBoundResourceNoRoom<List<ServiceRequest>, List<ServiceRequest>>(
                        appExecutors, dataToUpdate) {

                    @NonNull
                    @Override
                    protected LiveData<ApiResponse<List<ServiceRequest>>> createCall() {
                        return serviceClient.getNearbyServiceRequests(status, serviceCode);
                    }
                }.asLiveData();
    }

}
