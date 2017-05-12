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
import android.graphics.Path;
import android.media.Image;
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
import android.widget.ImageView;
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

/**
 * Deze klasse zorgt ervoor dat de gebruiker een meldingsformulier kan invullen en deze informatie aan de database connectie klasse geeft
 * Alle benodigde informatie moet hier ingevuld worden en zijn er enkele optionele opties die de gebruiker kan kiezen
 */
public class MeldingActivity extends AppCompatActivity {


    private Spinner catagorySpinner;
    private ArrayList<String> catagoryList;
    private Button locatieButton, fotoButton, terugButton, plaatsButton;
    private TextView locatieTextView, beschrijvingTextView, emailTextView, voornaamTextView, achternaamTextView,optioneelTextView;
    private EditText beschrijvingEditText, emailEditText, voornaamEditText, achternaamEditText;
    private CheckBox updateCheckBox;
    private ImageView fotoImageView;

    private String image_path = "";
    private static final int MY_PERMISSIONS_CAMERA = 1;
    private static final int MY_PERMISSIONS_STORAGE = 2;

    private static final int FOTO_MAKEN = 1;
    private static final int FOTO_KIEZEN = 2;
    private Uri selectedImage;

    private android.app.AlertDialog.Builder builder;

    private String imagePath = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_melding);
        fotoButton = (Button) findViewById(R.id.fotoButton);

        fotoImageView = (ImageView) findViewById(R.id.fotoImageView);

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

        catagoryList = new ArrayList<>();

        //tijdelijk voorbeeld
        catagoryList.add("Kies een categorie");
        catagoryList.add("Afval");
        catagoryList.add("Geluidsoverlast");

        catagorySpinner = (Spinner) findViewById(R.id.spinner2);
        ArrayAdapter<String> catagoryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, catagoryList);
        catagoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        catagorySpinner.setAdapter(catagoryAdapter);


        fotoImageView = (ImageView) findViewById(R.id.fotoImageView);

        builder = new android.app.AlertDialog.Builder(this);

        fotoButton.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED) {
                    Log.i("CAMERA", "ASKING PERMISSION");
                    reqCameraPermission();
                }

                final CharSequence[] items = {getString(R.string.fotoMaken), getString(R.string.fotoKiezen)};
                //builder.setTitle("Foto toevoegen");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {

                        if (items[item].equals(getString(R.string.fotoMaken))) {
                            Intent makePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(makePhoto, FOTO_MAKEN);
                        } else if (items[item].equals(getString(R.string.fotoKiezen))) {
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pickPhoto, FOTO_KIEZEN);
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


                // Check if checkbox is checked or not
                if( updateCheckBox.isChecked()){
                    checked = true;
                }else if(!updateCheckBox.isChecked()){
                    checked = false;
                }

                // Put al the information from the issue from into an object
                Melding melding = new Melding();
                melding.setLocatie(locatie);
                melding.setCategorie(catagorySpinner.getSelectedItem().toString());
                melding.setFotoUrl(imagePath);
                melding.setBeschrijving(beschrijvingEditText.getText().toString());
                melding.setEmail(emailEditText.getText().toString());
                melding.setVoornaam(voornaamEditText.getText().toString());
                melding.setAchternaam(achternaamEditText.getText().toString());
                melding.setUpdate(checked);

                boolean emailNull = true;

                emailNull = !beschrijvingEditText.equals("");

                Log.i("EMAIL", "" + emailNull );
                Log.i("EMAIL", "ksjh" );

                // Inform the user the issue has been submitted
                Toast toast = Toast.makeText(getApplicationContext(), "melding geplaatst", Toast.LENGTH_SHORT);
                toast.show();


                // Intent back to the Home screen
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });
    }

    /**
     * Controleren of permissies goed gekeurd zijn door de gebruiker
     * @param requestCode meegegeven activiteit nummer die gedaan is
     * @param permissions permissies die aangevraagd worden
     * @param grantResults hoeveelheid permissies die goed gekeurd zijn door de gebruiker
     */
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

                    Log.i("PERMISSION", "camera granted");
                    fotoButton.setEnabled(true);

                    Log.i("STORAGE", "ASKING PERMISSION");
                    reqWriteStoragePermission();

                } else {

                    // permission denied, boo! Disable the
                    Log.i("PERMISSION", "camera not granted");
                    fotoButton.setEnabled(false);

                    Log.i("STORAGE", "ASKING PERMISSION");
                    reqWriteStoragePermission();
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
                    Log.i("PERMISSION", "storage granted");
                } else {

                    // permission denied, boo! Disable the
                    Log.i("PERMISSION", "storage not granted");
                    fotoButton.setEnabled(false);
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    /**
     * Vragen om permissie voor het gebruik van de camera
     */
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

    /**
     * Vragen om permissie voor het aanpassen van de opslagruimte van de gebruiker
     */
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

    /**
     * Resultaat ophalen uit de activiteit die uitgevoerd is
     * @param requestCode meegegeven activiteit nummer die gedaan is
     * @param resultCode controle of er een result uit voortgekomen is
     * @param data het resultaat
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case FOTO_MAKEN:
                if (resultCode == RESULT_OK) {
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    selectedImage = getImageUri(getApplicationContext(), photo);

                    File finalFile = new File(getRealPathFromURI(selectedImage));
//                  test of image_path correct gepakt wordt
                    fotoImageView.setImageURI(selectedImage);
                    fotoButton.setText(R.string.fotoWijzigen);
                    imagePath = finalFile.toString();

                }
                break;
            case FOTO_KIEZEN:
                if(resultCode == RESULT_OK){
                    selectedImage = data.getData();
                    image_path = getRealPathFromURI(selectedImage);
                    fotoImageView.setImageURI(selectedImage);
                    fotoButton.setText(R.string.fotoWijzigen);
                    imagePath = image_path;

           }
                break;
        }
    }

    /**
     * Haalt de uri uit de bitmap, dit moet eerst gedaan worden als een foto gemaakt wordt (en dus niet gekozen wordt uit storage)
     * Formateerd de bitmap waarna het pad van de image geparsed wordt naar een Uri
     * @param inContext de context die gebruikt wordt
     * @param inImage de bitmap van de foto die gemaakt is door de gebruiker
     * @return geeft een Uri terug die verder gebruikt kan worden
     */
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    /**
     * Pad ophalen van een image door middel van de uri
     * @param uri de uri van de image die de gebruiker kiest
     * @return het pad van de image als een String
     */
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
