package com.example.justin.verbeterjegemeente.ui.adapters;


import android.content.Context;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;

import com.example.justin.verbeterjegemeente.R;
import com.example.justin.verbeterjegemeente.ui.Stepper.StepCatagory.StepCatagoryFragment;
import com.example.justin.verbeterjegemeente.ui.Stepper.StepCreateServiceRequest.StepCreateServiceRequestFragment;
import com.example.justin.verbeterjegemeente.ui.Stepper.StepLocation.StepLocationFragment;
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
        StepViewModel.Builder builder = new StepViewModel.Builder(context);
        switch (position) {
            case 0:
                String catagory = context.getResources().getString(R.string.catagory);
                builder.setTitle(catagory);
                break;
            case 1:
                String location = context.getResources().getString(R.string.activityMain_item_gps);
                builder.setTitle(location);
                break;
            case 2:
                String request = context.getResources().getString(R.string.request);
                builder.setTitle(request);
                break;
            default:
                throw new IllegalArgumentException("Unsupported position: " + position);
        }
        return builder.create();
        //Override this method to set Step title for the Tabs, not necessary for other stepper types
    }
}