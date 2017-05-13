package com.example.justin.verbeterjegemeente;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.justin.verbeterjegemeente.Presentation.MainActivity;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by uno on 12/04/2016.
 */
public class SplashScreen extends AppCompatActivity {
    private static final long SPLASH_TIME=3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Intent mainIntent=new Intent().setClass(SplashScreen.this,MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        };
        Timer timer=new Timer();
        timer.schedule(task,SPLASH_TIME);
    }
}
