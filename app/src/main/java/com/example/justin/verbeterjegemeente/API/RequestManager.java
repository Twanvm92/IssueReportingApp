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
 * This class will manage all the Retrofit API requests.
 * When the requests give back a response a custom callback interface
 * will notify and pass the data out of the response to the
 * activity that implemented the callback.
 * It will need the context of the activity to display toasts.
 * Created by twanv on 9-6-2017.
 */

public class RequestManager {
    private ServiceClient client;
    private Context context;
    private List<Service> serviceList;
    private OnServicesReady servCallb;

    /**
     * Accepts the context of an activity and initializes the ServiceClient
     * to handle the API requests.
     *
     * @param context context of an activity
     */
    public RequestManager(Context context) {
        client = ServiceGenerator.createService(ServiceClient.class);
        this.context = context;
    }

    /**
     * Gets the services from an Open311 interface APi and passes these as a list
     * to a callback interface.
     */
    public void getServices() {
        try {
            if (ConnectionChecker.isConnected()) { // check if user is actually connected to the internet
                // create a callback
//                ServiceGenerator.changeApiBaseUrl("https://asiointi.hel.fi/palautews/rest/v1/");
                Call<List<Service>> serviceCall = client.getServices(Constants.LANG_EN);
                // fire the get request
                serviceCall.enqueue(new Callback<List<Service>>() {
                    @Override
                    public void onResponse(Call<List<Service>> call, Response<List<Service>> response) {
                        // if a response has been received create a list with Services with the responsebody
                        serviceList = response.body();
                        Log.e("empty response: ", response.body().toString());

                        // test what services have been caught in the response
                        if (serviceList != null) {
                            for (Service s : serviceList) {
                                Log.i("Response: ", "" + s.getService_name());
                            }

                            // let the activity/fragment that implemented the service callback
                            // know that the services are ready.
                            servCallb.servicesReady(serviceList);
                        } else {
                            Log.i("Response: ", "List was empty");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Service>> call, Throwable t) { // something went wrong

                        Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            } else { // user is not connected to the internet
                Toast.makeText(context, context.getResources().getString(R.string.FoutOphalenProblemen),
                        Toast.LENGTH_SHORT).show();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param servCallb Callback interface that was implemented by the activity
     *                  that passed this callback.
     */
    public void setOnServicesReadyCallb(OnServicesReady servCallb) {
        this.servCallb = servCallb;
    }

    /**
     * Callback interface that will pass a list of services to a activity that implemented
     * the interface once the list has been received.
     */
    public interface OnServicesReady {
        /**
         * Passes available services in a list to class that is listening.
         *
         * @param services accepts a list of services obtained
         *                 from an open311 interface
         */
        void servicesReady(List<Service> services);
    }
}
