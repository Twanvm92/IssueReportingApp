package com.example.justin.verbeterjegemeente.Presentation;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.justin.verbeterjegemeente.API.ConnectionChecker;
import com.example.justin.verbeterjegemeente.API.RequestManager;
import com.example.justin.verbeterjegemeente.API.ServiceClient;
import com.example.justin.verbeterjegemeente.API.ServiceGenerator;
import com.example.justin.verbeterjegemeente.Adapters.ServiceRequestAdapter;
import com.example.justin.verbeterjegemeente.Business.ServiceManager;
import com.example.justin.verbeterjegemeente.Constants;
import com.example.justin.verbeterjegemeente.Database.DatabaseHandler;
import com.example.justin.verbeterjegemeente.R;
import com.example.justin.verbeterjegemeente.UpdateService;
import com.example.justin.verbeterjegemeente.domain.ServiceRequest;

import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.function.Predicate;

import retrofit2.Call;
import retrofit2.Response;

public class FollowingActivity extends AppCompatActivity implements RequestManager.OnServiceRequestsReady {

    ListView meldingListView;
    private ArrayAdapter meldingAdapter;
    private ArrayList<ServiceRequest> srListFinal = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);

        meldingListView = (ListView) findViewById(R.id.activityFollowing_LV_FollowingListView);
        meldingAdapter = new ServiceRequestAdapter(getApplicationContext(), srListFinal);
        meldingListView.setAdapter(meldingAdapter);
        meldingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(view.getContext(), DetailedMeldingActivity.class);
                ServiceRequest serviceRequest = srListFinal.get(position);

                ArrayList<String> srIDList = UpdateService.getUnreadServiceRequests();

                // remove this service request as unread if it was unread
                UpdateService.resetUnreadServiceRequest(serviceRequest);

                Log.i("FollowingActivity: ", "unread srs: " + srIDList.size());

                i.putExtra("serviceRequest", serviceRequest);
                i.putExtra("ORIGIN", "FollowActivity");
                Log.i("Followingactivity: ", serviceRequest.getServiceRequestId() + " was clicked");
                FollowingActivity.this.startActivity(i);
            }
        });

        // Filling the ArrayList with the service request id's from the database.

        final DatabaseHandler db = new DatabaseHandler(getApplicationContext(), null, null, 1);
        ArrayList<ServiceRequest> idList;

        idList = db.getReports();
        Log.i("IDs in userdb", idList.size() + "");
        db.close();

        String sRequestIDQ = ServiceManager.genServiceRequestIDQ(idList);
        RequestManager requestManager = new RequestManager(this);
        requestManager.setOnServiceReqReadyCallb(this);
        requestManager.getServiceRequestsByID(sRequestIDQ);

        Button terugButton = (Button) findViewById(R.id.activityFollowing_btn_terugBTN_ID);
        terugButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(FollowingActivity.this.getApplicationContext(), MainActivity.class);
                FollowingActivity.this.startActivity(in);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // reset the notification counter
        UpdateService.resetNotificationCounter();

        // remove the notification on the users phone
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(Constants.NOTIFICATION_ID);


    }

    @Override
    public void onBackPressed() {
        Intent in = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(in);
    }

    @Override
    public void serviceRequestsReady(ArrayList<ServiceRequest> serviceRequests) {
        srListFinal.clear();

        Comparator<ServiceRequest> comp = Collections.reverseOrder(new Comparator<ServiceRequest>() {
            DateFormat f = new SimpleDateFormat("dd/MM/yyyy'T'HH:mm:ss'Z'");
            @Override
            public int compare(ServiceRequest o1, ServiceRequest o2) {
                try {
                    return f.parse(o1.getUpdatedDatetime()).compareTo(f.parse(o2.getUpdatedDatetime()));
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });

        Collections.sort(serviceRequests,comp );

        if (!serviceRequests.isEmpty()) {
            for(ServiceRequest s : serviceRequests) {
                srListFinal.add(s);
                Log.i("FollowActivity: ", "sr: " + s.getServiceRequestId());
            }
        }
        meldingAdapter.notifyDataSetChanged();
    }
}
