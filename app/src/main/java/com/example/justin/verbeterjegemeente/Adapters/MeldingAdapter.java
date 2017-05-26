package com.example.justin.verbeterjegemeente.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.justin.verbeterjegemeente.R;
import com.example.justin.verbeterjegemeente.domain.Melding;

import java.util.ArrayList;

public class MeldingAdapter extends ArrayAdapter<Melding> {
    public MeldingAdapter(Context context, ArrayList<Melding> meldingen){
        super(context, 0, meldingen);
    }

    @Override
    public View getView(int position, View convertview, ViewGroup parent) {

        Melding melding = getItem(position);

        if (convertview == null){
            convertview = LayoutInflater.from(getContext()).inflate(R.layout.tab2_listviewrow, parent, false);
        }

        TextView hoofdcategorie = (TextView) convertview.findViewById(R.id.activitySRA_tv_hoofdCategorieID);
        hoofdcategorie.setText(melding.getHoofdcategorie());

        TextView subccategorie = (TextView) convertview.findViewById(R.id.activitySRA_tv_subCategorieID);
        subccategorie.setText(melding.getSubcategorie());

        TextView beschrijving = (TextView) convertview.findViewById(R.id.activitySRA_tv_beschrijvingID);
        beschrijving.setText(melding.getBeschrijving());

        return convertview;


    }
}
