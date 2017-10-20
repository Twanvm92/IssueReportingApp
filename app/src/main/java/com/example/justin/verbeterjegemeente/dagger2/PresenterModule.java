package com.example.justin.verbeterjegemeente.dagger2;

import android.content.Context;

import com.example.justin.verbeterjegemeente.Presentation.Stepper.StepCatagory.StepCatagoryPresenter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by twanv on 20-10-2017.
 */

@Module
public class PresenterModule {

    @Singleton
    @Provides
    StepCatagoryPresenter provideStepCatagoryPresenter(Context context) {
        return new StepCatagoryPresenter(context);
    }
}
