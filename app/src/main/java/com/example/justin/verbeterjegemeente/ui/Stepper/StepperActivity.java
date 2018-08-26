package com.example.justin.verbeterjegemeente.ui.Stepper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.example.justin.verbeterjegemeente.R;
import com.example.justin.verbeterjegemeente.app.Constants;
import com.example.justin.verbeterjegemeente.data.DataManager;
import com.example.justin.verbeterjegemeente.ui.adapters.StepperAdapter;
import com.example.justin.verbeterjegemeente.ui.callbacks.OnNavigationBarListener;
import com.google.android.gms.maps.model.LatLng;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.StepperLayout;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

public class StepperActivity extends AppCompatActivity implements HasSupportFragmentInjector,
        DataManager, OnNavigationBarListener {

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentDispatchingAndroidInjector;

    private StepperLayout mStepperLayout;
    private static final String CURRENT_STEP_POSITION_KEY = "position";
    private final String TAG = "StepperActivity: ";
    private static final String DATA = "data";

    private String mData;
    private LatLng mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stepper);

        mStepperLayout = (StepperLayout) findViewById(R.id.stepperLayout);
        int startingStepPosition = savedInstanceState != null ? savedInstanceState.getInt(CURRENT_STEP_POSITION_KEY) : 0;
        mData = savedInstanceState != null ? savedInstanceState.getString(DATA) : null;
        mStepperLayout.setAdapter(new StepperAdapter(getSupportFragmentManager(), this), startingStepPosition);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(CURRENT_STEP_POSITION_KEY, mStepperLayout.getCurrentStepPosition());
        outState.putString(DATA, mData);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Fragment step = (Fragment) mStepperLayout.getAdapter().findStep(1);


        switch (requestCode) {
            case Constants.REQUEST_CHECK_SETTINGS:
                step.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentDispatchingAndroidInjector;
    }

    @Override
    public void onBackPressed() {
        final int currentStepPosition = mStepperLayout.getCurrentStepPosition();
        if (currentStepPosition > 0) {
            mStepperLayout.onBackClicked();
        } else {
            finish();
        }
    }

    @Override
    public void saveData(String data) {
        mData = data;
    }

    @Override
    public void saveData(LatLng location) {
        mLocation = location;
    }

    @Override
    public String getData() {
        return mData;
    }

    @Override
    public LatLng getLocation() {
        return mLocation;
    }


    @Override
    public void onChangeEndButtonsEnabled(boolean enabled) {
        mStepperLayout.setNextButtonVerificationFailed(enabled);
        mStepperLayout.setCompleteButtonVerificationFailed(enabled);
    }
}
