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
    private int count;
    ServiceClient client;
    private int LONG_SLEEP_TIME, TEST_SLEEP_TIME;


    public final class ServiceHandler extends Handler{
        public ServiceHandler(Looper looper){
            super(looper);
            count = 0;
            TEST_SLEEP_TIME = 5000;
            LONG_SLEEP_TIME = 600000;
        }

        public void handleMessage(Message message){
            Log.i("SERVICE", "5 sec have past, count = " + count);

            DatabaseHanlder db = new DatabaseHanlder(getApplicationContext(), null, null, 1);
            final ArrayList<ServiceRequest> DatabaseList = db.getReports();
            db.close();



            try{
                    if(ConnectionChecker.isConnected()){  //checking for internet acces.
                        for(final ServiceRequest s: DatabaseList) {
                            int i = 0;
                            ServiceGenerator.changeApiBaseUrl("https://asiointi.hel.fi/palautews/rest/v1/");
                            while (i < 2){
                                client = ServiceGenerator.createService(ServiceClient.class);
                                Call<ArrayList<ServiceRequest>> RequestResponseCall =
                                        client.getServiceById(s.getServiceRequestId());
                                RequestResponseCall.enqueue(new retrofit2.Callback<ArrayList<ServiceRequest>>() {
                                    @Override
                                    public void onResponse(Call<ArrayList<ServiceRequest>> call, Response<ArrayList<ServiceRequest>> response) {
                                        if(response.isSuccessful()){
                                            ArrayList<ServiceRequest>responseSrList = response.body();
                                            for (int i = 0; i < responseSrList.size(); i++){

                                                String dateTime = responseSrList.get(i).getRequestedDatetime();

                                                if(count == 15){
                                                    dateTime = "different";
                                                }


                                                Log.i("API", responseSrList.get(i).getRequestedDatetime());

                                                if (responseSrList.get(i).getRequestedDatetime() != dateTime){
                                                    Log.i("CHECK", "changed date time = " + dateTime);
                                                    notifyReportChanged("Uw melding is veranderd",
                                                            "op " + responseSrList.get(i).getRequestedDatetime());
                                                }else{
                                                    Log.i("CHECK", "not changed date time = " + dateTime);
                                                }

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
                                ServiceGenerator.changeApiBaseUrl("http://dev.hel.fi/open311-test/v1/");
                                i++;
                            }
                        }
                        ServiceGenerator.changeApiBaseUrl("https://asiointi.hel.fi/palautews/rest/v1/");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }




            count++;
            try{
               Thread.sleep(LONG_SLEEP_TIME);
            }catch (InterruptedException e){
                e.printStackTrace();
                Log.i("THREAD","sleep failed");
                Thread.currentThread().interrupt();
            }


            if(count > 50) {
                stopSelf(message.arg1);
                Log.i("THREAD", "service comitted suicide");
            }else{
                Message m = new Message();
                handleMessage(m);
            }
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


    public void notifyReportChanged(String title, String content){
        Notification notification = new Notification();
        notification.makeNotification(getApplicationContext(), title, content);
    }

}
