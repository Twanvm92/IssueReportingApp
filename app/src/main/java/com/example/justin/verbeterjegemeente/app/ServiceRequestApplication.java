package com.example.justin.verbeterjegemeente.app;
import android.app.Application;

import com.example.justin.verbeterjegemeente.dagger2.AppComponent;
import com.example.justin.verbeterjegemeente.dagger2.AppModule;
import com.example.justin.verbeterjegemeente.dagger2.DaggerAppComponent;
/**
 * Created by twanv on 16-10-2017.
 */

public class ServiceRequestApplication extends android.app.Application {

    private AppComponent appComponent;

    public AppComponent getAppComponent() {
        return appComponent;
    }

    protected AppComponent initDagger(ServiceRequestApplication application) {
        return DaggerAppComponent.builder()
                .appModule(new AppModule(application))
                .build();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = initDagger(this);
    }
}
