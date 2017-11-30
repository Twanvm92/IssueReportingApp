package com.example.justin.verbeterjegemeente.dagger2;

import android.app.Application;

import com.example.justin.verbeterjegemeente.app.ServiceRequestApplication;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.support.AndroidSupportInjectionModule;

/**
 * Created by twanv on 16-10-2017.
 */

@Singleton
@Component(modules = {AppModule.class, StepperActivityModule.class, AndroidSupportInjectionModule.class})
public interface AppComponent {
//    void inject(StepCatagoryFragment target);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);
        AppComponent build();
    }

    void inject(ServiceRequestApplication app);

}
