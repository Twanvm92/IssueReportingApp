package com.example.justin.verbeterjegemeente.Presentation.Stepper.StepCatagory;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.example.justin.verbeterjegemeente.R;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import butterknife.BindView;


public class StepCatagoryFragment extends Fragment implements Step {

    @BindView(R.id.StepCatagoryFragment_sp_MainCatagory)
    Spinner mainCatagorySpinner;

    @BindView(R.id.StepCatagoryFragment_sp_SubCatagory)
    Spinner subCatagorySpinner;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_step_catagory, container, false);

        //initialize your UI

        return v;
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

}
