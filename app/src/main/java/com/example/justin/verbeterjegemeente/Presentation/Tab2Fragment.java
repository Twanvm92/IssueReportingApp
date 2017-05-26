package com.example.justin.verbeterjegemeente.Presentation;

import android.content.Intent;
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

import com.example.justin.verbeterjegemeente.API.ServiceClient;
import com.example.justin.verbeterjegemeente.API.ServiceGenerator;
import com.example.justin.verbeterjegemeente.Adapters.ServiceRequestAdapter;
import com.example.justin.verbeterjegemeente.R;
import com.example.justin.verbeterjegemeente.domain.Locatie;
import com.example.justin.verbeterjegemeente.domain.ServiceRequest;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class Tab2Fragment extends Fragment  {
    private static final String TAG = "Tab2Fragment";

    private ListView meldingListView;
    private ServiceClient client;
    private ArrayAdapter serviceRequestAdapter;
    private ArrayList<ServiceRequest> serviceList;
    private String lat = "", lon = "", status, meters;
    private ServiceRequest serviceRequest;
    private Locatie location;
    private static final int LOCATIE_KIEZEN= 3;
    private LatLng currentLatLng = null;
//    private Button locatieButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab2_fragment,container,false);

        Bundle bundle = getArguments();
        if(bundle!= null)
        {
            double lat = getArguments().getDouble("CURRENT_LAT");
            double lng = getArguments().getDouble("CURRENT_LONG");
            Log.e("LAT EN LONG MAP: ", "Lat: " + lat + " Long: " + lng);

            currentLatLng = new LatLng(lat, lng);

            searchServiceRequests();
        }


        //                locatiebtn toevoegen
//        locatieButton = (Button) view.findViewById(R.id.wijzigLocatieButton);
//        locatieButton.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getContext(), MapsActivity.class);
//                startActivityForResult(intent, LOCATIE_KIEZEN);
//            }
//        });

        //spinner maken voor status
        //spinner maken voor meters
        //zoek knop toevoegen

        client = ServiceGenerator.createService(ServiceClient.class);

        serviceList = new ArrayList<>();

        meldingListView = (ListView) view.findViewById(R.id.meldingListView);


        serviceRequestAdapter = new ServiceRequestAdapter(getContext(), serviceList);
        meldingListView.setAdapter(serviceRequestAdapter);
        meldingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent myIntent = new Intent(view.getContext(), DetailedMeldingActivity.class);
                serviceRequest = serviceList.get(position);
                myIntent.putExtra("serviceRequest", (Serializable) serviceRequest);
                startActivity(myIntent);

            }
        });

        return view;
    }

    //moet aangeroepen worden met zoekknop
    public void searchServiceRequests (){
        try {
            if(isConnected()) {
//                 zoek opties nog toevoegen
//                if(currentLatLng != null) {
//                    if(currentLatLng.longitude != 0.0 && currentLatLng.latitude != 0.0) {
//                        lon = "" + currentLatLng.longitude;
//                        lat = "" + currentLatLng.latitude;
//                        Log.e("Long after receive", lon);
//                        Log.e("Lat after receive", lat);
//                    } else {
//                        Toast.makeText(getContext(),
//                                getResources().getString(R.string.geenLocatie),Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                } else {
//                    Toast.makeText(getContext(),
//                            getResources().getString(R.string.geenLocatie),Toast.LENGTH_SHORT).show();
//                    return;
//                }


//                if (!status.getText().toString().equals("") && !meters.getText().toString().equals("")) {
//                    status = *.getText().toString();
//                    meters = *.getText().toString();
//                } else {
//                    return;
//                    // niet elk veld is ingevuld
//                }

                if (currentLatLng != null) {
                    lat = "" + currentLatLng.latitude;
                    lon = "" + currentLatLng.longitude;
                } else {
                    Toast.makeText(getContext(), getResources().getString(R.string.errLoadingServiceRequestList),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

//                hardcoded voor nu
//                lat = "60.168321";
//                lon = "24.952397";
//                status = "open";
                meters = "300";

//                create a callback
                Call<ArrayList<ServiceRequest>> serviceCall = client.getNearbyServiceRequests(lat, lon, status, meters);
//                 fire the get request
                serviceCall.enqueue(new Callback<ArrayList<ServiceRequest>>() {
                    @Override
                    public void onResponse(Call<ArrayList<ServiceRequest>> call, Response<ArrayList<ServiceRequest>> response) {
                        if(response.isSuccessful()) {
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
//                            test om te kijken hoeveel resultaten gevonden zijn
//                            Toast.makeText(getContext(), "" + servicesFound.size(),
//                                    Toast.LENGTH_SHORT).show();
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that pings to google.com to check if user is actually
     * connected to the internet.
     * @return True if user is connected to the internet
     * and false if user cannot connect to google.com
     * @throws InterruptedException
     * @throws IOException
     */
    public boolean isConnected() throws InterruptedException, IOException
    {
        String command = "ping -c 1 google.com";
        return (Runtime.getRuntime().exec (command).waitFor() == 0);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case LOCATIE_KIEZEN:
                if(resultCode == RESULT_OK) {
                    if (data.hasExtra("long") && data.hasExtra("lat")) {
                        double lng = data.getDoubleExtra("long", 1);
                        double lat = data.getDoubleExtra("lat", 1);
                        location = new Locatie(lng, lat);
                        Log.i("long: ", "" + location.getLongitude());
                        Log.i("lat: ", "" + location.getLatitude());

                    }
                }

        }
    }

    public void updateCurrentLoc(LatLng newLatLong) {
        currentLatLng = newLatLong;
        Log.e("LAT EN LONG MAP: ", "Lat: " + currentLatLng.latitude + " Long: " + currentLatLng.longitude);


        searchServiceRequests();
    }
}

