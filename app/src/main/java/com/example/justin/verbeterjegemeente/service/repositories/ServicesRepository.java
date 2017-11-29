package com.example.justin.verbeterjegemeente.service.repositories;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.example.justin.verbeterjegemeente.app.Constants;
import com.example.justin.verbeterjegemeente.data.network.ServiceClient;
import com.example.justin.verbeterjegemeente.service.model.Service;

import java.util.List;

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

    @Inject
    public ServicesRepository(ServiceClient serviceClient) {
        this.serviceClient = serviceClient;
    }

    public LiveData<List<Service>> getServiceList() {
        final MutableLiveData<List<Service>> data = new MutableLiveData<>();

        serviceClient.getServices(Constants.LANG_EN).enqueue(new Callback<List<Service>>() {
            @Override
            public void onResponse(Call<List<Service>> call, Response<List<Service>> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<Service>> call, Throwable t) {
                // TODO better error handling in part #2 ...
                data.setValue(null);
            }
        });

        return data;
    }
}
