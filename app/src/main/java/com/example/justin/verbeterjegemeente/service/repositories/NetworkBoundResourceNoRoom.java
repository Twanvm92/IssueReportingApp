package com.example.justin.verbeterjegemeente.service.repositories;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.example.justin.verbeterjegemeente.app.AppExecutors;
import com.example.justin.verbeterjegemeente.data.database.ServiceEntry;
import com.example.justin.verbeterjegemeente.data.network.ApiResponse;
import com.example.justin.verbeterjegemeente.data.network.Resource;

import java.util.List;

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
public abstract class NetworkBoundResourceNoRoom<ResultType, RequestType>
        extends AbstractNetworkBoundResource<ResultType, RequestType> {

    @MainThread
    NetworkBoundResourceNoRoom(AppExecutors appExecutors,
                               final MediatorLiveData<Resource<ResultType>> dataToUpdate) {
        this.appExecutors = appExecutors;
        result = dataToUpdate;
        result.setValue(Resource.loading(null));
        fetchFromNetwork();
    }

    protected void fetchFromNetwork() {
        LiveData<ApiResponse<RequestType>> apiResponse = createCall();
        // we re-attach dbSource as a new source, it will dispatch its latest value quickly
        result.addSource(apiResponse, response -> {
            result.removeSource(apiResponse);
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
                            setValue(Resource.success(processResponseResultType(response)))
                            );
                });
            } else {
                onFetchFailed();
                Timber.d("Retrofit response is unsuccessful");
                setValue(Resource.error(response.errorMessage, null));
            }
        });
    }

    @Override
    protected void fetchFromNetwork(LiveData<ResultType> dbSource) {
        // not used
    }

    protected void onFetchFailed() {
    }

    @WorkerThread
    protected void saveCallResult(@NonNull RequestType item) {
        // not used
    }

    @MainThread
    protected boolean shouldFetch(@Nullable ResultType data) {
        // not used
        return false;
    }

    @NonNull
    @MainThread
    protected LiveData<ResultType> loadFromDb() {
        //not used
        return null;
    }

    @NonNull
    @MainThread
    protected abstract LiveData<ApiResponse<RequestType>> createCall();
}
