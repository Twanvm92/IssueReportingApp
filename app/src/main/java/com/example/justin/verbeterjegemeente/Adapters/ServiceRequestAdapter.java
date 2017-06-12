package com.example.justin.verbeterjegemeente.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.justin.verbeterjegemeente.R;
import com.example.justin.verbeterjegemeente.domain.ServiceRequest;

import java.util.ArrayList;

/**
 * Created by Mika Krooswijk on 17-5-2017.
 */

public class ServiceRequestAdapter extends ArrayAdapter<ServiceRequest> {

    public ServiceRequestAdapter(@NonNull Context context, ArrayList<ServiceRequest> requests) {
        super(context, 0, requests);
    }

    public View getView(int position, View convertview, ViewGroup parent) {
        ServiceRequest serviceRequest = getItem(position);

        if (convertview == null) {
            convertview = LayoutInflater.from(getContext()).inflate(R.layout.tab2_listviewrow, parent, false);
        }


        TextView locatie = (TextView) convertview.findViewById(R.id.activitySRA_tv_locationID);
        locatie.setText(serviceRequest.getLat() + ", " + serviceRequest.getLong());

        TextView beschrijving = (TextView) convertview.findViewById(R.id.activitySRA_tv_beschrijvingID);
        beschrijving.setText(serviceRequest.getDescription());

//        TextView voornaam = (TextView) convertview.findViewById(R.id.beschrijvingID);
//        voornaam.setText(serviceRequest.get());


        TextView MainCatagory = (TextView) convertview.findViewById(R.id.activitySRA_tv_hoofdCategorieID);
        MainCatagory.setText(serviceRequest.getServiceCode());


        TextView subCatagory = (TextView) convertview.findViewById(R.id.activitySRA_tv_subCategorieID);
        subCatagory.setText(serviceRequest.getServiceCode());

//        TextView update = (TextView) convertview.findViewById(R.id.activitySRA_tv_laatstUpdateID);
//        update.setText(serviceRequest.getUpdatedDatetime());

        TextView laastUpdate = (TextView) convertview.findViewById(R.id.activitySRA_tv_laatstUpdateID);
        laastUpdate.setText(serviceRequest.getUpdatedDatetime());


        return convertview;
    }
}
