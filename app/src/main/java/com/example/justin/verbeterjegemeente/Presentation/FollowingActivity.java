package com.example.justin.verbeterjegemeente.Presentation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.justin.verbeterjegemeente.API.ConnectionChecker;
import com.example.justin.verbeterjegemeente.API.ServiceClient;
import com.example.justin.verbeterjegemeente.API.ServiceGenerator;
import com.example.justin.verbeterjegemeente.Adapters.ServiceRequestAdapter;
import com.example.justin.verbeterjegemeente.Database.DatabaseHandler;
import com.example.justin.verbeterjegemeente.R;
import com.example.justin.verbeterjegemeente.domain.ServiceRequest;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

public class FollowingActivity extends AppCompatActivity {


    ServiceClient client;
    ListView meldingListView;
    private ArrayAdapter meldingAdapter;
    private Button terugButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);


        // Filling the ArrayList with the service request id's from the database.

        final DatabaseHandler db = new DatabaseHandler(getApplicationContext(), null, null, 1);
        ArrayList<ServiceRequest> idList;

        idList = db.getReports();
        Log.i("IDs in userdb", idList.size() + "");
        db.close();

        final ArrayList<ServiceRequest> srListFinal = new ArrayList<>();


        try{
            if(ConnectionChecker.isConnected()){  //checking for internet acces.

                for(ServiceRequest s: idList) {
                    Log.i("Service request ids: ", s.getServiceRequestId());
                    client = ServiceGenerator.createService(ServiceClient.class);
                    Call<ArrayList<ServiceRequest>> RequestResponseCall =
                            client.getServiceById(s.getServiceRequestId(), "1");
                    RequestResponseCall.enqueue(new retrofit2.Callback<ArrayList<ServiceRequest>>() {
                        @Override
                        public void onResponse(Call<ArrayList<ServiceRequest>> call, Response<ArrayList<ServiceRequest>> response) {
                            if (response.isSuccessful()) {

                                for(ServiceRequest s : response.body()) {
                                    srListFinal.add(s);
                                }

                                if (meldingAdapter != null) {
                                    meldingAdapter.notifyDataSetChanged();
                                }
                            } else {
                                Log.i("response mis", "yup");
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
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            e.printStackTrace();
        }

        meldingListView = (ListView) findViewById(R.id.activityFollowing_LV_FollowingListView);
        meldingAdapter = new ServiceRequestAdapter(getApplicationContext(), srListFinal);
        meldingListView.setAdapter(meldingAdapter);
        meldingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(view.getContext(), DetailedMeldingActivity.class);
                ServiceRequest serviceRequest = srListFinal.get(position);
                i.putExtra("serviceRequest", (Serializable) serviceRequest);
                i.putExtra("ORIGIN", "FollowActivity");
                Log.i("REQUEST", serviceRequest.getStatus());
                startActivity(i);
            }
        });

        meldingAdapter.notifyDataSetChanged();

        terugButton = (Button) findViewById(R.id.activityFollowing_btn_terugBTN_ID);
        terugButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent in = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(in);
                }
        });
    }

    @Override
    public void onBackPressed() {
        Intent in = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(in);
    }

}
