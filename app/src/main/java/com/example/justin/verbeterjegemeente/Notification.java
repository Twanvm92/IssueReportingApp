package com.example.justin.verbeterjegemeente;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.example.justin.verbeterjegemeente.Presentation.DetailedMeldingActivity;
import com.example.justin.verbeterjegemeente.Presentation.MainActivity;
import com.example.justin.verbeterjegemeente.domain.ServiceRequest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Mika Krooswijk on 23-5-2017.
 */

public class Notification {

    ArrayList<String> notificationIDs = new ArrayList<>();

    public void makeNotification(Context context, String title, String text, ServiceRequest serviceRequest){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(R.drawable.imageicon);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(text);

        Intent i = new Intent(context, DetailedMeldingActivity.class);
        i.putExtra("serviceRequest", (Serializable) serviceRequest);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);

        stackBuilder.addNextIntent(i);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent. FLAG_UPDATE_CURRENT
        );

        mBuilder.setContentIntent(resultPendingIntent);


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder.build();

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);





        mNotificationManager.notify(getRandom(), mBuilder.build());
    }



    public void clearNotifications(Context context){
        for(String id :notificationIDs) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(Integer.parseInt(id));
        }
    }

    public int getRandom(){
        Random rand = new Random();
        int  notificationID = rand.nextInt(50) + 1;
        notificationIDs.add(notificationID + "");
        Log.i("IDS", notificationIDs.toString());
        return notificationID;
    }

}
