package com.example.justin.verbeterjegemeente.service.repositories;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.example.justin.verbeterjegemeente.app.AppExecutors;
import com.example.justin.verbeterjegemeente.data.network.ApiResponse;
import com.example.justin.verbeterjegemeente.data.network.Resource;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * A generic class that can provide a resource backed by both the sqlite database and the network.
 * <p>
 * You can read more about it in the <a href="https://developer.android.com/arch">Architecture
 * Guide</a>.
 * @param <ResultType>
 * @param <RequestType>
 */
// extended the class with AbstractNetworkBoundResource - T. van Maastricht
public abstract class NetworkBoundResourceRefresh<ResultType, RequestType>
        extends AbstractNetworkBoundResource<ResultType, RequestType> {

    @MainThread
    NetworkBoundResourceRefresh(AppExecutors appExecutors, final MediatorLiveData<Resource<ResultType>> dataToUpdate) {
        this.appExecutors = appExecutors;
        result = dataToUpdate;

        result.setValue(Resource.loading(null));
        LiveData<ResultType> dbSource = loadFromDb();
        result.addSource(dbSource, data -> {
            result.removeSource(dbSource);
            if (shouldFetch(data)) {
                fetchFromNetwork(dbSource);
            } else {
                result.addSource(dbSource, newData -> setValue(Resource.success(newData)));
            }
        });
    }

    protected void fetchFromNetwork(final LiveData<ResultType> dbSource) {
        LiveData<ApiResponse<RequestType>> apiResponse = createCall();
        // we re-attach dbSource as a new source, it will dispatch its latest value quickly
        result.addSource(dbSource, newData -> setValue(Resource.loading(newData))/*dataToUpdate.setValue(Resource.loading(newData))*/);
        result.addSource(apiResponse, response -> {
            result.removeSource(apiResponse);
            result.removeSource(dbSource);
            //noinspection ConstantConditions
            if (response.isSuccessful()) {
                Timber.d("Retrofit response is successful");
                appExecutors.diskIO().execute(() -> {
                    saveCallResult(processResponse(response));
                    Timber.d("Loading new livedata from database with latest results");
                    appExecutors.mainThread().execute(() ->
                            // we specially request a new live data,
                            // otherwise we will get immediately last cached value,
                            // which may not be updated with latest results received from network.
                            result.addSource(loadFromDb(),
                                    newData -> setValue(Resource.success(newData))

                    ));
                });
            } else {
                onFetchFailed();
                Timber.d("Retrofit response is unsuccessful");
                result.addSource(dbSource,
                        newData -> setValue(Resource.error(response.errorMessage, newData)));
            }
        });
    }

    protected void onFetchFailed() {
    }

    @WorkerThread
    protected abstract void saveCallResult(@NonNull RequestType item);

    @MainThread
    protected abstract boolean shouldFetch(@Nullable ResultType data);

    @NonNull
    @MainThread
    protected abstract LiveData<ResultType> loadFromDb();

    @NonNull
    @MainThread
    protected abstract LiveData<ApiResponse<RequestType>> createCall();
}
