package com.example.justin.verbeterjegemeente.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.justin.verbeterjegemeente.R;
import com.example.justin.verbeterjegemeente.model.ServiceRequest;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MeldingDialogAdapter extends ArrayAdapter<ServiceRequest> {

    public MeldingDialogAdapter(Context context, ArrayList<ServiceRequest> requests) {
        super(context, 0, requests);
    }

    public View getView(int position, View convertview, ViewGroup parent) {
        ServiceRequest serviceRequest = getItem(position);

        if (convertview == null) {
            convertview = LayoutInflater.from(getContext()).inflate(R.layout.activity_melding_dialog_listviewrowww, parent, false);
        }

        ImageView imageSmall = (ImageView) convertview.findViewById(R.id.meldingDialog_iv_imageSmall_ID);

        // get the lsit of media urls inside the service request
        List<String> srMediaUrls = serviceRequest.getMediaUrls();
        if (!srMediaUrls.isEmpty()) {
            String mediaURL = serviceRequest.getMediaUrls().get(0);
            // load image from service request into imageview
            Picasso.with(getContext()).load(mediaURL).into(imageSmall);

            Log.i("DetailMeldingActivity: ", "Mediaurl: " + mediaURL);
        }

        TextView locatie = (TextView) convertview.findViewById(R.id.meldingDialog_tv_locatieID);
        locatie.setText(serviceRequest.getLat() + ", " + serviceRequest.getLong());

        TextView laastUpdate = (TextView) convertview.findViewById(R.id.meldingDialog_tv_laatstUpdateID);
        laastUpdate.setText(serviceRequest.getUpdatedDatetime());

        TextView beschrijving = (TextView) convertview.findViewById(R.id.meldingDialog_tv_beschrijvingID);
        beschrijving.setText(serviceRequest.getDescription());


        return convertview;
    }
}