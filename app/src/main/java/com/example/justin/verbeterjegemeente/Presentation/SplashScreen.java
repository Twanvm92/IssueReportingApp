package com.example.justin.verbeterjegemeente.Presentation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.justin.verbeterjegemeente.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Deze klasse dient als splashscreen van de app de gebruiker zal eerst dit laadscherm zien voordat hij de app echt kan gebruiken.
 * @author Justin Kannekens
 */
public class SplashScreen extends AppCompatActivity {

    /** Duration of wait **/
    private static final long SPLASH_TIME=500;

    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                /** This method will be executed once the timer is over and will
                 start your app main activity**/
                Intent mainIntent=new Intent().setClass(SplashScreen.this, MainActivity.class);
                startActivity(mainIntent);
                // Close this activity
                finish();
            }
        };
        Timer timer=new Timer();
        timer.schedule(task,SPLASH_TIME);
    }
}
