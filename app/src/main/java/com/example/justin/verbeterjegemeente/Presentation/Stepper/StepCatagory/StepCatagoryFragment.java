package com.example.justin.verbeterjegemeente.Presentation.Stepper.StepCatagory;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.justin.verbeterjegemeente.Presentation.Stepper.StepperActivity;
import com.example.justin.verbeterjegemeente.R;
import com.example.justin.verbeterjegemeente.app.ServiceRequestApplication;
import com.example.justin.verbeterjegemeente.dagger2.AppComponent;
import com.example.justin.verbeterjegemeente.dagger2.DaggerAppComponent;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import javax.inject.Inject;


public class StepCatagoryFragment extends Fragment implements Step, StepCatagoryView {
    @Inject
    StepCatagoryPresenter presenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_step_catagory, container, false);

        //initialize your UI

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ServiceRequestApplication.getAppComponent(getActivity()).inject(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public VerificationError verifyStep() {
        //return null if the user can go to the next step, create a new VerificationError instance otherwise
        return null;
    }

    @Override
    public void onSelected() {
        //update UI when selected
    }

    @Override
    public void onError(@NonNull VerificationError error) {
        //handle error inside of the fragment, e.g. show error on EditText
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showErrorMessage() {

    }

    @Override
    public void showMainCatagories() {

    }

    @Override
    public void showSubCatagories() {

    }
}
