package com.example.justin.verbeterjegemeente.Presentation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.justin.verbeterjegemeente.R;
import com.example.justin.verbeterjegemeente.domain.Melding;

public class DetailedMeldingActivity extends AppCompatActivity {

    private Melding melding;
    private Button terugButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_melding);

//        terugButton = (Button) findViewById(R.id.terugButton2);
//        terugButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(getApplicationContext(), Tab2Fragment.class);
//                startActivity(i);
//            }
//        });

//        Bundle extras = getIntent().getExtras();
//        if (extras !=null){
//            melding = (Melding) extras.getSerializable("melding");
//        }
//
//        TextView beschrijving = (TextView) findViewById(R.id.beschrijvingTextView);
//        TextView categorie = (TextView) findViewById(R.id.Status_Id);
//
//        beschrijving.setText(melding.getBeschrijving());
//        categorie.setText(melding.getHoofdcategorie());



    }
}
