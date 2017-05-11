package com.example.justin.verbeterjegemeente;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.justin.verbeterjegemeente.domain.Locatie;
import com.example.justin.verbeterjegemeente.domain.Melding;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.util.ArrayList;

public class MeldingActivity extends AppCompatActivity {


    private Spinner catagorySpinner;
    private ArrayList<String> catagoryList;
    private Button locatieButton, fotoButton, terugButton, plaatsButton;
    private TextView locatieTextView, beschrijvingTextView, emailTextView, voornaamTextView, achternaamTextView,optioneelTextView;
    private EditText beschrijvingEditText, emailEditText, voornaamEditText, achternaamEditText;
    private CheckBox updateCheckBox;

    private Location location;

    private static final int MY_PERMISSIONS_CAMERA = 1;
    File destination;
    Uri selectedImage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_melding);



        reqCameraPermission();


        locatieTextView = (TextView) findViewById(R.id.locatieTextView);
        beschrijvingTextView = (TextView) findViewById(R.id.beschrijving);
        emailTextView = (TextView) findViewById(R.id.emailtextview);
        optioneelTextView = (TextView) findViewById(R.id.optioneeltextview);
        voornaamTextView = (TextView) findViewById(R.id.voornaamtextview);
        achternaamTextView = (TextView) findViewById(R.id.achternaamtextview);

        locatieButton = (Button) findViewById(R.id.wijzigLocatieButton);
        locatieButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        catagoryList = new ArrayList<String>();

        //tijdelijk voorbeeld
        catagoryList.add("Kies een categorie");
        catagoryList.add("Afval");
        catagoryList.add("Geluidsoverlast");

        catagorySpinner = (Spinner) findViewById(R.id.spinner2);
        ArrayAdapter<String> catagoryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, catagoryList);
        catagoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        catagorySpinner.setAdapter(catagoryAdapter);



        fotoButton = (Button) findViewById(R.id.fotoButton);


        fotoButton.setEnabled(false);
        fotoButton.setText("Foto toevoegen");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            fotoButton.setEnabled(true);
            fotoButton.setText("foto toevoegen");
        }

        fotoButton.setOnClickListener(new View.OnClickListener() {



            @Override

            public void onClick(View v) {

                ArrayList<String> fotoArray = new ArrayList<String>();
                fotoArray.add("take picture");
                fotoArray.add("chose picture");

                


//                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
//                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    startActivity(cameraIntent);
//                }

               }

            }

        );


        beschrijvingEditText = (EditText) findViewById(R.id.beschrijving);
        emailEditText = (EditText) findViewById(R.id.email);
        voornaamEditText = (EditText) findViewById(R.id.voornaam);
        achternaamEditText = (EditText) findViewById(R.id.achternaam);

        updateCheckBox = (CheckBox) findViewById(R.id.updateCheckBox);

        terugButton = (Button) findViewById(R.id.terugButton);
        terugButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        plaatsButton = (Button) findViewById(R.id.plaatsButton);
        plaatsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Locatie locatie = new Locatie();
                Boolean checked = false;

                if( updateCheckBox.isChecked()){
                    checked = true;
                }else if(!updateCheckBox.isChecked()){
                    checked = false;
                }

                Melding melding = new Melding();
                melding.setLocatie(locatie);
                melding.setCategorie(catagorySpinner.getSelectedItem().toString());
                //melding.setFoto();
                melding.setBeschrijving(beschrijvingEditText.getText().toString());
                melding.setEmail(emailEditText.getText().toString());
                melding.setVoornaam(voornaamEditText.getText().toString());
                melding.setAchternaam(achternaamEditText.getText().toString());
                melding.setUpdate(checked);

              //  Log.i("MELDING", "" + melding.toString());
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    Log.i("MDGK", "granted");

                } else {

                    // permission denied, boo! Disable the
                    Log.i("MDGK", "not granted");
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void reqCameraPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_CAMERA);

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_CAMERA);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    public void onActivityResult(int requestCode,int resultCode, Intent data) {
        location.setLongitude(data.getDoubleExtra("long", 1));
        location.setLongitude(data.getDoubleExtra("lat", 1));
    }


}
