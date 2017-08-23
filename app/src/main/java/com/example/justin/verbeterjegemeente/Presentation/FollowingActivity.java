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
import java.util.Locale;
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

                // remove this service request as unread if it was unread
                UpdateService.resetUnreadServiceRequest(serviceRequest);

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
        for (ServiceRequest sr : idList) {

            Log.i("FollowingActivity: ", sr.getDescription() + " : " + sr.getUpdatedDatetime());
        }
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

        ArrayList<ServiceRequest> orderedServiceRequests = orderServiceRequests(serviceRequests);

        if (!serviceRequests.isEmpty()) {
            for(ServiceRequest s : orderedServiceRequests) {
                srListFinal.add(s);
                Log.i("FollowActivity: ", "sr: " + s.getServiceRequestId());
            }
        }
        meldingAdapter.notifyDataSetChanged();
    }

    public ArrayList<ServiceRequest> orderServiceRequests(ArrayList<ServiceRequest> serviceRequests) {
        Comparator<ServiceRequest> comp = Collections.reverseOrder(new Comparator<ServiceRequest>() {
            DateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.ENGLISH);
            @Override
            public int compare(ServiceRequest o1, ServiceRequest o2) {
                Date firstDate;
                Date secondDate;
                try {
                    if (o1.getUpdatedDatetime() == null && o2.getUpdatedDatetime() == null) {
                        return 0;
                    }

                    if (o1.getUpdatedDatetime() != null) {
                        firstDate = f.parse(o1.getUpdatedDatetime());
                    } else {
                        return -1;
                    }

                    if (o2.getUpdatedDatetime() != null) {
                        secondDate = f.parse(o2.getUpdatedDatetime());
                    } else {
                        return +1;
                    }

                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }
                return firstDate.compareTo(secondDate);
            }
        });

        Collections.sort(serviceRequests,comp );

        return serviceRequests;
    }
}
