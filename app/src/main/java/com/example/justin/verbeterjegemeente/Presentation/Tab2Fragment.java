package com.example.justin.verbeterjegemeente.Presentation;

import android.content.Context;
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
import android.widget.Button;
import android.widget.ListView;

import com.example.justin.verbeterjegemeente.Adapters.MeldingAdapter;
import com.example.justin.verbeterjegemeente.Business.MeldingGenerator;
import com.example.justin.verbeterjegemeente.Presentation.MeldingActivity;
import com.example.justin.verbeterjegemeente.R;
import com.example.justin.verbeterjegemeente.domain.Melding;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by Justin on 27-4-2017.
 */

public class Tab2Fragment extends Fragment {
    private static final String TAG = "Tab2Fragment";

    private MeldingGenerator generator = new MeldingGenerator();
    private ListView meldingListView;
    private ArrayAdapter meldingAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.tab2_fragment,container,false);

        // Autos in een arraylijst
        //dummyAutos();
        generator.generate();

        // Listview UI referentie
        meldingListView = (ListView) view.findViewById(R.id.meldingListView);
        meldingListView.setAdapter(new MeldingAdapter(this.getContext(), generator.getMeldingen()));

        return view;
    }
}
