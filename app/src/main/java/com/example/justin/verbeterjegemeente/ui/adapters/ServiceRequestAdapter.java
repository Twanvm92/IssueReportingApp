package com.example.justin.verbeterjegemeente.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.justin.verbeterjegemeente.API.RequestManager;
import com.example.justin.verbeterjegemeente.data.database.DatabaseHandler;
import com.example.justin.verbeterjegemeente.R;
import com.example.justin.verbeterjegemeente.app.UpdateService;
import com.example.justin.verbeterjegemeente.service.model.ServiceRequest;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.ArrayList;

public class ServiceRequestAdapter extends ArrayAdapter<ServiceRequest> {

    public ServiceRequestAdapter(@NonNull Context context, ArrayList<ServiceRequest> requests) {
        super(context, 0, requests);
    }

    @NonNull
    public View getView(int position, View convertview, @NonNull ViewGroup parent) {
        ServiceRequest serviceRequest = getItem(position);
        DatabaseHandler db = new DatabaseHandler(getContext(), null, null, 1 );

        if (convertview == null) {
            convertview = LayoutInflater.from(getContext()).inflate(R.layout.tab2_listviewrow, parent, false);
        }

        TextView numbOfUpvoted = (TextView) convertview.findViewById(R.id.activitySRA_tv_upvoteText);
        numbOfUpvoted.setText(String.valueOf(serviceRequest.getUpvotes()));

        LikeButton followBtn = (LikeButton) convertview.findViewById(R.id.favorietenknopdetail_ListviewRow);

        String serviceRequestID = serviceRequest.getServiceRequestId();
        if(db.ReportExists(serviceRequestID)){
            followBtn.setLiked(true);
        } else {
            followBtn.setLiked(false);
        }

        followBtn.setOnLikeListener(new OnLikeListener() {
            @Override

            // if the like star is pressed when the button is not liked, the like method is called.
            // This method adds the selectedServiceRequest to the database and sets the star to liked.
            public void liked(LikeButton likeButton) {

                try {
                    if (!db.ReportExists(serviceRequestID)) {
                        db.addReport(serviceRequestID);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // When the star is presses when the button is liked, the unLike method is called.
            // This method deletes the selected.ServiceRequest from the database and set the button to unLiked.
            @Override
            public void unLiked(LikeButton likeButton) {
                try {
                    if (db.ReportExists(serviceRequestID)) {
                        db.deleteReport(serviceRequestID);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        ImageButton upvoteButton = (ImageButton) convertview.findViewById(R.id.upvoteknopdetail_ListviewRow);

        upvoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!db.upvoteExists(serviceRequestID)) {
                    // add upvote to sqlite database
                    db.addUpvote(serviceRequestID);
                    // add upvote to service request in Gemeente Database
                    sendUpvoteToAPI(serviceRequestID);
                    addUpvoteToTextview(numbOfUpvoted);
                    // make sure user cant see the upvote button anymore
//                    upvoteButton.setVisibility(View.INVISIBLE);
                    // let user know service request is upvoted
                    Toast.makeText(getContext(), getContext().getString(R.string.upvoteSucces), Toast.LENGTH_SHORT).show();
                } else {
                    // let user know he/she already upvoted this service request
                    Toast.makeText(getContext(), getContext().getString(R.string.alreadyUpvoted), Toast.LENGTH_SHORT).show();
                }
            }
        });

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
                    ConstraintLayout parentLayout = (ConstraintLayout) convertview.findViewById(R.id.tab2fragment_ll_parentLayout);
                    parentLayout.setBackgroundResource(R.color.colorUnreadServiceRequest);
                    Log.i("FollowActivity: ", "Looping through list items");
                }
            }
        }


        return convertview;
    }

    /**
     * Send a post request to Open311 Interface notifying about the service request that
     * has been upvoted.
     * @param serviceRequestID the ID of the service request that just has been upvoted by the user
     */
    private void sendUpvoteToAPI(String serviceRequestID) {
        String extraDescription = "";
        RequestManager rManager = new RequestManager(getContext());
        rManager.upvoteServiceRequest(serviceRequestID, extraDescription);
    }

    /**
     * Add + 1 to the number of upvotes that are being shown on the UI.
     */
    // TODO: 24-8-2017 updating upvote count still not working
    public void addUpvoteToTextview(TextView numbOfUpvoted) {
        String upvoteText = numbOfUpvoted.getText().toString();
        Integer updatedUpvoteNumb = Integer.parseInt(upvoteText) + 1;
        numbOfUpvoted.setText(String.valueOf(updatedUpvoteNumb));
    }
}
