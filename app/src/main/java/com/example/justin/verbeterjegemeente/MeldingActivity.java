package com.example.justin.verbeterjegemeente;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
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
import com.mifmif.common.regex.Main;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.R.id.list;

public class MeldingActivity extends AppCompatActivity {


    private Spinner catagorySpinner;
    private ArrayList<String> catagoryList;
    private Button locatieButton, fotoButton, terugButton, plaatsButton;
    private TextView locatieTextView, beschrijvingTextView, emailTextView, voornaamTextView, achternaamTextView,optioneelTextView;
    private EditText beschrijvingEditText, emailEditText, voornaamEditText, achternaamEditText;
    private CheckBox updateCheckBox;

    private String image_path = "";
    private static final int MY_PERMISSIONS_CAMERA = 1;
    private static final int MY_PERMISSIONS_STORAGE = 2;

    private static final int FOTO_MAKEN = 1;
    private static final int FOTO_KIEZEN = 2;
    private Uri selectedImage;

    private android.app.AlertDialog.Builder builder;

    private String imageUri = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_melding);

        reqCameraPermission();
        reqWriteStoragePermission();

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

        builder = new android.app.AlertDialog.Builder(this);

        fotoButton.setOnClickListener(new View.OnClickListener() {



            @Override

            public void onClick(View v) {
                final CharSequence[] items = {"Foto maken", "Kies bestaande foto", "Terug"};
                //builder.setTitle("Foto toevoegen");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {

                        if (items[item].equals("Foto maken")) {
                            Intent makePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(makePhoto, FOTO_MAKEN);
                        } else if (items[item].equals("Kies bestaande foto")) {
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pickPhoto, FOTO_KIEZEN);
                        } else if (items[item].equals("Terug")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
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
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
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
                melding.setFotoUrl(imageUri.toString());
                melding.setBeschrijving(beschrijvingEditText.getText().toString());
                melding.setEmail(emailEditText.getText().toString());
                melding.setVoornaam(voornaamEditText.getText().toString());
                melding.setAchternaam(achternaamEditText.getText().toString());
                melding.setUpdate(checked);

                Log.i("MELDING", "" + melding.toString());
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
                    fotoButton.setEnabled(true);

                } else {

                    // permission denied, boo! Disable the
                    Log.i("MDGK", "not granted");
                    fotoButton.setEnabled(false);
                    // functionality that depends on this permission.
                }
            }
            break;
            case MY_PERMISSIONS_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    fotoButton.setEnabled(true);
                    Log.i("MDGK", "granted");
                } else {

                    // permission denied, boo! Disable the
                    Log.i("MDGK", "not granted");
                    fotoButton.setEnabled(false);
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

    public void reqWriteStoragePermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_STORAGE);
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case FOTO_MAKEN:
                if (resultCode == RESULT_OK) {
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    selectedImage = getImageUri(getApplicationContext(), photo);

                    File finalFile = new File(getRealPathFromURI(selectedImage));
//                  test of image_path correct gepakt wordt
                    fotoButton.setText(finalFile.toString());
                    imageUri = finalFile.toString();


//                  indien foto getoond moet worden
//                  imageView.setImageBitmap(photo);
                }
                break;
            case FOTO_KIEZEN:
                if(resultCode == RESULT_OK){
                    selectedImage = data.getData();
                    image_path = getRealPathFromURI(selectedImage);

                    fotoButton.setText(image_path);
                    imageUri = image_path;
//                  imageview.setImageURI(selectedImage);
                }
                break;
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri)
    {
        try
        {
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        catch (Exception e)
        {
            return uri.getPath();
        }
    }
}
