package com.example.justin.verbeterjegemeente.Presentation;

import android.content.Intent;
import android.os.Parcelable;
import android.support.design.internal.ParcelableSparseArray;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.justin.verbeterjegemeente.API.ServiceClient;
import com.example.justin.verbeterjegemeente.API.ServiceGenerator;
import com.example.justin.verbeterjegemeente.Adapters.MeldingAdapter;
import com.example.justin.verbeterjegemeente.Adapters.ServiceRequestAdapter;
import com.example.justin.verbeterjegemeente.Business.MeldingGenerator;
import com.example.justin.verbeterjegemeente.Database.DatabaseHanlder;
import com.example.justin.verbeterjegemeente.R;
import com.example.justin.verbeterjegemeente.domain.Melding;
import com.example.justin.verbeterjegemeente.domain.ServiceRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowingActivity extends AppCompatActivity {


    ServiceClient client;
    ArrayList<ServiceRequest> list;
    ListView meldingListView;
    private ArrayAdapter meldingAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);

        client = ServiceGenerator.createService(ServiceClient.class);

        // Filling the ArrayList with the service request id's from the database.
        final DatabaseHanlder db = new DatabaseHanlder(getApplicationContext(), null, null, 1 );
        ArrayList<String> idList = new ArrayList<>();
        idList = db.getReports();
        db.close();

        final ArrayList<ServiceRequest> srListFinal = new ArrayList<>();

        try{
            if(isConnected()){  //checking for internet acces.
                for(String s: idList) {
                    Call<ArrayList<ServiceRequest>> RequestResponseCall =
                            client.getServiceById(s);
                    RequestResponseCall.enqueue(new Callback<ArrayList<ServiceRequest>>() {
                        @Override
                        public void onResponse(Call<ArrayList<ServiceRequest>> call, Response<ArrayList<ServiceRequest>> response) {
                            if(response.isSuccessful()){
                                ArrayList<ServiceRequest> srList = response.body();
                                for (int i = 0; i < srList.size(); i++){
                                    srListFinal.add(srList.get(i));
                                };

                                if(meldingAdapter != null) {
                                    meldingAdapter.notifyDataSetChanged();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ArrayList<ServiceRequest>> call, Throwable t) {
                            Toast.makeText(getApplicationContext(),
                                    "Something went wrong while getting your requests",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        meldingListView = (ListView) findViewById(R.id.FollowingListView);
        meldingAdapter = new ServiceRequestAdapter(getApplicationContext(), srListFinal);
        meldingListView.setAdapter(meldingAdapter);
        meldingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(view.getContext(), DetailedMeldingActivity.class);
                ServiceRequest serviceRequest = srListFinal.get(position);
                i.putExtra("melding", serviceRequest);
                startActivity(i);
            }
        });


        meldingAdapter.notifyDataSetChanged();






    }


    // A methode for checking if the user has intrenet acces.
    public boolean isConnected() throws InterruptedException, IOException
    {
        String command = "ping -c 1 google.com";
        return (Runtime.getRuntime().exec (command).waitFor() == 0);
    }
}
