package com.example.justin.verbeterjegemeente.Business;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.example.justin.verbeterjegemeente.API.ConnectionChecker;
import com.example.justin.verbeterjegemeente.API.ServiceClient;
import com.example.justin.verbeterjegemeente.API.ServiceGenerator;
import com.example.justin.verbeterjegemeente.Constants;
import com.example.justin.verbeterjegemeente.Presentation.Tab1Fragment;
import com.example.justin.verbeterjegemeente.R;
import com.example.justin.verbeterjegemeente.domain.Service;
import com.example.justin.verbeterjegemeente.domain.ServiceRequest;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.justin.verbeterjegemeente.Constants.DEFAULT_LAT;
import static com.example.justin.verbeterjegemeente.Constants.DEFAULT_LONG;

public class MarkerHandler {

    private GoogleMap mMap;
    private ArrayList<String> categoryList = new ArrayList<>();
    private List<Service> serviceList;
    private ArrayList<ServiceRequest> serivceRequestList = new ArrayList<>();
    private ArrayList<ArrayList<Marker>> deOpperArrayList = new ArrayList();
    private GoogleApiClient mApiClient;
    private LocationRequest mLocationRequest;
    private Tab1Fragment tab;
    private Context context;
    ServiceClient client;
    boolean servicesGotten = false;
    boolean requestsGotten = false;
    boolean init = false;

    //constructor
    public MarkerHandler(GoogleMap map, Context context, Tab1Fragment tabFrag) {
        this.context = context;
        mMap = map;
        this.tab = tabFrag;

        client = ServiceGenerator.createService(ServiceClient.class);
    }

    //set markers for specific category visible
    public void setVisible(int category) {

        for(int i =0; i < deOpperArrayList.size(); i++){
            for(Marker m : deOpperArrayList.get(i)) {
                m.setVisible(false);
                Log.i("SHOWING","marker: " +  m.getTitle() + " invisible");
            }
        }

        ArrayList<Marker> checkList = deOpperArrayList.get(category);
        for (Marker m : checkList) {
            m.setVisible(true);
            Log.i("SHOWING","marker: " +  m.getTitle() + " visible");
        }
    }

    //initialize data and markers
    public void init() {
        getServices();
        getData();

    }

    //get Data from api and make markers
    public void getData() {
        try {
            if(ConnectionChecker.isConnected()) {
                Call<ArrayList<ServiceRequest>> nearbyServiceRequests = client.getNearbyServiceRequests("" + DEFAULT_LONG, "" + DEFAULT_LAT, null, "300");
                nearbyServiceRequests.enqueue(new Callback<ArrayList<ServiceRequest>>() {
                    @Override
                    public void onResponse(Call<ArrayList<ServiceRequest>> call, Response<ArrayList<ServiceRequest>> response) {
                        if(response.isSuccessful()) {
                            ArrayList<ServiceRequest> srList = response.body();

                            for (ServiceRequest s : srList) {
                                serivceRequestList.add(s);
                                Log.e("Opgehaalde servicereq: ", s.getDescription());
                            }
                            requestsGotten = true;
                            sortCategories();
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
                    public void onFailure(Call<ArrayList<ServiceRequest>> call, Throwable t) {
                        Toast.makeText(context, context.getResources().getString(R.string.ePostRequest),
                                Toast.LENGTH_SHORT).show();
                        t.printStackTrace();
                    }
                });
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getServices() {
        try {
            if(ConnectionChecker.isConnected()) { // check if user is actually connected to the internet
                // create a callback
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
                                deOpperArrayList.add(new ArrayList<Marker>());
                            }
                            servicesGotten = true;
                        } else {
                            Log.i("Response: ", "List was empty");
                        }

                        if(serviceList != null) {
                            int x = 1; // set iterable separately for categoryList
                            for (int i = 0; i < serviceList.size(); i++) {
                                // first categoryList item is a default String
                                if(categoryList.size() > 1) { // do something if list already has 1 or more categories
                                    // do something if previous category is not the same as new category in servicelist
                                    if(!categoryList.get(x).equals(serviceList.get(i).getGroup())) {
                                        categoryList.add(serviceList.get(i).getService_code()); // add new category
                                        x++; // only up this iterable if new category is added
                                    }
                                } else {
                                    categoryList.add(serviceList.get(i).getGroup());
                                }
                            }
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

    private void sortCategories() {
        if(servicesGotten) {
            Log.i("SORTING", "Started sorting");
            int i = 0;
            for (ServiceRequest sr : serivceRequestList) {
                LatLng latLng = new LatLng(sr.getLat(), sr.getLong());
                Marker marker = tab.newMarker(sr.getDescription(), latLng);
                marker.setVisible(false);
                for (String s : categoryList) {
                    if (sr.getServiceCode().equals(s)) {
                        deOpperArrayList.get(i).add(marker);
                        Log.i("SORTING",i + ": " + sr.getDescription());
                        i=0;
                    }
                    i++;
                }
            }
            init = true;
        } else {
//            sortCategories();
            Log.i("SORTING", "Not Sorting");
        }
    }

    public boolean initiated() {
        return init;
    }

}
