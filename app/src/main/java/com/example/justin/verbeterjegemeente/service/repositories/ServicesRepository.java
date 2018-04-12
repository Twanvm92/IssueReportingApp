package com.example.justin.verbeterjegemeente.service.repositories;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.justin.verbeterjegemeente.app.AppExecutors;
import com.example.justin.verbeterjegemeente.app.Constants;
import com.example.justin.verbeterjegemeente.data.database.ServiceDao;
import com.example.justin.verbeterjegemeente.data.database.ServiceEntry;
import com.example.justin.verbeterjegemeente.data.network.ApiResponse;
import com.example.justin.verbeterjegemeente.data.network.Resource;
import com.example.justin.verbeterjegemeente.data.network.ServiceClient;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

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

    @Inject
    public ServicesRepository(ServiceClient serviceClient, ServiceDao serviceDao,
                              AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
        this.serviceDao = serviceDao;
        this.serviceClient = serviceClient;
    }

    public LiveData<Resource<List<ServiceEntry>>> loadServices() {
        return new NetworkBoundResourceRoom<List<ServiceEntry>,List<ServiceEntry>>(appExecutors) {
            @Override
            protected void saveCallResult(@NonNull List<ServiceEntry> item) {
                // Deletes old historical data
                serviceDao.deleteAllServices();
                Timber.d("Old services deleted");

                Timber.d("Saving result from http request in database");
                serviceDao.bulkInsert(item);
            }

            @Override
            protected boolean shouldFetch(@Nullable List<ServiceEntry> data) {
                // Only perform initialization once per app lifetime. If initialization has already been
                // performed, we have nothing to do in this method.
                if (mInitialized) return false;
                mInitialized = true;
                Timber.d("Fetching new services from network");
                return data == null || data.isEmpty();

            }

            @NonNull
            @Override
            protected LiveData<List<ServiceEntry>> loadFromDb() {
                return serviceDao.getAllServices();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<ServiceEntry>>> createCall() {
                return serviceClient.getServices(Constants.LANG_EN);
            }
        }.asLiveData();
    }

    public LiveData<Resource<List<ServiceEntry>>> refreshServices(MediatorLiveData<Resource<List<ServiceEntry>>> dataToUpdate) {
        return
                new NetworkBoundResourceRefresh<List<ServiceEntry>, List<ServiceEntry>>(
                        appExecutors, dataToUpdate) {
                    @Override
                    protected void saveCallResult(@NonNull List<ServiceEntry> item) {
                        // Deletes old historical data
                        serviceDao.deleteAllServices();
                        Timber.d("Old services deleted");

                        Timber.d("Saving result from http request in database");
                        serviceDao.bulkInsert(item);
                    }

                    @Override
                    protected boolean shouldFetch(@Nullable List<ServiceEntry> data) {
                        return data == null || data.isEmpty();
                    }

                    @NonNull
                    @Override
                    protected LiveData<List<ServiceEntry>> loadFromDb() {
                        return serviceDao.getAllServices();
                    }

                    @NonNull
                    @Override
                    protected LiveData<ApiResponse<List<ServiceEntry>>> createCall() {
                        return serviceClient.getServices(Constants.LANG_EN);
                    }
                }.asLiveData();
    }
}
