package com.example.justin.verbeterjegemeente;

import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.health.ServiceHealthStats;
import android.util.Log;
import android.widget.Toast;
import android.app.Service;

import static android.app.Service.START_STICKY;
import static android.os.Process.THREAD_PRIORITY_BACKGROUND;

/**
 * Created by Mika Krooswijk on 30-5-2017.
 */

public class UpdateService extends Service {
    private Looper serviceLooper;
    private ServiceHandler serviceHandler;
    private int count;


    public final class ServiceHandler extends Handler{
        public ServiceHandler(Looper looper){
            super(looper);
            count = 0;
        }

        public void handleMessage(Message message){
            Log.i("SERVICE", "5 sec have past");
            try{
               Thread.sleep(5000);
            }catch (InterruptedException e){
                e.printStackTrace();
                Log.i("THREAD","sleep failed");
                Thread.currentThread().interrupt();
            }
            count++;

            if(count > 50) {
                stopSelf(message.arg1);
            }else{
                Message m = new Message();
                handleMessage(m);
                Log.i("THREAD", "service comitted suicide");
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

}
