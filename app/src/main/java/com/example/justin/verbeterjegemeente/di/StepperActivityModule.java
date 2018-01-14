package com.example.justin.verbeterjegemeente.di;

import com.example.justin.verbeterjegemeente.ui.Stepper.StepperActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by twanv on 26-11-2017.
 */

@Module
public abstract class StepperActivityModule {
    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract StepperActivity contributeMainActivity();
}
