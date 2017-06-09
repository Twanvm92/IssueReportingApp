package com.example.justin.verbeterjegemeente.API;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.justin.verbeterjegemeente.Constants;
import com.example.justin.verbeterjegemeente.R;
import com.example.justin.verbeterjegemeente.domain.Service;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by twanv on 9-6-2017.
 */

public class RequestManager {
    private ServiceClient client;
    private Context context;
    private List<Service> serviceList;
    private OnServicesReady servCallb;

    public RequestManager(Context context) {
        client = ServiceGenerator.createService(ServiceClient.class);
        this.context = context;
    }

    public void getServices() {
        try {
            if(ConnectionChecker.isConnected()) { // check if user is actually connected to the internet
                // create a callback
//                ServiceGenerator.changeApiBaseUrl("https://asiointi.hel.fi/palautews/rest/v1/");
                Call<List<Service>> serviceCall = client.getServices(Constants.LANG_EN);
                // fire the get request
                serviceCall.enqueue(new Callback<List<Service>>() {
                    @Override
                    public void onResponse(Call<List<Service>> call, Response<List<Service>> response) {
                        // if a response has been received create a list with Services with the responsebody
                        serviceList = response.body();

                        // test what services have been caught in the response
                        if (serviceList != null) {
                            for (Service s : serviceList) {
                                Log.i("Response: ", "" + s.getService_name());
                            }

                            servCallb.servicesReady(serviceList);
                        } else {
                            Log.i("Response: ", "List was empty");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Service>> call, Throwable t) { // something went wrong

                        Toast.makeText(context, t.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

            } else { // user is not connected to the internet
                Toast.makeText(context, context.getResources().getString(R.string.FoutOphalenProblemen),
                        Toast.LENGTH_SHORT).show();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setOnServicesReadyCallb(OnServicesReady servCallb) {
        this.servCallb = servCallb;
    }

    public interface OnServicesReady {
        void servicesReady(List<Service> services);
    }
}
