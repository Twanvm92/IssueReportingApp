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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.justin.verbeterjegemeente.API.RequestManager;
import com.example.justin.verbeterjegemeente.Adapters.MeldingDialogAdapter;
import com.example.justin.verbeterjegemeente.Business.ServiceManager;
import com.example.justin.verbeterjegemeente.Constants;
import com.example.justin.verbeterjegemeente.Database.DatabaseHandler;
import com.example.justin.verbeterjegemeente.R;
import com.example.justin.verbeterjegemeente.domain.PostServiceRequestResponse;
import com.example.justin.verbeterjegemeente.domain.Service;
import com.example.justin.verbeterjegemeente.domain.ServiceRequest;
import com.example.justin.verbeterjegemeente.domain.User;
import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides the ability for the user to fill in a service request report that then will be send through an
 * open311 Interface. The uer can choose to store some personal information in the users phone.
 *
 * @author Twan van Maastricht
 * @author Justin Kannekens
 * @author Maikel Jacobs
 * @author Thijs van Marle
 * @author Mika Krooswijk
 */
public class MeldingActivity extends AppCompatActivity implements RequestManager.OnServicesReady,
        RequestManager.OnServiceRequestsReady, RequestManager.OnServiceRequestPosted {

    private Spinner subCatagorySpinner;
    private Spinner catagorySpinner;
    private ArrayList<String> catagoryList;
    private ArrayList<String> subCategoryList;
    private Button locatieButton;
    private Button fotoButton;
    private EditText beschrijvingEditText, emailEditText;
    private EditText voornaamEditText, achternaamEditText;
    private CheckBox updateCheckBox;
    private ImageView fotoImageView;
    private List<Service> serviceList;
    ArrayAdapter<String> catagoryAdapter;
    ArrayAdapter<String> subCategoryAdapter;
    private android.app.AlertDialog.Builder builder;
    private String imagePath = null;
    private LatLng location, mapLocation;
    private CheckBox onthoudCheckbox;
    private String descr, sc, lName, fName, email, address_string, address_id, jurisdiction_id, imgUrl, selectedItem;
    private Double lon, lat;
    private Float zoom;
    private String[] attribute = {};
    private boolean marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_melding);
        fotoButton = (Button) findViewById(R.id.activityMelding_btn_fotoButton);
        fotoImageView = (ImageView) findViewById(R.id.activityMelding_IV_fotoImageView);
        onthoudCheckbox = (CheckBox) findViewById(R.id.activityMelding_cb_onthoudCheckbox);
        updateCheckBox = (CheckBox) findViewById(R.id.activityMelding_cb_updateCheckbox);

        locatieButton = (Button) findViewById(R.id.activityMelding_btn_wijzigLocation);
        locatieButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = createIntentWithLocation();
                startActivityForResult(intent, Constants.LOCATIE_KIEZEN);
            }
        });

        setupCategorySpinner();
        setupSubCategorySpinner();
        getServicesForSpinner();

        catagorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fillSubCategorySpinner(parent);
                // reset the selected sub catagory to the default one everytime
                // a new catagory is selected
                if (position != 0) {
                    subCatagorySpinner.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // something is always selected...also by default
            }
        });

        fotoButton.setOnClickListener(new View.OnClickListener() {
                                          @Override

                                          public void onClick(View v) {
                                              int PERMISSION_ALL = 1;
                                              String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                                              if (!hasPermissions(getApplicationContext(), PERMISSIONS)) {
                                                  ActivityCompat.requestPermissions(MeldingActivity.this, PERMISSIONS, PERMISSION_ALL);
                                              }

                                              setupPhotoBtnDialog();
                                          }
                                      }
        );

        beschrijvingEditText = (EditText) findViewById(R.id.activityMelding_tv_beschrijving);
        emailEditText = (EditText) findViewById(R.id.email);
        voornaamEditText = (EditText) findViewById(R.id.voornaam);
        achternaamEditText = (EditText) findViewById(R.id.achternaam);

        // automatically fill in the email, first name and last name if they were saved by the user
        fillPersonalInfo();

        Button terugButton = (Button) findViewById(R.id.terugButton);
        terugButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivityForResult(i, Constants.BACK_BUTTON);
            }
        });

        Button plaatsButton = (Button) findViewById(R.id.plaatsButton);
        plaatsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveUser();

                if (!assignLong()) {// if long is not found, break stop posting service request
                    return;
                }
                if (!assignLat()) {// if lat is not found, stop posting service request
                    return;
                }
                if (!getSelectedSubCat()) {// if sub category is not selected, stop posting service request
                    return;
                }

                assignServiceCode();
                createImgUrl();

                if (!assignDescription()) {// if description is not given, stop posting service request
                    return;
                }
                if (!updateCheckBox.isChecked() && emailEditText.getText().toString().equals("")) {
                    // if update checkbox is not checked and no email is filled in, let user know
                    // and stop posting service request
                    Toast.makeText(getApplicationContext(),
                            R.string.eContactGegevens, Toast.LENGTH_SHORT).show();
                    return;
                }

                assignEmail();
                assignFirstName();
                assignLastName();

//              this has to be added to service request
                address_string = "adress_string";
                address_id = "address_id";
                jurisdiction_id = "1";

                getSimilarServiceRequests();
            }
        });
    }

    /**
     * Send a service request report through the open311 Interface.
     *
     */
    public void postNotification() {
        RequestManager requestManager = new RequestManager(this);
        requestManager.setOnServiceReqPostedCallb(this);
        requestManager.postServiceRequest(sc, descr, lat, lon, address_string,
                address_id, attribute, jurisdiction_id, email, fName, lName, imgUrl);
    }

    /**
     * Checks if all permissions are giving that are needed to fulfill all tasks in this activity.
     *
     * @param context Context of this activity
     * @param permissions permissions that have to be checked
     * @return boolean Returns true if all permissions are granted. Returns false if one of the
     * neccesary permissions are not granted.
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
     * Checks if permissions are granted by the user.
     *
     * @param requestCode  Activity number that is given
     * @param permissions  permissions asked
     * @param grantResults number of permissions that are granted by the user
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

                    Log.i(Constants.PERMISSION, "camera granted");
//                    fotoButton.setEnabled(true);

//                    Log.i("STORAGE", "ASKING PERMISSION");
//                    reqWriteStoragePermission();

                } else {

                    // permission denied, boo! Disable the
                    Log.i(Constants.PERMISSION, "camera not granted");
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
                    Log.i(Constants.PERMISSION, "storage granted");
                } else {

                    // permission denied, boo! Disable the
                    Log.i(Constants.PERMISSION, "storage not granted");
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
     * Ask for permission to use the phone camera
     */
    public void reqCameraPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    Constants.MY_PERMISSIONS_CAMERA);

        }
    }

    /**
     * Ask for permission to adjust the storage of the users phone
     */
    public void reqWriteStoragePermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    Constants.MY_PERMISSIONS_STORAGE);

        }
    }

    /**
     * Gets the result out of the activity that was started from within the current activity.
     *
     * @param requestCode Activity number that is given
     * @param resultCode  Check if a result has been given
     * @param data        The result
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Constants.FOTO_MAKEN:
                Uri selectedImage;
                if (resultCode == RESULT_OK) {
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
//                The following commented code works but the API doesnt accept the long String URI
//                    -----------------------------------------------
//                    selectedImage = getImageUri(getApplicationContext(), photo);
//                    File finalFile = new File(getRealPathFromURI(selectedImage));
//                    fotoImageView.setImageURI(selectedImage);
                    fotoImageView.setImageBitmap(photo);
                    fotoButton.setText(R.string.fotoWijzigen);
//                    imagePath = finalFile.toString();

                }
                break;
            case Constants.FOTO_KIEZEN:
                if (resultCode == RESULT_OK) {
                    selectedImage = data.getData();
                    String image_path = getRealPathFromURI(selectedImage);
                    fotoImageView.setImageURI(selectedImage);
                    fotoButton.setText(R.string.fotoWijzigen);
                    imagePath = image_path;

                }
                break;
            case Constants.LOCATIE_KIEZEN:
                if (resultCode == RESULT_OK) {
                    if (data.hasExtra("long")) {
                        double lng = data.getDoubleExtra("long", 1);
                        double lat = data.getDoubleExtra("lat", 1);
                        zoom = data.getFloatExtra("zoom", 16.0f);
                        mapLocation = new LatLng(lat, lng);
                        location = mapLocation;

                        Log.e("long: ", "" + mapLocation.longitude);
                        Log.e("lat: ", "" + mapLocation.latitude);
                        marker = true;
                        locatieButton.setText(getResources().getString(R.string.locatieWijzigen));
                    }

                }
                break;
            case Constants.BACK_BUTTON:
                if (resultCode == RESULT_CANCELED) {
                    finish();
                    startActivity(getIntent());
                }
                break;

        }
    }

    /**
     * Takes the uri out of a bitmap. This has to be done when a picture is taken and not when
     * a picture is taken from the storage. Parses the path of the picture to an URI after formatting the bitmap.
     *
     * @param inContext The context being used
     * @param inImage   Bitmap of the picture that has been taken by the user
     * @return The image URI
     */
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    /**
     * Gets the path of an img by using the img uri
     *
     * @param uri the uri of the image selected by the user
     * @return the path of the image as a string
     */
    public String getRealPathFromURI(Uri uri) {
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
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

    /**
     * Add newly posted service request to the database on users phone.
     * After that, redirect user to a list with his posted and followed service requests.
     * @param id the id of the post service response. This is a unique id that belongs to
     *           the newly created service request.
     * @see PostServiceRequestResponse
     *
     */
    public void insertReport(String id) {
        DatabaseHandler db = new DatabaseHandler(getApplicationContext(), null, null, 1);
        db.addReport(id);
        db.close();
        Intent i = new Intent(getApplicationContext(), FollowingActivity.class);
        startActivityForResult(i, Constants.BACK_BUTTON);
    }

    /**
     * Setup a spinner that will hold the main categories taken from the services provided
     * by an open311 Interface.
     */
    public void setupCategorySpinner() {
        // create an arraylist that will contain different categories fetched from an open311 interface
        catagoryList = new ArrayList<String>();
        // add a default item for the spinner
        catagoryList.add(getResources().getString(R.string.kiesProblemen));
        catagorySpinner = (Spinner) findViewById(R.id.spinner2);
        catagoryAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, catagoryList) {
            @Override
            //pakt de positions van elements in catagoryList en disabled the element dat postion null staat zodat we het kunnen gebruiken als een hint.
            public boolean isEnabled(int position) {
                if (position == 0) {
                    return false;
                } else {
                    return true;
                }
            }
        };
        catagoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        catagorySpinner.setAdapter(catagoryAdapter);
    }

    /**
     * Setup a spinner that will hold the sub categories taken from the services provided
     * by an open311 Interface.
     */
    public void setupSubCategorySpinner() {
        // create an arraylist that will contain different sub categories fetched from an open311 interface
        subCategoryList = new ArrayList<String>();
        // add a default item for the spinner
        subCategoryList.add(getResources().getString(R.string.kiesSubProblemen));
        subCatagorySpinner = (Spinner) findViewById(R.id.spinnerSub);
        subCategoryAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, subCategoryList) {
            @Override
            //pakt de positions van elements in subCatagoryList en disabled the element dat postion null staat zodat we het kunnen gebruiken als een hint.
            public boolean isEnabled(int position) {
                if (position == 0) {
                    return false;
                } else {
                    return true;
                }
            }
        };
        subCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subCatagorySpinner.setAdapter(subCategoryAdapter);
    }

    /**
     * Fill the previously setup sub category spinner with sub categories that match the
     * main category's group name.
     * @param parent
     */
    public void fillSubCategorySpinner(AdapterView<?> parent) {
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
    }

    /**
     * Requests services from an open311 Interface that will be used to filter out main and
     * sub categories.
     */
    public void getServicesForSpinner() {
        // from here all the API requests will be handled
        final RequestManager reqManager = new RequestManager(this);
        // set callback for data passing
        reqManager.setOnServicesReadyCallb(this);
        // launch Retrofit callback and retrieve services asynchronously
        reqManager.getServices();
    }

    /**
     * Automatically fill in the personal info in the form fields if the user
     * choose to save his personal info.
     */
    public void fillPersonalInfo() {
        final DatabaseHandler db = new DatabaseHandler(getApplicationContext(), null, null, 1);
        User foundUser = db.getUser();
        Log.i("FOUND USER", foundUser.toString());
        emailEditText.setText(foundUser.getEmail());
        voornaamEditText.setText(foundUser.getFirstName());
        achternaamEditText.setText(foundUser.getLastName());
        db.close();
    }

    /**
     * Save the users personal info in the user's phone's local database.
     */
    public void saveUser() {
        if (onthoudCheckbox.isChecked() && !emailEditText.getText().toString().equals("")
                && !voornaamEditText.getText().toString().equals("") &&
                !achternaamEditText.getText().toString().equals("")) {

            final DatabaseHandler db = new DatabaseHandler(getApplicationContext(), null, null, 1);

            User user = new User();
            user.setLastName(achternaamEditText.getText().toString());
            user.setFirstName(voornaamEditText.getText().toString());
            user.setEmail(emailEditText.getText().toString());
            db.deleteUser();
            db.addUser(user);
            db.close();
        }
    }

    /**
     * Assign a longitude to a variable. Returns if the longitude was correctly assigned.
     * @return longitude of the location of a new service request chosen by the user.
     */
    public boolean assignLong() {
        // initializes a longtitude of the user's current location or a longtitude that
        // has been provided by the user
        boolean longAssigned = false;
        lon = 0.0;
        if (mapLocation != null) {
            lon = mapLocation.longitude;
            longAssigned = true;
        } else {
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.geenLocatie), Toast.LENGTH_SHORT).show();
        }
        return longAssigned;
    }

    /**
     * Assign a latitude to a variable. Returns if the longitude was correctly assigned.
     * @return latitude of the location of a new service request chosen by the user.
     */
    public boolean assignLat() {
        // initializes a latitude of the user's current location or a latitude that
        // has been provided by the user
        boolean latAssigned = false;
        lat = 0.0;
        if (mapLocation != null) {
            lat = mapLocation.latitude;
            latAssigned = true;
        } else {
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.geenLocatie), Toast.LENGTH_SHORT).show();
        }
        return latAssigned;
    }

    /**
     * Assign selected category and check if selected category is actually a category.
     * @return True if selected item in spinner is a sub category. Returns false otherwise.
     */
    public boolean getSelectedSubCat() {
        // initialize selected category and check if selected category is actually a category
        selectedItem = "";
        boolean itemSelected = false;

        if (!subCatagorySpinner.getSelectedItem()
                .equals(getResources().getString(R.string.kiesSubProblemen))) {
            selectedItem = subCatagorySpinner.getSelectedItem().toString();
            itemSelected = true;
        } else {
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.kiesSubCategory), Toast.LENGTH_SHORT).show();
        }
        return itemSelected;
    }

    /**
     * Assign a service code to a variable for the selected sub category.
     */
    public void assignServiceCode() {
        sc = null;
        if (serviceList != null) {
            for (Service s : serviceList) {
                if (s.getService_name().equals(selectedItem)) {
                    sc = s.getService_code();
                    Log.i("Service code: ", sc);
                }
            }
        }
    }

    /**
     *  Assigns the description that the user has provided.
     * @return Will return true if a description was given. Returns false otherwise.
     */
    public boolean assignDescription() {
        // initializes a description that the user has provided
        // to send with the post service request
        boolean descrAssigned = false;
        if (!beschrijvingEditText.getText().toString().equals("")) {
            descr = beschrijvingEditText.getText().toString();
            descrAssigned = true;
        } else {
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.geenBeschrijving), Toast.LENGTH_SHORT).show();
        }
        return descrAssigned;
    }

    /**
     * Assigns an email to a variable of an email was given by the user.
     */
    public void assignEmail() {
        // initializes an e-mailaddress that the user has provided
        // to send with the post service request
        email = null;
        if (!emailEditText.getText().toString().equals("")) {
            email = emailEditText.getText().toString();
        }
    }

    /**
     * Assigns a first name to a variable of a first name was given by the user.
     */
    public void assignFirstName() {
        // initializes a last name that the user has provided
        // to send with the post service request
        fName = null;
        if (!voornaamEditText.getText().toString().equals("")) {
            fName = voornaamEditText.getText().toString();

        }
    }

    /**
     * Assigns a last name to a variable of a last name was given by the user.
     */
    public void assignLastName() {
        // initializes a last name that the user has provided
        // to send with the post service request
        lName = null;
        if (!achternaamEditText.getText().toString().equals("")) {
            lName = achternaamEditText.getText().toString();
        }
    }

    /**
     * Setup the dialog that is shown after the user chooses to add a picture to the service request.
     * Executes different code based on the decision of the user, that is taking a picture or using a
     * picture that is stored on the user's phone.
     */
    public void setupPhotoBtnDialog() {
        // permissies aanvragen gaat async, kan niet wachten tot gebruiker antwoord geeft op de aanvraag
        // misschien later nog uitzoeken hoe dit moet.
        final CharSequence[] items = {getString(R.string.fotoMaken), getString(R.string.fotoKiezen)};
        builder = new android.app.AlertDialog.Builder(MeldingActivity.this);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals(getString(R.string.fotoMaken))) {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        Log.i("CAMERA", "ASKING PERMISSION");
                        reqCameraPermission();
                    }
                    if (ContextCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        try {
                            Intent makePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(makePhoto, Constants.FOTO_MAKEN);
                        } catch (Exception e) {
                            Log.e(Constants.PERMISSION, "camera not granted");
                            reqCameraPermission();
                        }
                    } else {
                        builder.show();
                    }

                } else if (items[item].equals(getString(R.string.fotoKiezen))) {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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
                            Log.e(Constants.PERMISSION, "storage not granted");
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

    /**
     * Create a new intent and adds the information of a location chosen by the user to the intent.
     * @return The intent that was created.
     */
    public Intent createIntentWithLocation() {
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        if (location != null) {
            intent.putExtra("long", location.longitude);
            intent.putExtra("lat", location.latitude);
            intent.putExtra("zoom", zoom);
        }

        if (marker) {
            intent.putExtra("marker", "true");
        }
        return intent;
    }

    /**
     * Create an image url that includes the file path to an image provided by the user.
     * This image url will added as a parameter in a POST Service Request.
     * @return The image url that was created from the image path of the image provided by the user.
     */
    public String createImgUrl() {
        imgUrl = null;
        FileInputStream imageInFile = null;
        if (imagePath != null) {
            try {
                File imgFile = new File(imagePath);
                imageInFile = new FileInputStream(imgFile);
                byte imageData[] = new byte[(int) imgFile.length()];
                imageInFile.read(imageData);

                // Converting Image byte array into Base64 String
                imgUrl = encodeImage(imageData);
                Log.i("imageString", imgUrl);

            } catch (FileNotFoundException e) {
                Log.e("Image not found", e.getMessage());
            } catch (IOException ioe) {
                Log.e("Exception img reading", ioe.getMessage());
            } finally {
                try {
                    imageInFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return imgUrl;
    }

    /**
     * Retrieve service requests from an open311 Interface that are similar to the service request
     * that the user is trying to post. Can be set to find service requests in a given radius.
     */
    public void getSimilarServiceRequests() {
        RequestManager reqManager = new RequestManager(MeldingActivity.this);
        reqManager.setOnServiceReqReadyCallb(MeldingActivity.this);
        reqManager.getServiceRequests(lat.toString(), lon.toString(), "open", "10", sc);
    }

    @Override
    public void servicesReady(List<Service> services) {
        serviceList = services;
        // update the catagoryList with main categories generated from the service list
        catagoryList = ServiceManager.genMainCategories(services, catagoryList);

        // let the adapter know that data has changed
        catagoryAdapter.notifyDataSetChanged();
    }

    @Override
    public void serviceRequestsReady(final ArrayList<ServiceRequest> serviceRequests) {
        if (!serviceRequests.isEmpty()) {

            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.activity_melding_dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            ListView meldingDialogListView = (ListView) dialog.findViewById(R.id.activity_melding_dialog_listView);
            MeldingDialogAdapter meldingDialogAdapter = new MeldingDialogAdapter(getApplicationContext(), serviceRequests);
            meldingDialogListView.setAdapter(meldingDialogAdapter);
            meldingDialogListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent myIntent = new Intent(view.getContext(), DetailedMeldingActivity.class);
                    ServiceRequest serviceRequest = serviceRequests.get(position);
                    myIntent.putExtra("serviceRequest", serviceRequest);
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
            postNotification();
        }
    }

    @Override
    public void serviceRequestPosted(ArrayList<PostServiceRequestResponse> pReqRespList) {
        if(!pReqRespList.isEmpty()) {
            // show the service code that was found in the respond as a toast
            for (PostServiceRequestResponse psrr : pReqRespList) {
                Log.i("Service response: ", psrr.getId());
                Toast.makeText(getApplicationContext(),
                        "service request is aangemaakt met id: " + psrr.getId(),
                        Toast.LENGTH_SHORT).show();

                insertReport(psrr.getId());
            }
        }
    }
}