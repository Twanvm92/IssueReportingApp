package com.example.justin.verbeterjegemeente.API;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.justin.verbeterjegemeente.Constants;
import com.example.justin.verbeterjegemeente.Database.DatabaseHandler;
import com.example.justin.verbeterjegemeente.R;
import com.example.justin.verbeterjegemeente.domain.PostServiceRequestResponse;
import com.example.justin.verbeterjegemeente.domain.Service;
import com.example.justin.verbeterjegemeente.domain.ServiceRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
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
 */

public class RequestManager {
    private ServiceClient client;
    private Context context;
    private OnServicesReady servCallb;
    private OnServiceRequestsReady servReqCallb;
    private OnServiceRequestPosted servReqPostedCallb;

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
                Call<List<Service>> serviceCall = client.getServices(Constants.LANG_EN);
                // fire the get request
                serviceCall.enqueue(new Callback<List<Service>>() {
                    @Override
                    public void onResponse(Call<List<Service>> call, Response<List<Service>> response) {
                        if(response.isSuccessful()) {
                            // if a response has been received create a list with Services with the responsebody
                            List<Service> serviceList = response.body();
                            Log.e("empty response: ", response.body().toString());

                            // test what services have been caught in the response
                            if (!serviceList.isEmpty()) {
                                for (Service s : serviceList) {
                                    Log.i("Response: ", "" + s.getService_name());
                                }

                                // let the activity/fragment that implemented the service callback
                                // know that the services are ready.
                                servCallb.servicesReady(serviceList);
                            } else {
                                Log.i("Response: ", "List was empty");
                            }
                        } else {
                        try { //something went wrong. Show the user what went wrong
                            JSONArray jObjErrorArray = new JSONArray(response.errorBody().string());
                            JSONObject jObjError = (JSONObject) jObjErrorArray.get(0);

                            Toast.makeText(context, jObjError.getString("description"),
                                    Toast.LENGTH_SHORT).show();
                            Log.i("Error message: ", jObjError.getString("description"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
     * Gets the service requests from an Open311 interface APi and passes these as a list
     * to a callback interface.
     * @param lat latitude of the camera position on the Google map
     * @param lng longtitude of the camera position on the Google map
     * @param status status of service requests that are requested. Can be open, closed or null.
     * @param radius amount of meters around the given lat and lng.
     * @param servQ if user filtered on a category, this will be the category code.
     */
    public void getServiceRequests(String lat, String lng, String status, String radius, String servQ) {
        try {
            if (ConnectionChecker.isConnected()) { // check if user is actually connected to the internet
                // create a callback
                Call<ArrayList<ServiceRequest>> serviceCall = client.getNearbyServiceRequests(lat, lng, status, radius, servQ);
                // fire the get request
                serviceCall.enqueue(new Callback<ArrayList<ServiceRequest>>() {
                    @Override
                    public void onResponse(Call<ArrayList<ServiceRequest>> call, Response<ArrayList<ServiceRequest>> response) {
                        if(response.isSuccessful()) {
                            // if a response has been received create a list with Services with the responsebody
                            ArrayList<ServiceRequest> servReqList = response.body();
                            servReqCallb.serviceRequestsReady(servReqList);
                        } else {
                        try { //something went wrong. Show the user what went wrong
                            JSONArray jObjErrorArray = new JSONArray(response.errorBody().string());
                            JSONObject jObjError = (JSONObject) jObjErrorArray.get(0);

                            Toast.makeText(context, jObjError.getString("description"),
                                    Toast.LENGTH_SHORT).show();
                            Log.i("Error message: ", jObjError.getString("description"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    }

                    @Override
                    public void onFailure(Call<ArrayList<ServiceRequest>> call, Throwable t) { // something went wrong

                        Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            } else { // user is not connected to the internet
                Toast.makeText(context, context.getResources().getString(R.string.noConnection),
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
     * Gets the service requests from an Open311 interface APi and passes these as a list
     * to a callback interface.
     * @param lat latitude of the camera position on the Google map
     * @param lng longtitude of the camera position on the Google map
     * @param status status of service requests that are requested. Can be open, closed or null.
     * @param radius amount of meters around the given lat and lng.
     */
    public void getServiceRequests(String lat, String lng, String status, String radius) {
        try {
            if (ConnectionChecker.isConnected()) { // check if user is actually connected to the internet
                // create a callback
                Call<ArrayList<ServiceRequest>> serviceCall = client.getNearbyServiceRequests(lat, lng, status, radius);
                // fire the get request
                serviceCall.enqueue(new Callback<ArrayList<ServiceRequest>>() {
                    @Override
                    public void onResponse(Call<ArrayList<ServiceRequest>> call, Response<ArrayList<ServiceRequest>> response) {
                        if(response.isSuccessful()) {
                            // if a response has been received create a list with Services with the responsebody
                            ArrayList<ServiceRequest> servReqList = response.body();

                                servReqCallb.serviceRequestsReady(servReqList);

                        } else {
                        try { //something went wrong. Show the user what went wrong
                            JSONArray jObjErrorArray = new JSONArray(response.errorBody().string());
                            JSONObject jObjError = (JSONObject) jObjErrorArray.get(0);

                            Toast.makeText(context, jObjError.getString("description"),
                                    Toast.LENGTH_SHORT).show();
                            Log.i("Error message: ", jObjError.getString("description"));
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                        }

                    }

                    @Override
                    public void onFailure(Call<ArrayList<ServiceRequest>> call, Throwable t) { // something went wrong

                        Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            } else { // user is not connected to the internet
                Toast.makeText(context, context.getResources().getString(R.string.noConnection),
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
     * Gets the service requests from an Open311 interface API using service request IDs and passes these as a list
     * to a callback interface.
     * @param sRequestID ID of a service request. Can be several IDs delimited by comma's.
     */
    public void getServiceRequestsByID(String sRequestID) {
        try {
            if (ConnectionChecker.isConnected()) { // check if user is actually connected to the internet
                // create a callback
                Call<ArrayList<ServiceRequest>> serviceCall = client.getServiceById(sRequestID, "1");
                // fire the get request
                serviceCall.enqueue(new Callback<ArrayList<ServiceRequest>>() {
                    @Override
                    public void onResponse(Call<ArrayList<ServiceRequest>> call, Response<ArrayList<ServiceRequest>> response) {
                        if(response.isSuccessful()) {
                            // if a response has been received create a list with Services with the responsebody
                            ArrayList<ServiceRequest> servReqList = response.body();

                            if (!servReqList.isEmpty()) {
//                                for (ServiceRequest sr : servReqList) {
//                                    sr.setUpdatedDatetime("2017-08-02T16:59:42Z");
//                                }

                                for (int i = 0; i < servReqList.size() - 1; i++) {
                                    servReqList.get(i).setUpdatedDatetime("2017-08-0" + (i + 5) + "T16:59:42Z");
                                }

                            }

                            servReqCallb.serviceRequestsReady(servReqList);

                        } else {
                        try { //something went wrong. Show the user what went wrong
                            JSONArray jObjErrorArray = new JSONArray(response.errorBody().string());
                            JSONObject jObjError = (JSONObject) jObjErrorArray.get(0);

                            Toast.makeText(context, jObjError.getString("description"),
                                    Toast.LENGTH_SHORT).show();
                            Log.i("Error message: ", jObjError.getString("description"));
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                        }

                    }

                    @Override
                    public void onFailure(Call<ArrayList<ServiceRequest>> call, Throwable t) { // something went wrong

                        Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            } else { // user is not connected to the internet
                Toast.makeText(context, context.getResources().getString(R.string.noConnection),
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
     * Gets the service requests from an Open311 interface APi and passes these as a list
     * to a callback interface.
     * @param lat latitude of the camera position on the Google map
     * @param lng longtitude of the camera position on the Google map
     * @param status status of service requests that are requested. Can be open, closed or null.
     * @param radius amount of meters around the given lat and lng.
     * @param updatedTime earliest updated_datetime of a service request.
     */
    public void getClosedServiceRequests(String lat, String lng, String status, String radius, String updatedTime) {
        try {
            if (ConnectionChecker.isConnected()) { // check if user is actually connected to the internet
                // create a callback
                Call<ArrayList<ServiceRequest>> serviceCall = client.getClosedNearbyServiceRequests(lat, lng, status, radius, updatedTime);
                // fire the get request
                serviceCall.enqueue(new Callback<ArrayList<ServiceRequest>>() {
                    @Override
                    public void onResponse(Call<ArrayList<ServiceRequest>> call, Response<ArrayList<ServiceRequest>> response) {
                        if (response.isSuccessful()) {
                            // if a response has been received create a list with Services with the responsebody
                            ArrayList<ServiceRequest> servReqList = response.body();

                            servReqCallb.serviceRequestsReady(servReqList);
                        } else {
                            try { //something went wrong. Show the user what went wrong
                                JSONArray jObjErrorArray = new JSONArray(response.errorBody().string());
                                JSONObject jObjError = (JSONObject) jObjErrorArray.get(0);

                                Toast.makeText(context, jObjError.getString("description"),
                                        Toast.LENGTH_SHORT).show();
                                Log.i("Error message: ", jObjError.getString("description"));
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<ArrayList<ServiceRequest>> call, Throwable t) { // something went wrong

                        Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            } else { // user is not connected to the internet
                Toast.makeText(context, context.getResources().getString(R.string.noConnection),
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
     * Gets the service requests from an Open311 interface APi and passes these as a list
     * to a callback interface.
     * @param lat latitude of the camera position on the Google map
     * @param lng longtitude of the camera position on the Google map
     * @param status status of service requests that are requested. Can be open, closed or null.
     * @param radius amount of meters around the given lat and lng.
     * @param servQ if user filtered on a category, this will be the category code.
     * @param updatedTime earliest updated_datetime of a service request.
     */
    public void getClosedServiceRequests(String lat, String lng, String status, String radius, String servQ, String updatedTime) {
        try {
            if (ConnectionChecker.isConnected()) { // check if user is actually connected to the internet
                // create a callback
                Call<ArrayList<ServiceRequest>> serviceCall = client.getClosedNearbyServiceRequests(lat, lng, status, radius, servQ, updatedTime);
                // fire the get request
                serviceCall.enqueue(new Callback<ArrayList<ServiceRequest>>() {
                    @Override
                    public void onResponse(Call<ArrayList<ServiceRequest>> call, Response<ArrayList<ServiceRequest>> response) {
                        if(response.isSuccessful()) {
                            // if a response has been received create a list with Services with the responsebody
                            ArrayList<ServiceRequest> servReqList = response.body();

                            servReqCallb.serviceRequestsReady(servReqList);

                        } else {
                            try { //something went wrong. Show the user what went wrong
                                JSONArray jObjErrorArray = new JSONArray(response.errorBody().string());
                                JSONObject jObjError = (JSONObject) jObjErrorArray.get(0);

                                Toast.makeText(context, jObjError.getString("description"),
                                        Toast.LENGTH_SHORT).show();
                                Log.i("Error message: ", jObjError.getString("description"));
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<ArrayList<ServiceRequest>> call, Throwable t) { // something went wrong

                        Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            } else { // user is not connected to the internet
                Toast.makeText(context, context.getResources().getString(R.string.noConnection),
                        Toast.LENGTH_SHORT).show();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void postServiceRequest(String sc, String descr, double lat, double lon, String address_string,
                                   String address_id, String[] attribute, String jurisdiction_id,
                                   String email, String fName, String lName, String imgUrl) {
        try {
            if (ConnectionChecker.isConnected()) {

                Call<ArrayList<PostServiceRequestResponse>> serviceRequestResponseCall =
                        client.postServiceRequest(sc, descr, lat, lon, address_string,
                                address_id, attribute, jurisdiction_id, email, fName, lName, imgUrl);
                // fire the get post request
                serviceRequestResponseCall.enqueue(new Callback<ArrayList<PostServiceRequestResponse>>() {
                    @Override
                    public void onResponse(Call<ArrayList<PostServiceRequestResponse>> call,
                                           Response<ArrayList<PostServiceRequestResponse>> response) {
                        if (response.isSuccessful()) {
                            // if a response was successful get an arraylist of postservicerequestresponses
                            ArrayList<PostServiceRequestResponse> pRespList = response.body();

                            servReqPostedCallb.serviceRequestPosted(pRespList);

                        } else {
                            try { //something went wrong. Show the user what went wrong
                                JSONArray jObjErrorArray = new JSONArray(response.errorBody().string());
                                JSONObject jObjError = (JSONObject) jObjErrorArray.get(0);

                                Toast.makeText(context, jObjError.getString("description"),
                                        Toast.LENGTH_SHORT).show();
                                Log.i("Error message: ", jObjError.getString("description"));
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    // a connection could not have been made. Tell the user.
                    @Override
                    public void onFailure(Call<ArrayList<PostServiceRequestResponse>> call, Throwable t) {
                        Toast.makeText(context, context.getResources().getString(R.string.ePostRequest),
                                Toast.LENGTH_SHORT).show();
                    }
                });

            } else {// a connection could not have been made. Tell the user.
                Toast.makeText(context, context.getResources().getString(R.string.ePostRequest),
                        Toast.LENGTH_SHORT).show();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void upvoteServiceRequest(String serviceRequestID, String extraDescription) {
        try {
            if (ConnectionChecker.isConnected()) {

                Call<ArrayList> serviceRequestResponseCall =
                        client.upvoteRequest(serviceRequestID, extraDescription);
                // fire the get post request
                serviceRequestResponseCall.enqueue(new Callback<ArrayList>() {
                    @Override
                    public void onResponse(Call<ArrayList> call,
                                           Response<ArrayList> response) {
                        if (response.isSuccessful()) {
                            // if a response was successful tell Activity where this request was
                            // fired from

                            Toast.makeText(context, context.getResources().getString(R.string.upvoteSucces),
                                    Toast.LENGTH_SHORT).show();

                        } else {
                            try { //something went wrong. Show the user what went wrong
                                JSONArray jObjErrorArray = new JSONArray(response.errorBody().string());
                                JSONObject jObjError = (JSONObject) jObjErrorArray.get(0);

                                Toast.makeText(context, jObjError.getString("description"),
                                        Toast.LENGTH_SHORT).show();
                                Log.i("Error message: ", jObjError.getString("description"));
                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    // a connection could not have been made. Tell the user.
                    @Override
                    public void onFailure(Call<ArrayList> call, Throwable t) {
                        Toast.makeText(context, context.getResources().getString(R.string.ePostRequest),
                                Toast.LENGTH_SHORT).show();
                    }
                });

            } else {// a connection could not have been made. Tell the user.
                Toast.makeText(context, context.getResources().getString(R.string.ePostRequest),
                        Toast.LENGTH_SHORT).show();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setOnServicesReadyCallb(OnServicesReady servCallb) {
        this.servCallb = servCallb;
    }

    public void setOnServiceReqReadyCallb(OnServiceRequestsReady servReqCallb) {
        this.servReqCallb = servReqCallb;
    }

    public void setOnServiceReqPostedCallb(OnServiceRequestPosted servReqPostedCallb) {
        this.servReqPostedCallb = servReqPostedCallb;
    }


    /**
     * Callback interface that will pass a list of services to an activity that implemented
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

    /**
     * Callback interface that will pass a list of ServiceRequests to an activity that implemented
     * the interface once the list has been received.
     */
    public interface OnServiceRequestsReady {
        /**
         * Passes available service requests in a list to class that is listening.
         *
         * @param serviceRequests accepts a list of service requests obtained
         *                 from an open311 interface
         */
        void serviceRequestsReady(ArrayList<ServiceRequest> serviceRequests);
    }

    public interface OnServiceRequestPosted {
        void serviceRequestPosted(ArrayList<PostServiceRequestResponse> pReqRespList);
    }
}
