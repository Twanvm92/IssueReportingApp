package com.example.justin.verbeterjegemeente.Presentation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.justin.verbeterjegemeente.API.ConnectionChecker;
import com.example.justin.verbeterjegemeente.API.ServiceClient;
import com.example.justin.verbeterjegemeente.API.ServiceGenerator;
import com.example.justin.verbeterjegemeente.Adapters.ServiceRequestAdapter;
import com.example.justin.verbeterjegemeente.Constants;
import com.example.justin.verbeterjegemeente.R;
import com.example.justin.verbeterjegemeente.domain.ServiceRequest;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Tab2Fragment extends Fragment  {

    private ArrayAdapter serviceRequestAdapter;
    private ArrayList<ServiceRequest> serviceList;
    private ServiceRequest serviceRequest;
    private LatLng currentLatLng = null;
    private String currentRadius;
    private String servCodeQ;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab2_fragment, container, false);


        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.getDouble("CURRENT_LAT") != 0) {
                currentLatLng = new LatLng(bundle.getDouble("CURRENT_LAT"), bundle.getDouble("CURRENT_LONG"));
            } else {
                currentLatLng = new LatLng(Constants.DEFAULT_LAT, Constants.DEFAULT_LONG);
            }
        }
        // get user selected radius and cat or use default radius and cat
        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        int rValue = prefs.getInt(getString(R.string.activityMain_saved_radius), 20); // 20 is default
        currentRadius = Integer.toString(rValue);
        String savedservCodeQ = prefs.getString(getString(R.string.activityMain_saved_servcodeQ),
                getString(R.string.geenFilter));

        // check if service code is not default value
        // otherwise make String null
        // this will let API requests not take in account service codes
        if (savedservCodeQ.equals("")) {
            servCodeQ = null;
        } else {
            servCodeQ = savedservCodeQ;
        }

        searchServiceRequests();

        serviceList = new ArrayList<>();

        ListView meldingListView = (ListView) view.findViewById(R.id.meldingListView);


        serviceRequestAdapter = new ServiceRequestAdapter(getContext(), serviceList);
        meldingListView.setAdapter(serviceRequestAdapter);
        meldingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent myIntent = new Intent(view.getContext(), DetailedMeldingActivity.class);
                serviceRequest = serviceList.get(position);
                myIntent.putExtra("serviceRequest", (Serializable) serviceRequest);
                myIntent.putExtra("ORIGIN", "Tab2Fragment");
                startActivity(myIntent);

            }
        });

        return view;
    }

    //moet aangeroepen worden met zoekknop
    public void searchServiceRequests() {
        try {
            if (ConnectionChecker.isConnected()) {

                String lat;
                String lon;
                String convLng;
                String convLat;
                if (currentLatLng != null) {
                    convLat = Double.toString(currentLatLng.latitude);
                    convLng = Double.toString(currentLatLng.longitude);
                    lat = convLat;
                    lon = convLng;
                } else {
                    currentLatLng = new LatLng(Constants.DEFAULT_LAT, Constants.DEFAULT_LONG);
                    convLat = Double.toString(currentLatLng.latitude);
                    convLng = Double.toString(currentLatLng.longitude);
                    lat = convLat;
                    lon = convLng;
                }

//                create a callback
                ServiceClient client = ServiceGenerator.createService(ServiceClient.class);

                Call<ArrayList<ServiceRequest>> serviceCall;
                if (servCodeQ == null) {
                    serviceCall = client.getNearbyServiceRequests(
                            lat, lon, null, currentRadius);
                } else {
                    serviceCall = client.getNearbyServiceRequests(
                            lat, lon, null, currentRadius, servCodeQ);
                }
//               fire the get request

                serviceCall.enqueue(new Callback<ArrayList<ServiceRequest>>() {
                    @Override
                    public void onResponse(Call<ArrayList<ServiceRequest>> call, Response<ArrayList<ServiceRequest>> response) {
                        if (response.isSuccessful()) {
                            serviceList.clear();
                            ArrayList<ServiceRequest> servicesFound = response.body();
                            if (!servicesFound.isEmpty()) {
                                for (ServiceRequest s : servicesFound) {
                                    serviceList.add(s);
                                }
                            } else {
                                Toast.makeText(getActivity(), getResources().getString(R.string.eGetServices),
                                        Toast.LENGTH_SHORT).show();
                            }
                            serviceRequestAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(), getResources().getString(R.string.FoutOphalenProblemen),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<ServiceRequest>> call, Throwable t) {
                        Toast.makeText(getContext(), getResources().getString(R.string.ePostRequest),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            } else { // user is not connected to the internet
                Toast.makeText(getContext(), getResources().getString(R.string.FoutOphalenProblemen),
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
     * Accepts current location of the user (or the location of the camera on the Google map
     * if user does not have gps activated) that was passed from Tab1Fragment to MainActivity
     * to this Fragment. Also sends a new get request to obtain srvice requests based on the new
     * location given.
     *
     * @param newLatLong Current location of the user that is determined by
     *                   a gps location or the center of the camera on the Google map
     */
    public void updateCurrentLoc(LatLng newLatLong) {
        currentLatLng = newLatLong;
        Log.e("Method: ", "Lat: " + currentLatLng.latitude + " Long: " + currentLatLng.longitude);

        searchServiceRequests();
    }

    /**
     * This method will update the radius and service codes connected to the category
     * set by the user. After that it will get new service requests based on the new radius and
     * category filter and add them as markers on a Google map
     *
     * @param radius    radius in meters
     * @param servCodeQ String with service codes appending by a , delimiter
     *                  that can be used for filtering service requests.
     */
    public void updateRadiusCat(int radius, String servCodeQ) {
        String pRadius = (String) Integer.toString(radius);
        currentRadius = pRadius;
        this.servCodeQ = servCodeQ;
        Log.e("Radius update tab2: ", currentRadius);

        searchServiceRequests();
    }
}

