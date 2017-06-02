package com.example.justin.verbeterjegemeente.Presentation;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.justin.verbeterjegemeente.API.ConnectionChecker;
import com.example.justin.verbeterjegemeente.API.ServiceClient;
import com.example.justin.verbeterjegemeente.API.ServiceGenerator;

import com.example.justin.verbeterjegemeente.Adapters.MeldingDialogAdapter;
import com.example.justin.verbeterjegemeente.Database.DatabaseHanlder;

import com.example.justin.verbeterjegemeente.Constants;

import com.example.justin.verbeterjegemeente.R;
import com.example.justin.verbeterjegemeente.domain.Locatie;
import com.example.justin.verbeterjegemeente.domain.PostServiceRequestResponse;
import com.example.justin.verbeterjegemeente.domain.Service;
import com.example.justin.verbeterjegemeente.domain.ServiceRequest;
import com.example.justin.verbeterjegemeente.domain.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Deze klasse zorgt ervoor dat de gebruiker een meldingsformulier kan invullen en deze informatie aan de database connectie klasse geeft
 * Alle benodigde informatie moet hier ingevuld worden en zijn er enkele optionele opties die de gebruiker kan kiezen
 * @author Twan van Maastricht
 * @author Justin Kannekens
 * @author Maikel Jacobs
 * @author Thijs van Marle
 * @author Mika Krooswijk
 */
public class MeldingActivity extends AppCompatActivity {


    private Spinner catagorySpinner;
    private Spinner subCatagorySpinner;
    private ArrayList<String> catagoryList;
    private ArrayList<String> subCategoryList;
    private Button locatieButton, fotoButton, terugButton, plaatsButton;
    private TextView locatieTextView, beschrijvingTextView, emailTextView,
            voornaamTextView, achternaamTextView,optioneelTextView;
    private EditText beschrijvingEditText, emailEditText,
            voornaamEditText, achternaamEditText;
    private CheckBox updateCheckBox;
    private ImageView fotoImageView;
    private List<Service> serviceList;
    ArrayAdapter<String> catagoryAdapter;
    ArrayAdapter<String> subCategoryAdapter;
    private ServiceClient client;
    private String image_path = "";
    private Uri selectedImage;
    private android.app.AlertDialog.Builder builder;
    private String imagePath = null;
    private Locatie location;
    private CheckBox onthoudCheckbox;
    private String descr, sc, lName, fName, email, address_string, address_id, jurisdiction_id, imgUrl;
    private Double lon, lat;
    private String[] attribute = {};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_melding);
        final Dialog dialog = new Dialog(this);
        fotoButton = (Button) findViewById(R.id.activityMelding_btn_fotoButton);

        fotoImageView = (ImageView) findViewById(R.id.activityMelding_IV_fotoImageView);

        locatieTextView = (TextView) findViewById(R.id.activityMelding_tv_Location);
        beschrijvingTextView = (TextView) findViewById(R.id.activityMelding_tv_beschrijving);
        emailTextView = (TextView) findViewById(R.id.activityMelding_tv_Email);
        optioneelTextView = (TextView) findViewById(R.id.activityMelding_tv_Optioneel);
        voornaamTextView = (TextView) findViewById(R.id.activityMelding_tv_voorNaam);
        achternaamTextView = (TextView) findViewById(R.id.activityMelding_tv_achterNaam);
        onthoudCheckbox = (CheckBox) findViewById(R.id.activityMelding_cb_onthoudCheckbox);

        updateCheckBox = (CheckBox) findViewById(R.id.activityMelding_cb_updateCheckbox);

        Intent in = getIntent();
        if(in.hasExtra("long")) {
            double lng = in.getDoubleExtra("long", 1);
            double lat = in.getDoubleExtra("lat", 1);
            location = new Locatie(lng, lat);
            //Toast.makeText(this, "Long: " + location.getLongitude() + " Lat: " + location.getLatitude(),Toast.LENGTH_SHORT).show();

        }

        locatieButton = (Button) findViewById(R.id.activityMelding_btn_wijzigLocation);
        locatieButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivityForResult(intent, Constants.LOCATIE_KIEZEN);
            }
        });

        fotoImageView = (ImageView) findViewById(R.id.activityMelding_IV_fotoImageView);

        builder = new android.app.AlertDialog.Builder(this);

        // create an arraylist that will contain different categories fetched from an open311 interface
        catagoryList = new ArrayList<String>();
        catagoryList.add(getResources().getString(R.string.kiesProblemen));
        catagorySpinner = (Spinner) findViewById(R.id.spinner2);
        catagoryAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, catagoryList){
            @Override //pakt de positions van elements in catagoryList en disabled the element dat postion null staat zodat we het kunnen gebruiken als een hint.
            public boolean isEnabled(int position){
                if (position == 0)
                {
                    return false;
                }else{
                    return true;
                }
            }
        };
        catagoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        catagorySpinner.setAdapter(catagoryAdapter);

        // create an arraylist that will contain different sub categories fetched from an open311 interface
        subCategoryList = new ArrayList<String>();
        subCategoryList.add(getResources().getString(R.string.kiesSubProblemen));
        subCatagorySpinner = (Spinner) findViewById(R.id.spinnerSub);
        subCategoryAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, subCategoryList){
            @Override //pakt de positions van elements in subCatagoryList en disabled the element dat postion null staat zodat we het kunnen gebruiken als een hint.
            public boolean isEnabled(int position){
                if (position == 0)
                {
                    return false;
                }else{
                    return true;
                }
            }
        };
        subCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subCatagorySpinner.setAdapter(subCategoryAdapter);

        catagorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (subCategoryList.size() > 1) { // check if list has more than just the default string
                    subCategoryList.clear(); // clear ist before filling it again
                    subCategoryList.add(getResources().getString(R.string.kiesSubProblemen));
                }

                if (serviceList != null) {
                    for (Service s : serviceList) {
                        // check if selected main category is same as main category of service object
                        if (parent.getSelectedItem().toString().equals(s.getGroup())) {
                            subCategoryList.add(s.getService_name()); // add sub category to list
                        }
                    }
                    subCategoryAdapter.notifyDataSetChanged();
                }

                if(position != 0) {
                    subCatagorySpinner.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        fotoButton.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                int PERMISSION_ALL = 1;
                String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                if(!hasPermissions(getApplicationContext(), PERMISSIONS)){
                    ActivityCompat.requestPermissions(MeldingActivity.this, PERMISSIONS, PERMISSION_ALL);
                }

//                permissies aanvragen gaat async, kan niet wachten tot gebruiker antwoord geeft op de aanvraag
//                misschien later nog uitzoeken hoe dit moet.
                final CharSequence[] items = {getString(R.string.fotoMaken), getString(R.string.fotoKiezen)};
                //builder.setTitle("Foto toevoegen");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {

                        if (items[item].equals(getString(R.string.fotoMaken))) {
                            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                                    Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED) {
                                Log.i("CAMERA", "ASKING PERMISSION");
                                reqCameraPermission();
                            }
                            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                                    Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                try {
                                    Intent makePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    startActivityForResult(makePhoto, Constants.FOTO_MAKEN);
                                } catch (Exception e) {
                                    Log.e("PERMISSION", "camera not granted");
                                    reqCameraPermission();
                                }
                            } else {
                                    builder.show();
                            }

                        } else if (items[item].equals(getString(R.string.fotoKiezen))) {
                            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
                                Log.i("STORAGE", "ASKING PERMISSION");
                                reqWriteStoragePermission();
                            }
                            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                try {
                                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                    startActivityForResult(pickPhoto, Constants.FOTO_KIEZEN);
                                } catch (Exception e) {
                                    Log.e("PERMISSION", "storage not granted");
                                    reqWriteStoragePermission();
                                }
                            } else {
                                builder.show();
                            }
                        }
                    }
                });
                builder.show();
               }
            }
        );

        beschrijvingEditText = (EditText) findViewById(R.id.activityMelding_tv_beschrijving);
        emailEditText = (EditText) findViewById(R.id.email);
        voornaamEditText = (EditText) findViewById(R.id.voornaam);
        achternaamEditText = (EditText) findViewById(R.id.achternaam);

        final DatabaseHanlder db = new DatabaseHanlder(getApplicationContext(), null, null, 1);
        User foundUser = db.getUser();
        Log.i("FOUND USER", foundUser.toString());
        if(foundUser != null){
            emailEditText.setText(foundUser.getEmail());
            voornaamEditText.setText(foundUser.getFirstName());
            achternaamEditText.setText(foundUser.getLastName());
        }
        db.close();

        try {
            if(ConnectionChecker.isConnected()) { // check if user is actually connected to the internet
                // create a callback
//                ServiceGenerator.changeApiBaseUrl("https://asiointi.hel.fi/palautews/rest/v1/");
                client = ServiceGenerator.createService(ServiceClient.class);
                Call<List<Service>> serviceCall = client.getServices(Constants.LANG_EN);
                // fire the get request
                serviceCall.enqueue(new Callback<List<Service>>() {
                    @Override
                    public void onResponse(Call<List<Service>> call, Response<List<Service>> response) {
                        // if a response has been received create a list with Services with the responsebody
                        serviceList = response.body();

                        // test what services have been caught in the response
                        if (serviceList != null) {
                            for (Service s : serviceList) {
                                Log.i("Response: ", "" + s.getService_name());
                            }

                        } else {
                            Log.i("Response: ", "List was empty");
                        }

                        if(serviceList != null) {
                            int x = 1; // set iterable separately for categoryList
                            for (int i = 0; i < serviceList.size(); i++) {
                                // first categoryList item is a default String
                                if(catagoryList.size() > 1) { // do something if list already has 1 or more categories
                                    // do something if previous category is not the same as new category in servicelist
                                    if(!catagoryList.get(x).equals(serviceList.get(i).getGroup())) {
                                        catagoryList.add(serviceList.get(i).getGroup()); // add new category
                                        x++; // only up this iterable if new category is added
                                    }
                                } else {
                                    catagoryList.add(serviceList.get(i).getGroup());
                                }
                            }

                            catagoryAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Service>> call, Throwable t) { // something went wrong

                        Toast.makeText(getApplicationContext(), t.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

            } else { // user is not connected to the internet
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.FoutOphalenProblemen),
                        Toast.LENGTH_SHORT).show();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        terugButton = (Button) findViewById(R.id.terugButton);
        terugButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent i = new Intent(getApplicationContext(), MainActivity.class);
//                startActivityForResult(i, Constants.BACK_BUTTON);

                postNotification();
            }
        });

        plaatsButton = (Button) findViewById(R.id.plaatsButton);
        plaatsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (onthoudCheckbox.isChecked()) {
                    if (!emailEditText.equals("") && !voornaamEditText.equals("") && !achternaamEditText.equals("")) {
                        final DatabaseHanlder db = new DatabaseHanlder(getApplicationContext(), null, null, 1);

                        User user = new User();
                        user.setLastName(achternaamEditText.getText().toString());
                        user.setFirstName(voornaamEditText.getText().toString());
                        user.setEmail(emailEditText.getText().toString());
                        db.deleteUser();
                        db.addUser(user);

                        db.close();
                    }
                }


                // initialize selected category and check if selected category is actually a category
                String selecIt = "";
                if (subCatagorySpinner != null && subCatagorySpinner.getSelectedItem() != null
                        && !subCatagorySpinner.getSelectedItem()
                        .equals(getResources().getString(R.string.kiesSubProblemen))) {
                    selecIt = subCatagorySpinner.getSelectedItem().toString();
                } else {
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.kiesSubCategory), Toast.LENGTH_SHORT).show();
//                    return;
                }

                // checks which category is selected and initializes the service code tht matches
                // the category
                 sc = null;
                if (serviceList != null) {
                    for (Service s : serviceList) {
                        if (s.getService_name().equals(selecIt)) {
                            sc = s.getService_code();
                            Log.i("Service code: ", sc);
                        }
                    }
                }

                // create a new file part that contains an image, to send with a post service request.
                // the image path has been provided by the user.
                imgUrl = null;
                if (imagePath != null) {
                    try {
                        File imgFile = new File(imagePath);
                        FileInputStream imageInFile = new FileInputStream(imgFile);
                        byte imageData[] = new byte[(int) imgFile.length()];
                        imageInFile.read(imageData);

                        // Converting Image byte array into Base64 String
                        imgUrl = encodeImage(imageData);
                        Log.i("imageString", imgUrl);

                    } catch (FileNotFoundException e) {
                        System.out.println("Image not found" + e);
                    } catch (IOException ioe) {
                        System.out.println("Exception while reading the Image " + ioe);
                    }
                }

                // initializes a description that the user has provided
                // to send with the post service request
                descr = null;
                if (beschrijvingEditText != null && !beschrijvingEditText.getText().toString().equals("")) {
                    if (beschrijvingEditText.getText().toString().length() >= 10) {
                        descr = beschrijvingEditText.getText().toString();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                getResources().getString(R.string.kBeschrijving), Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.geenBeschrijving), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!updateCheckBox.isChecked() && emailEditText.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(),
                            R.string.eContactGegevens, Toast.LENGTH_SHORT).show();
                    return;
                }

                // initializes an e-mailaddress that the user has provided
                // to send with the post service request
                email = null;
                if (emailEditText != null || !emailEditText.getText().equals("")) {
                    email = emailEditText.getText().toString();
                }

                // initializes a last name that the user has provided
                // to send with the post service request
                fName = null;
                if (voornaamEditText != null || !voornaamEditText.getText().equals("")) {
                    fName = voornaamEditText.getText().toString();

                }

                // initializes a last name that the user has provided
                // to send with the post service request
                lName = null;
                if (achternaamEditText != null || !achternaamEditText.getText().equals("")) {
                    lName = achternaamEditText.getText().toString();
                }

                // initializes a longtitude of the user's current location or a longtitude that
                // has been provided by the user
                lon = null;
                if (location != null) {
                    if (location.getLongitude() != 0.0) {
                        lon = location.getLongitude();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                getResources().getString(R.string.geenLocatie), Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.geenLocatie), Toast.LENGTH_SHORT).show();
//                    return;
                }

                // initializes a latitude of the user's current location or a latitude that
                // has been provided by the user
                lat = null;
                if (location != null) {
                    if (location.getLatitude() != 0.0) {
                        lat =  location.getLatitude();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                getResources().getString(R.string.geenLocatie), Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.geenLocatie), Toast.LENGTH_SHORT).show();
//                    return;
                }

                final ArrayList<ServiceRequest> srListFinal = new ArrayList<>();

                try {
                    if (ConnectionChecker.isConnected()) {  //checking for internet acces.
                        client = ServiceGenerator.createService(ServiceClient.class);

//                      map moet deze gegevens meegeven meegeven
                        lat = 52.00;
                        lon = 10.00;
                        address_string = "adress_string";
                        address_id = "address_id";
                        jurisdiction_id = "1";

                        Call<ArrayList<ServiceRequest>> RequestResponseCall =
                                client.getSimilarServiceRequests(lat.toString(), lon.toString(), "", "10", sc);
                        RequestResponseCall.enqueue(new Callback<ArrayList<ServiceRequest>>() {
                            @Override
                            public void onResponse(Call<ArrayList<ServiceRequest>> call, Response<ArrayList<ServiceRequest>> response) {
                                if (response.isSuccessful()) {
                                    ArrayList<ServiceRequest> srList = response.body();
                                    if (!srList.isEmpty()) {
                                        for (int i = 0; i < srList.size(); i++) {
                                            Log.i("gevondenService", srList.get(i).getServiceRequestId() + "");
                                            srListFinal.add(srList.get(i));
                                        }

                                        dialog.setContentView(R.layout.activity_melding_dialog);
                                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        ListView meldingDialogListView = (ListView) dialog.findViewById(R.id.activity_melding_dialog_listView);
                                        MeldingDialogAdapter meldingDialogAdapter = new MeldingDialogAdapter(getApplicationContext(), srListFinal);
                                        meldingDialogListView.setAdapter(meldingDialogAdapter);
                                        meldingDialogListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                Intent myIntent = new Intent(view.getContext(), DetailedMeldingActivity.class);
                                                ServiceRequest serviceRequest = srListFinal.get(position);
                                                myIntent.putExtra("serviceRequest", (Serializable) serviceRequest);
                                                myIntent.putExtra("ORIGIN", "MeldingActivityDialog");
                                                startActivity(myIntent);
                                            }
                                        });

                                        Button closeBtn = (Button) dialog.findViewById(R.id.activity_melding_dialog_btn_terug);
                                        closeBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialog.dismiss();
                                            }
                                        });

                                        Button postBtn = (Button) dialog.findViewById(R.id.activity_melding_dialog_btn_maakMelding);
                                        postBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                postNotification();
                                                dialog.dismiss();
                                            }
                                        });

                                        dialog.show();

                                    } else {
//                                        postNotification();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<ArrayList<ServiceRequest>> call, Throwable t) {

                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Melding sturen naar de API met meerdere variabele die al gedeclareerd zijn bovenaan de klasse
     * Indien de melding goed verwerkt wordt door de API, wordt het id opgeslagen in de user database en wordt de gebruiker naar zijn favoriete meldingen verwezen
     * Als er iets mis gaat in dit proces wordt een foutmelding getoond aan de gebuiker
     */
    public void postNotification(){
         try {
            if (ConnectionChecker.isConnected()) {
                client = ServiceGenerator.createService(ServiceClient.class);
                Call<ArrayList<PostServiceRequestResponse>> serviceRequestResponseCall =
                        client.postServiceRequest(sc, descr, lat, lon, address_string,
                                address_id, attribute, jurisdiction_id, email, fName, lName, imgUrl);
                // fire the get post request
                serviceRequestResponseCall.enqueue(new Callback<ArrayList<PostServiceRequestResponse>>() {
                    @Override
                    public void onResponse(Call<ArrayList<PostServiceRequestResponse>> call,
                                           Response<ArrayList<PostServiceRequestResponse>> response) {
                        if (response.isSuccessful()) {
                            // if a response was successful get an arraylist of postservicerequestresponses
                            ArrayList<PostServiceRequestResponse> pRespList = response.body();

                            // show the service code that was found in the respond as a toast
                            for (PostServiceRequestResponse psrr : pRespList) {
                                Log.i("Service response: ", psrr.getId());
                                Toast.makeText(getApplicationContext(),
                                        "service request is aangemaakt met id: " + psrr.getId(),
                                        Toast.LENGTH_SHORT).show();

                                final DatabaseHanlder db = new DatabaseHanlder(getApplicationContext(), null, null, 1);
                                db.addReport(psrr.getId());
                                db.close();
                                Intent i = new Intent(getApplicationContext(), FollowingActivity.class);
                                startActivityForResult(i, Constants.BACK_BUTTON);
                            }
                        } else {
                            try { //something went wrong. Show the user what went wrong
                                JSONArray jObjErrorArray = new JSONArray(response.errorBody().string());
                                JSONObject jObjError = (JSONObject) jObjErrorArray.get(0);

                                Toast.makeText(getApplicationContext(), jObjError.getString("description"),
                                        Toast.LENGTH_SHORT).show();
                                Log.i("Error message: ", jObjError.getString("description"));
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    // a connection could not have been made. Tell the user.
                    @Override
                    public void onFailure(Call<ArrayList<PostServiceRequestResponse>> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.ePostRequest),
                                Toast.LENGTH_SHORT).show();
                    }
                });

            } else {// a connection could not have been made. Tell the user.
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.ePostRequest),
                        Toast.LENGTH_SHORT).show();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * controleren of alle permissies zijn gegeven die nodig zijn in dit scherm om alles uit te kunnen voeren
     * @param context
     * @param permissions permissies die gecontroleerd moeten worden
     * @return boolean of ALLE permissies gegeven zijn
     */
    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
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
            case Constants.MY_PERMISSIONS_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    Log.i("PERMISSION", "camera granted");
//                    fotoButton.setEnabled(true);

//                    Log.i("STORAGE", "ASKING PERMISSION");
//                    reqWriteStoragePermission();

                } else {

                    // permission denied, boo! Disable the
                    Log.i("PERMISSION", "camera not granted");
//                    fotoButton.setEnabled(false);

//                    Log.i("STORAGE", "ASKING PERMISSION");
//                    reqWriteStoragePermission();
                    // functionality that depends on this permission.
                }
            }
            break;
            case Constants.MY_PERMISSIONS_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

//                    fotoButton.setEnabled(true);
                    Log.i("PERMISSION", "storage granted");
                } else {

                    // permission denied, boo! Disable the
                    Log.i("PERMISSION", "storage not granted");
//                    fotoButton.setEnabled(false);
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
                        Constants.MY_PERMISSIONS_CAMERA);

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        Constants.MY_PERMISSIONS_CAMERA);

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

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        Constants.MY_PERMISSIONS_STORAGE);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        Constants.MY_PERMISSIONS_STORAGE);
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
            case Constants.FOTO_MAKEN:
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
            case Constants.FOTO_KIEZEN:
                if(resultCode == RESULT_OK){
                    selectedImage = data.getData();
                    image_path = getRealPathFromURI(selectedImage);
                    fotoImageView.setImageURI(selectedImage);
                    fotoButton.setText(R.string.fotoWijzigen);
                    imagePath = image_path;

                }
                break;
            case Constants.LOCATIE_KIEZEN:
                if(resultCode == RESULT_OK){
                    if(data.hasExtra("long")) {
                        double lng = data.getDoubleExtra("long", 1);
                        double lat = data.getDoubleExtra("lat", 1);
                        location = new Locatie(lng, lat);
                        Log.e("long: ", "" + location.getLongitude());
                        Log.e("lat: ", "" + location.getLatitude());

                    }

                }
                break;
            case Constants.BACK_BUTTON:
                if(resultCode == RESULT_CANCELED) {
                    finish();
                    startActivity(getIntent());
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

    /**
     * Encodes the byte array into base64 string
     *
     * @param imageByteArray - byte array
     * @return String a {@link java.lang.String}
     */
    public static String encodeImage(byte[] imageByteArray) {
//        return Base64.encodeBase64URLSafeString(imageByteArray);
        return Base64.encodeToString(imageByteArray, Base64.DEFAULT);
    }


}
