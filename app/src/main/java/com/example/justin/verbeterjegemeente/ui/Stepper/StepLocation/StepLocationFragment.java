package com.example.justin.verbeterjegemeente.ui.Stepper.StepLocation;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.justin.verbeterjegemeente.R;
import com.example.justin.verbeterjegemeente.app.Constants;
import com.example.justin.verbeterjegemeente.data.DataManager;
import com.example.justin.verbeterjegemeente.data.network.ConnectionChecker;
import com.example.justin.verbeterjegemeente.databinding.FragmentStepLocationBinding;
import com.example.justin.verbeterjegemeente.di.Injectable;
import com.example.justin.verbeterjegemeente.service.model.Coordinates;
import com.example.justin.verbeterjegemeente.service.model.ServiceRequest;
import com.example.justin.verbeterjegemeente.ui.MeldingActivity;
import com.example.justin.verbeterjegemeente.ui.Tab1Fragment;
import com.example.justin.verbeterjegemeente.viewModel.ServiceRequestListViewModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import java.io.IOException;

import javax.inject.Inject;

import timber.log.Timber;

import static com.example.justin.verbeterjegemeente.app.Constants.REQUEST_CHECK_SETTINGS;


public class StepLocationFragment extends Fragment implements BlockingStep, Injectable, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener{

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private ServiceRequestListViewModel viewModel;
    private FragmentStepLocationBinding mBinding;
    private DataManager dataManager;
    private WebView wbMap;
    private GoogleApiClient mApiClient;
    private LocationRequest mLocationRequest;
    private Boolean FirstTime = true;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private LatLng locationSelected;
    private Snackbar zoomReadySnackbar;
    private Snackbar locationSelectedSnackbar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if(locationResult == null) {
                    return;
                }
                Location lastLocation = locationResult.getLastLocation();
                if (lastLocation != null) {
                    Timber.d("Location updated!");
                    zoomToLocation(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()));
                }
            }
        };

        createLocationRequest();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        buildGoogleApiClient();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_step_location, container,
                false);

        mBinding.aStepCatagoryFragmentFbtnGps.setOnClickListener(v -> promptLocationSettings());
        wbMap = mBinding.StepCatagoryFragmentWvMap;
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.of(this,
                viewModelFactory).get(ServiceRequestListViewModel.class);

        mBinding.setLifecycleOwner(this);
        observeViewModel(viewModel);

        mBinding.setViewModel(viewModel);

        // create Snackbar for future use when observing zoomReady LiveData
        View view = mBinding.getRoot().findViewById(R.id.StepLocationFragment_l_catagoryLayout);
        locationSelectedSnackbar = Snackbar.make(view, getString(R.string.clickOnMap), Snackbar.LENGTH_INDEFINITE);
        zoomReadySnackbar = Snackbar.make(view, getString(R.string.createOrFollowRequest), Snackbar.LENGTH_INDEFINITE);
        zoomReadySnackbar.setAction(R.string.chooseLocation, view1 -> {
            wbMap.loadUrl("javascript:Geomerk.Map.drawGeo('Point', callbackGeom);");
            locationSelectedSnackbar.show();
        });
        zoomReadySnackbar.setActionTextColor(getResources().getColor(R.color.green500));

    }

    private void observeViewModel(ServiceRequestListViewModel viewModel) {
        // Update the list when the data changes
        viewModel.getServiceRequestListObservable().observe(this, serviceRequests -> {
            if (serviceRequests != null) {
                if (serviceRequests.data != null) {
                    Gson gson = new Gson();
                    String serviceRequestJson = gson.toJson(serviceRequests.data);
                    wbMap.loadUrl("javascript:Geomerk.Map.addCluster(0.5, 46, 'http://openlayers.org/en/v3.7.0/examples/data/icon.png'," +
                            serviceRequestJson + ")");
                    Timber.d("Service request added to map");

                }

            }
        });
        viewModel.getMapLoaded().observe(this, mapLoaded -> {
            if (mapLoaded != null && mapLoaded) {
                Timber.d("Map is loaded");
                viewModel.setPageVisibility(true);
                viewModel.updateServiceRequests("open", "RB");
            }
        });
        viewModel.getShowSnackbar().observe(this, message -> {
            if (message != null) {
                View view = mBinding.getRoot().findViewById(R.id.StepLocationFragment_l_catagoryLayout);
                Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        });
        viewModel.getLocationSelected().observe(this, (LatLng location) -> {

            if (location != null) {
                Timber.d("Location selected");
                onMarkedLocation(location);
            }
        });
        viewModel.getZoomReadyLive().observe(this, (status) -> {
            if (status != null) {
                if (status) {
                    zoomReadySnackbar.show();
                } else {
                    zoomReadySnackbar.dismiss();
                }
            }
        });
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DataManager) {
            dataManager = (DataManager) context;
        } else {
            throw new IllegalStateException("Activity must implement DataManager interface!");
        }
    }

    /**
     * Build the Google API Client that will be used to access Google Play Services
     */
    protected synchronized void buildGoogleApiClient() {
        Timber.d("Building GoogleApiClient");

        mApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .enableAutoManage(getActivity(), this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // only do something when current fragment is visible to user
    }

    @Override
    public void onConnectionSuspended(int i) {
        // nothing has to happen here
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // gets managed by api client automatically
    }

    /**
     * Create a location request that can be used later on to
     * prompt location settings for the user.
     * @see Tab1Fragment#promptLocationSettings()
     */
    protected void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(30 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);
    }

    /**
     * Will try to retrieve users' last location after permission to access fine location
     * is granted. Will ask for user to give permission otherwise and onRequestPermissionResult
     * will be triggered.
     */
    public void getLastLocation() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener((location) -> {
                        if (location != null) {// location was successfully retrieved
                            Timber.d(location.toString());

                            LatLng locationCoords = new LatLng(location.getLatitude(),
                                    location.getLongitude());
                            zoomToLocation(locationCoords);
                        } else {
                            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                    mLocationCallback, null);
                        }
                    });
        } else {
            // request the user for permission.
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    Constants.MY_PERMISSIONS_LOCATION);
        }
    }

    /**
     * Prompt the location settings of the users' phone.
     * Will try to resolve the users' location settings if they are not satisfied.
     * Will try to get users' last location if location settings are satisfied
     */
    public void promptLocationSettings() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(getActivity());
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(getActivity(), locationSettingsResponse -> getLastLocation());

        task.addOnFailureListener(getActivity(), e -> {
            int statusCode = ((ApiException) e).getStatusCode();
            switch (statusCode) {
                case CommonStatusCodes.RESOLUTION_REQUIRED:
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialoggg.
                    try {
                        // Show the dialoggg by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(getActivity(),
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    // Location settings are not satisfied. However, we have no way
                    // to fix the settings so we won't show the dialoggg.
                    break;
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        // after updated location is available, make sure that location services does not keep updating locations
        LocationServices.FusedLocationApi.removeLocationUpdates(mApiClient, this);
        Timber.d("onLocationChangedFired");
        LatLng locationCoords = new LatLng(location.getLatitude(), location.getLongitude());

        zoomToLocation(locationCoords);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.MY_PERMISSIONS_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // received permission from user to access fine location
                    getLastLocation(); // location settings are already set so jump to getting last location
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION)) {
                        // user has declined permission to access fine location at least once before
                        new AlertDialog.Builder(getActivity()) // show user why getting access to fine location is important
                                .setMessage(getResources().getText(R.string.eFineLocationPermissionExplain))
                                .setCancelable(false)
                                .setPositiveButton(getResources().getText(R.string.eImSure),
                                        (dialog, id) -> {
                                            // user decided not to give permission
                                        })
                                .setNegativeButton(getResources().getText(R.string.eRetry), (dialog, which) -> {
                                    // user wants to try again
                                    promptLocationSettings();
                                })
                                .show();
                    } else { // user has not declined permission before
                        Timber.d("User did not give permission");
                    }

                }
            }
        }
    }

    // gets called after trying to resolve the users' location settings
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                if (resultCode == Activity.RESULT_OK) {
                    getLastLocation();
                } else {
                    new AlertDialog.Builder(this.getContext())
                            .setTitle(getString(R.string.eFindLocationTitle))
                            .setMessage(getString(R.string.eFindLocation))
                            .setPositiveButton(android.R.string.ok, (dialog, which) -> {

                            }).setIcon(android.R.drawable.ic_dialog_alert).show();

                }
                Timber.d("Gps aanvraag afgewezen");
        }
    }

    @Override
    public VerificationError verifyStep() {
        //return null if the user can go to the next step, create a new VerificationError instance otherwise
        if (locationSelected != null) {
            return null;
        } else {
            return new VerificationError(getString(R.string.selectLocation));
        }
    }

    @Override
    public void onSelected() {
        if (FirstTime) {

            if(!setupBredaMapInterface()) {
                showSnackbar();
            }

        } else {
            FirstTime = false;
        }
        Timber.d("service_code: " + dataManager.getData());

    }

    @Override
    public void onError(@NonNull VerificationError error) {
        //handle error inside of the fragment, e.g. show error on EditText
    }

    @Override
    public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {
        // stuff to do before going to next step like sending data async or to next step
        dataManager.saveData(locationSelected);
        Timber.d(String.format("On next clicked: %s, %s", locationSelected.latitude, locationSelected.longitude));
        callback.goToNextStep();
    }

    @Override
    public void onCompleteClicked(StepperLayout.OnCompleteClickedCallback callback) {
        callback.complete();
    }

    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {
        callback.goToPrevStep();
    }

    public boolean setupBredaMapInterface () {
        try {
            if (ConnectionChecker.isConnected()) {
                viewModel.setBredaMapInterface();
                if (mApiClient.isConnected()) {
                    promptLocationSettings();
                }
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    // TODO: 24-8-2017 add javadoc
    public void zoomToLocation(LatLng currentLatLng){
        String lat = String.valueOf(currentLatLng.latitude);
        String lng = String.valueOf(currentLatLng.longitude);

        // TODO Change this to databinding?
        wbMap.loadUrl("javascript:Geomerk.Map.zoomToLonLat(" + lng + "," + lat + ",20)");
    }

    public void showSnackbar () {
        View view = mBinding.getRoot().findViewById(R.id.StepLocationFragment_l_catagoryLayout);

        Toast.makeText(getContext(), getString(R.string.errLoadingMap), Toast.LENGTH_SHORT).show();
        Snackbar snackbar = Snackbar.make(view, getString(R.string.noConnection), Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.Retry, view1 -> {
            if (!setupBredaMapInterface()) {
                showSnackbar();
            }
        });
        snackbar.setActionTextColor(getResources().getColor(R.color.green500));
        snackbar.show();
    }

    private void onMarkedLocation(LatLng location) {
        //create a new custom dialog
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        View mView = getLayoutInflater().inflate(R.layout.activity_maps_dialog, null);

        mBuilder.setView(mView);

        final TextView tvLocation = mView.findViewById(R.id.activityMapsDialog_tv_location);
        tvLocation.setText(String.format("%s, %s", location.latitude, location.longitude));

        mBuilder.setTitle(getString(R.string.activityMain_item_gps));
        mBuilder.setPositiveButton(getString(R.string.eImSure), (dialog, which) -> {

            locationSelected = location;
            zoomReadySnackbar.setText(R.string.clickNext);
            zoomReadySnackbar.show();
        });
        mBuilder.setNegativeButton(getString(R.string.eRetry), (dialog, which) -> dialog.dismiss());

        mBuilder.setOnDismissListener(dialog -> wbMap.post(() -> wbMap.loadUrl("javascript:Geomerk.Map.drawGeo('Point', callbackGeom);")));

        final AlertDialog dialog = mBuilder.create();
        dialog.show();
    }

}
