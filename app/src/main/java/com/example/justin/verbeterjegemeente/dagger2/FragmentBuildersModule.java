package com.example.justin.verbeterjegemeente.dagger2;

import com.example.justin.verbeterjegemeente.Presentation.Stepper.StepCatagory.StepCatagoryFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by twanv on 26-11-2017.
 */

@Module
public abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract StepCatagoryFragment contributeProjectFragment();

}
