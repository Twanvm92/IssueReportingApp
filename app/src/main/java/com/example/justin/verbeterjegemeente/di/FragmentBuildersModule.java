package com.example.justin.verbeterjegemeente.di;

import com.example.justin.verbeterjegemeente.ui.Stepper.StepCatagory.StepCatagoryFragment;
import com.example.justin.verbeterjegemeente.ui.Stepper.StepCreateServiceRequest.StepCreateServiceRequestFragment;
import com.example.justin.verbeterjegemeente.ui.Stepper.StepLocation.StepLocationFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by twanv on 26-11-2017.
 */

@Module
public abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract StepCatagoryFragment stepCatagoryFragment();

    @ContributesAndroidInjector
    abstract StepCreateServiceRequestFragment stepCreateServiceRequestFragment();

    @ContributesAndroidInjector
    abstract StepLocationFragment stepLocationFragment();

}
