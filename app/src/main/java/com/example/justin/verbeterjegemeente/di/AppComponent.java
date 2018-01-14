package com.example.justin.verbeterjegemeente.di;

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
@Component(modules = {AppModule.class, NetworkModule.class, RoomModule.class, StepperActivityModule.class, AndroidSupportInjectionModule.class})
public interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);
        AppComponent build();
        Builder appModule(AppModule appModule);
        Builder roomModule(RoomModule roomModule);
        Builder networkModule(NetworkModule networkModule);
        Builder executorsModule(ExecutorsModule executorsModule);
    }

    void inject(ServiceRequestApplication app);

}
