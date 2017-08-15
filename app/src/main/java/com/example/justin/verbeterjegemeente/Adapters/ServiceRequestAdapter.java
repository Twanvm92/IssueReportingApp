package com.example.justin.verbeterjegemeente.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.justin.verbeterjegemeente.R;
import com.example.justin.verbeterjegemeente.UpdateService;
import com.example.justin.verbeterjegemeente.domain.ServiceRequest;

import java.util.ArrayList;

public class ServiceRequestAdapter extends ArrayAdapter<ServiceRequest> {

    public ServiceRequestAdapter(@NonNull Context context, ArrayList<ServiceRequest> requests) {
        super(context, 0, requests);
    }

    @NonNull
    public View getView(int position, View convertview, @NonNull ViewGroup parent) {
        ServiceRequest serviceRequest = getItem(position);

        if (convertview == null) {
            convertview = LayoutInflater.from(getContext()).inflate(R.layout.tab2_listviewrow, parent, false);
        }


        TextView locatie = (TextView) convertview.findViewById(R.id.activitySRA_tv_locationID);
        locatie.setText(serviceRequest.getLat() + ", " + serviceRequest.getLong());

        TextView beschrijving = (TextView) convertview.findViewById(R.id.activitySRA_tv_beschrijvingID);
        beschrijving.setText(serviceRequest.getDescription());

        TextView MainCatagory = (TextView) convertview.findViewById(R.id.activitySRA_tv_hoofdCategorieID);
        MainCatagory.setText(serviceRequest.getServiceCode());


        TextView subCatagory = (TextView) convertview.findViewById(R.id.activitySRA_tv_subCategorieID);
        subCatagory.setText(serviceRequest.getServiceCode());

        TextView laastUpdate = (TextView) convertview.findViewById(R.id.activitySRA_tv_laatstUpdateID);
        laastUpdate.setText(serviceRequest.getUpdatedDatetime());

        ArrayList<String> unreadSR = UpdateService.getUnreadServiceRequests();
        for (String unsr : unreadSR) {
            Log.i("ServiceRequestAdapter: ", "Unreadsr: " + unsr);
        }
        if (!unreadSR.isEmpty()) { // do nothing if there are no unread updated service requests
            Log.i("ServiceRequestAdapter: ", "amount of unread ids: " + unreadSR.size());
            for (String uSR : unreadSR) { // loop through ids of unread updated service request
                if (uSR.equals(serviceRequest.getServiceRequestId())) {
                    Log.i("ServiceRequestAdapter: ", "Changed color servicerequest: " + serviceRequest.getServiceRequestId());

                    // if unread service request id equals service request id of listview row,
                    // change the background color of listview row
                    LinearLayout parentLayout = (LinearLayout) convertview.findViewById(R.id.tab2fragment_ll_parentLayout);
                    parentLayout.setBackgroundResource(R.color.colorUnreadServiceRequest);
                    Log.i("FollowActivity: ", "Looping through list items");
                }
            }
        }


        return convertview;
    }
}
