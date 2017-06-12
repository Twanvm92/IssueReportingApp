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
 * This class is responsible for creating and pushing a notification when the UpdateService calls it
 * @author Mika Krooswijk
 */

public class Notification {

    public void makeNotification(Context context, String title, String text, ServiceRequest serviceRequest){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        // Sets the icon of the notification in the status bar.
        mBuilder.setSmallIcon(R.drawable.imageicon);
        // Set the Title of the notification in the status bar.
        mBuilder.setContentTitle(title);
        // Set the content of the notification.
        mBuilder.setContentText(text);

        // Sets the intent to open when the notification is clicked, using the serivceRequest in the parameters.
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


        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);





        mNotificationManager.notify(getRandom(), mBuilder.build());
    }


    // Creates a random number between 0 and 50 to give to the notification as the ID.
    public int getRandom(){
        Random rand = new Random();
        int  notificationID = rand.nextInt(50) + 1;
        return notificationID;
    }

}
