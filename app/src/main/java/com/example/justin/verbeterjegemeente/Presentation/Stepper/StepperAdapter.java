package com.example.justin.verbeterjegemeente.Presentation.Stepper;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;

import com.example.justin.verbeterjegemeente.Presentation.Stepper.StepCatagory.StepCatagoryFragment;
import com.example.justin.verbeterjegemeente.Presentation.Stepper.StepCreateServiceRequest.StepCreateServiceRequestFragment;
import com.example.justin.verbeterjegemeente.Presentation.Stepper.StepLocation.StepLocationFragment;
import com.example.justin.verbeterjegemeente.R;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter;
import com.stepstone.stepper.viewmodel.StepViewModel;

/**
 * Created by twanv on 17-10-2017.
 */

public class StepperAdapter extends AbstractFragmentStepAdapter {

    public StepperAdapter(FragmentManager fm, Context context) {
        super(fm, context);
    }

    @Override
    public Step createStep(int position) {
        switch (position) {
            case 0:
                return new StepCatagoryFragment();
            case 1:
                return new StepLocationFragment();
            case 2:
                return new StepCreateServiceRequestFragment();
            default:
                throw new IllegalArgumentException("Unsupported position: " + position);
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @NonNull
    @Override
    public StepViewModel getViewModel(@IntRange(from = 0) int position) {
        //Override this method to set Step title for the Tabs, not necessary for other stepper types
        return new StepViewModel.Builder(context)
                .setTitle("yello") //can be a CharSequence instead
                .create();
    }
}