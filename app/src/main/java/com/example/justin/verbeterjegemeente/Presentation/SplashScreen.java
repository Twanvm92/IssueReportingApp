package com.example.justin.verbeterjegemeente.Presentation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.justin.verbeterjegemeente.Presentation.Stepper.StepperActivity;
import com.example.justin.verbeterjegemeente.R;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Will show a splashscreen when starting the app. After a set amount of time the user will be send to the starting activity.
 *
 * @author Justin Kannekens
 */
public class SplashScreen extends AppCompatActivity {

    /**
     * Duration of wait
     **/
    private static final long SPLASH_TIME = 500;

    /**
     * Called when the activity is first created.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                /** This method will be executed once the timer is over and will
                 start your app main activity**/
                loadLocale();

                Intent mainIntent = new Intent().setClass(SplashScreen.this, StepperActivity.class);
                startActivity(mainIntent);
                // Close this activity
                finish();
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, SPLASH_TIME);
    }

    /**
     * Comparing default language on phone and settings in the application,
     * if different; change the language to match the settings
     */
    public void loadLocale() {
        SharedPreferences prefs = getSharedPreferences("CommonPrefs",
                getApplicationContext().MODE_PRIVATE);
        String language = prefs.getString("Language", "");
        if (!language.equals("")) {
            changeLang(language);
        }
    }

    /**
     * Changing language to English unless user specifically chooses Dutch as only 2 languages are supported at the moment
     *
     * @param lang language user is requesting
     */
    public void changeLang(String lang) {
        Locale myLocale;
        if (lang.equalsIgnoreCase("")) {
            myLocale = new Locale("nl");
        } else if (lang.equalsIgnoreCase("en")) {
            myLocale = new Locale("en");
        } else {
            myLocale = new Locale("nl");
        }
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }
}
