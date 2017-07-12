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

        // get user selected radius and cat or use default radius and cat
        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        int rValue = prefs.getInt(getString(R.string.activityMain_saved_radius), 20); // 20 is default
        currentRadius = Integer.toString(rValue);
        servCodeQ = prefs.getString(getString(R.string.activityMain_saved_servcodeQ), null);

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

    public void updateServiceRequests(ArrayList<ServiceRequest> srList) {
        serviceList.clear();
        if (!srList.isEmpty() && srList != null) {
            for (ServiceRequest s : srList) {
                serviceList.add(s);
            }
        }
        serviceRequestAdapter.notifyDataSetChanged();
    }
}

