package com.example.justin.verbeterjegemeente.app;
import android.app.Activity;
import android.app.Application;

import com.example.justin.verbeterjegemeente.dagger2.AppInjector;
import com.example.justin.verbeterjegemeente.dagger2.DaggerAppComponent;


import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;

/**
 * Created by twanv on 16-10-2017.
 */

public class ServiceRequestApplication extends Application implements HasActivityInjector {

    @Inject
    DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;

//    private AppComponent appComponent;

//    public static AppComponent getAppComponent(Context context) {
//
//        return ((ServiceRequestApplication) context.getApplicationContext()).appComponent;
//    }

//    protected AppComponent initDagger(ServiceRequestApplication application) {
//        return DaggerAppComponent.builder()
//                .appModule(new AppModule(application))
//                .networkModule(new NetworkModule())
//                .presenterModule(new PresenterModule())
//                .build();
//    }

    @Override
    public void onCreate() {
        super.onCreate();

        AppInjector.init(this);

//        DaggerAppComponent
//                .builder()
//                .application(this)
//                .build()
//                .inject(this);
//        appComponent = initDagger(this);
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return dispatchingAndroidInjector;
    }
}
