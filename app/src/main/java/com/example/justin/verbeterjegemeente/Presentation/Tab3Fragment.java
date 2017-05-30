package com.example.justin.verbeterjegemeente.Presentation;

import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.justin.verbeterjegemeente.Notification;
import com.example.justin.verbeterjegemeente.R;

import java.util.Locale;

/**
 * Created by Justin on 27-4-2017.
 */

public class Tab3Fragment extends Fragment {
    private static final String TAG = "Tab3Fragment";

    private Button btnNEDERLANDS, btnENGELS, btnMELDINGEN;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.tab3_fragment,container,false);
//        test buttons voor taal veranderen
        btnNEDERLANDS = (Button) view.findViewById(R.id.activityTab3_btn_buttonNederlands);
        btnNEDERLANDS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "NEDERLANDSE TAAL AANGEZET",Toast.LENGTH_SHORT).show();

                changeLang("nl");

                Notification notification = new Notification();
                notification.makeNotification(getContext(), "Title", "Text");
            }
        });

        btnENGELS = (Button) view.findViewById(R.id.activityTab3_btn_buttonEngels);
        btnENGELS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "ENGELSE TAAL AANGEZET",Toast.LENGTH_SHORT).show();

                changeLang("en");

            }
        });

        btnMELDINGEN = (Button) view.findViewById(R.id.activityTab3_btn_buttonMijnMelding);
        btnMELDINGEN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), FollowingActivity.class);
                startActivity(i);
            }
        });

        return view;
    }

    /**
     * Changing language to user's choice
     * @param lang language user is requesting
     */
    public void changeLang(String lang) {
        if (lang.equalsIgnoreCase(""))
            return;
        Locale myLocale = new Locale(lang);
        saveLocale(lang);
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getContext().getResources().updateConfiguration(config,getContext().getResources().getDisplayMetrics());
    }

    /**
     * Saving preferred language
     * @param lang language user is requesting
     */
    public void saveLocale(String lang) {
        String langPref = "Language";
        SharedPreferences prefs = getContext().getSharedPreferences("CommonPrefs",
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(langPref, lang);
        editor.commit();

//        refresh van huidige fragment werkt, backpress naar meldingen ook
//        andere activiteiten worden nog niet refresht
//        misschien is er een betere manier ipv elke activiteit apart op te vangen..
        android.support.v4.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }


}
