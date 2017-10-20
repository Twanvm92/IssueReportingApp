package com.example.justin.verbeterjegemeente.dagger2;

import com.example.justin.verbeterjegemeente.Presentation.Stepper.StepCatagory.StepCatagoryFragment;
import com.example.justin.verbeterjegemeente.Presentation.Stepper.StepCatagory.StepCatagoryPresenter;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by twanv on 16-10-2017.
 */

@Singleton
@Component(modules = {AppModule.class, PresenterModule.class, NetworkModule.class})
public interface AppComponent {
    void inject(StepCatagoryPresenter target);
    void inject(StepCatagoryFragment target);

}
