package com.example.justin.verbeterjegemeente;

import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.health.ServiceHealthStats;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.app.Service;

import com.example.justin.verbeterjegemeente.API.ConnectionChecker;
import com.example.justin.verbeterjegemeente.API.ServiceClient;
import com.example.justin.verbeterjegemeente.API.ServiceGenerator;
import com.example.justin.verbeterjegemeente.Database.DatabaseHanlder;
import com.example.justin.verbeterjegemeente.domain.ServiceRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Service.START_STICKY;
import static android.os.Process.THREAD_PRIORITY_BACKGROUND;

/**
 * Created by Mika Krooswijk on 30-5-2017.
 */

public class UpdateService extends Service {
    private Looper serviceLooper;
    private ServiceHandler serviceHandler;
    ServiceClient client;
    private int LONG_SLEEP_TIME, TEST_SLEEP_TIME;



    public final class ServiceHandler extends Handler{
        public ServiceHandler(Looper looper){
            super(looper);
            TEST_SLEEP_TIME = 1000;
            LONG_SLEEP_TIME = 600000;

        }

        public void handleMessage(Message message){
            //Log.i("SERVICE", "5 sec have past, count = " + count);

            DatabaseHanlder db = new DatabaseHanlder(getApplicationContext(), null, null, 1);
            final ArrayList<ServiceRequest> DatabaseList = db.getReports();
            db.close();





            try{
                if(ConnectionChecker.isConnected()){  //checking for internet acces.

                    for(ServiceRequest s: DatabaseList) {
                        client = ServiceGenerator.createService(ServiceClient.class);
                        Call<ServiceRequest> RequestResponseCall =
                                client.getServiceById(s.getServiceRequestId(), "1");
                        RequestResponseCall.enqueue(new retrofit2.Callback<ServiceRequest>() {
                            @Override
                            public void onResponse(Call<ServiceRequest> call, Response<ServiceRequest> response) {
                                if(response.isSuccessful()){
                                    ServiceRequest sr = response.body();

                                    if(sr != null) {

                                        String dateTime = sr.getRequestedDatetime();

                                        Log.i("API", sr.getRequestedDatetime());

                                        if (sr.getRequestedDatetime() != dateTime) {
                                            Log.i("CHECK", "changed date time = " + dateTime);
                                            notifyReportChanged(getString(R.string.reportUpdated) + " ",
                                                    getString(R.string.on) + " " + sr.getRequestedDatetime(), sr);
                                        } else {
                                            Log.i("CHECK", "not changed date time = " + dateTime);
                                        }
                                    }

                                } else { Log.i("response mis", "yup");}
                            }

                            @Override
                            public void onFailure(Call<ServiceRequest> call, Throwable t) {
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






            try{
               Thread.sleep(LONG_SLEEP_TIME);
            }catch (InterruptedException e){
                e.printStackTrace();
                Log.i("THREAD","sleep failed");
                Thread.currentThread().interrupt();
            }


            Message m = new Message();
            handleMessage(m);

        }

        }
    @Override
    public void onCreate(){
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);

        thread.start();

        serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = serviceHandler.obtainMessage();
        msg.arg1 = startId;
        serviceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }


    public void notifyReportChanged(String title, String content, ServiceRequest serviceRequest){
        Notification notification = new Notification();
        notification.makeNotification(getApplicationContext(), title, content, serviceRequest);
    }


}
