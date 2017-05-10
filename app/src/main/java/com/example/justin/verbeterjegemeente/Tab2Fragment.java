package com.example.justin.verbeterjegemeente;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.justin.verbeterjegemeente.Database.DatabaseAccess;

import java.util.List;

/**
 * Created by Justin on 27-4-2017.
 */

public class Tab2Fragment extends Fragment {
    private static final String TAG = "Tab2Fragment";

    private Button btnTEST;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.tab2_fragment,container,false);
        btnTEST = (Button) view.findViewById(R.id.button2);
        btnTEST.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getActivity(), "TESTING BUTTON CLICK 2",Toast.LENGTH_SHORT).show();

                try{
                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getContext());
                    databaseAccess.open();
                    List<String> ids = databaseAccess.getIds();
                    databaseAccess.close();
                    Log.i("RESULT", "RESULTS: ");
                    Log.i("RESULT", ids.toString());
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

        return view;
    }
}
