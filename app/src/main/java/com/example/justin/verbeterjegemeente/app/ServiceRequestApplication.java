package com.example.justin.verbeterjegemeente.app;
import android.app.Application;
import android.content.Context;

import com.example.justin.verbeterjegemeente.dagger2.AppComponent;
import com.example.justin.verbeterjegemeente.dagger2.AppModule;
import com.example.justin.verbeterjegemeente.dagger2.DaggerAppComponent;
import com.example.justin.verbeterjegemeente.dagger2.NetworkModule;
import com.example.justin.verbeterjegemeente.dagger2.PresenterModule;

/**
 * Created by twanv on 16-10-2017.
 */

public class ServiceRequestApplication extends Application {

    private AppComponent appComponent;

    public static AppComponent getAppComponent(Context context) {

        return ((ServiceRequestApplication) context.getApplicationContext()).appComponent;
    }

    protected AppComponent initDagger(ServiceRequestApplication application) {
        return DaggerAppComponent.builder()
                .appModule(new AppModule(application))
                .networkModule(new NetworkModule())
                .presenterModule(new PresenterModule())
                .build();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = initDagger(this);
    }
}
