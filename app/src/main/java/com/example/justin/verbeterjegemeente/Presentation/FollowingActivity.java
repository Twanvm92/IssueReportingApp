package com.example.justin.verbeterjegemeente.Presentation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.justin.verbeterjegemeente.Adapters.MeldingAdapter;
import com.example.justin.verbeterjegemeente.Business.MeldingGenerator;
import com.example.justin.verbeterjegemeente.R;
import com.example.justin.verbeterjegemeente.domain.Melding;

import java.util.ArrayList;
import java.util.List;

public class FollowingActivity extends AppCompatActivity {


    ArrayList<Melding> list;
    ListView meldingListView;
    private ArrayAdapter meldingAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);

        MeldingGenerator generator = new MeldingGenerator();
        list = new ArrayList<>();
        generator.generate();
        list = generator.getMeldingen();

        meldingListView = (ListView) findViewById(R.id.FollowingListView);
        meldingAdapter = new MeldingAdapter(getApplicationContext(), list);
        meldingListView.setAdapter(meldingAdapter);
        meldingAdapter.notifyDataSetChanged();


    }
}
