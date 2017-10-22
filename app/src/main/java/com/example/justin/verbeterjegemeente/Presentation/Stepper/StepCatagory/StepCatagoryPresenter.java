package com.example.justin.verbeterjegemeente.Presentation.Stepper.StepCatagory;

import android.content.Context;

import com.example.justin.verbeterjegemeente.app.ServiceRequestApplication;
import com.example.justin.verbeterjegemeente.model.Service;
import com.example.justin.verbeterjegemeente.network.ServiceClient;

import javax.inject.Inject;

/**
 * Created by twanv on 20-10-2017.
 */

public class StepCatagoryPresenter implements IStepCatagoryPresenter {
    private StepCatagoryView view;
    @Inject
    ServiceClient api;

    public StepCatagoryPresenter(Context context) {
        ((ServiceRequestApplication)context).getAppComponent().inject(this);
    }

    @Override
    public void setView(StepCatagoryView view) {

    }

    @Override
    public void getMainCatagory() {

    }

    @Override
    public void getSubCatagory() {

    }
}
