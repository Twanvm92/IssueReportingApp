package com.example.justin.verbeterjegemeente.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.justin.verbeterjegemeente.Presentation.DetailedMeldingActivity;
import com.example.justin.verbeterjegemeente.R;
import com.example.justin.verbeterjegemeente.domain.ServiceRequest;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MeldingDialogAdapter extends ArrayAdapter<ServiceRequest> {

    public MeldingDialogAdapter(Context context, ArrayList<ServiceRequest> requests) {
        super(context, 0, requests);
    }

    public View getView(int position, View convertview, ViewGroup parent){
        ServiceRequest serviceRequest = getItem(position);

        if(convertview == null){
            convertview = LayoutInflater.from(getContext()).inflate(R.layout.activity_melding_dialog_listviewrow, parent, false);
        }

        ImageView imageSmall = (ImageView) convertview.findViewById(R.id.meldingDialog_iv_imageSmall_ID);
        Picasso.with(getContext()).load(serviceRequest.getMediaUrl()).into(imageSmall);

        TextView locatie = (TextView) convertview.findViewById(R.id.meldingDialog_tv_locatieID);
        locatie.setText(serviceRequest.getLat() + ", " + serviceRequest.getLong());

        TextView laastUpdate = (TextView) convertview.findViewById(R.id.meldingDialog_tv_laatstUpdateID);
        laastUpdate.setText(serviceRequest.getUpdatedDatetime());

        TextView beschrijving = (TextView) convertview.findViewById(R.id.meldingDialog_tv_beschrijvingID);
        beschrijving.setText(serviceRequest.getDescription());


        return convertview;
    }
}