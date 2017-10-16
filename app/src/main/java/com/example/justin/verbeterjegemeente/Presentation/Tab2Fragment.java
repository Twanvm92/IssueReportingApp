package com.example.justin.verbeterjegemeente.Presentation;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.justin.verbeterjegemeente.Adapters.ServiceRequestAdapter;
import com.example.justin.verbeterjegemeente.R;
import com.example.justin.verbeterjegemeente.model.ServiceRequest;

import java.util.ArrayList;


public class Tab2Fragment extends Fragment  {

    private static final String TAG = "Tab2Fragment";
    private ArrayAdapter serviceRequestAdapter;
    private ArrayList<ServiceRequest> serviceList;
    private ServiceRequest serviceRequest;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab2_fragment, container, false);

        serviceList = new ArrayList<>();

        ListView meldingListView = (ListView) view.findViewById(R.id.meldingListView);

        serviceRequestAdapter = new ServiceRequestAdapter(getContext(), serviceList);
        meldingListView.setAdapter(serviceRequestAdapter);
        meldingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent myIntent = new Intent(view.getContext(), DetailedMeldingActivity.class);
                serviceRequest = serviceList.get(position);
                myIntent.putExtra("serviceRequest", serviceRequest);
                myIntent.putExtra("ORIGIN", "Tab2Fragment");
                startActivity(myIntent);

            }
        });

        return view;
    }

    public void updateServiceRequests(ArrayList<ServiceRequest> srList) {
        serviceList.clear();
        if (!srList.isEmpty()) {
            for (ServiceRequest s : srList) {
                serviceList.add(s);
            }
        }
        serviceRequestAdapter.notifyDataSetChanged();
    }
}

