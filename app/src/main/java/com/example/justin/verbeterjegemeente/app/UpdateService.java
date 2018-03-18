//package com.example.justin.verbeterjegemeente.app;
//
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Handler;
//import android.os.HandlerThread;
//import android.os.IBinder;
//import android.os.Looper;
//import android.os.Message;
//import android.os.Process;
//import android.support.v4.app.NotificationCompat;
//import android.support.v4.app.TaskStackBuilder;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.example.justin.verbeterjegemeente.API.RequestManager;
//import com.example.justin.verbeterjegemeente.data.database.DatabaseHandler;
//import com.example.justin.verbeterjegemeente.ui.FollowingActivity;
//import com.example.justin.verbeterjegemeente.R;
//import com.example.justin.verbeterjegemeente.service.model.ServiceRequest;
//
//import java.lang.ref.WeakReference;
//import java.util.ArrayList;
//
///**
// * This class is a Service that runs in the background even if the app is closed.
// * Every 15 minutes it checks the timestamps stores in the database with the timestamps it gets from the server.
// * If they are different is creates a notification.
// * @author Mika Krooswijk
// */
//
//public class UpdateService extends Service {
//    private ServiceHandler serviceHandler;
//    private static int count = 0;
//    private static ArrayList<String> srIDList = new ArrayList<>();
//
//    private static class ServiceHandler extends Handler implements RequestManager.OnServiceRequestsReady {
//        // A weak reference to the enclosing context
//        private final WeakReference<UpdateService> mContext;
//        private int LONG_SLEEP_TIME;
//        ArrayList<ServiceRequest> databaseList;
//
//        private final UpdateService context;
//
//        ServiceHandler(Looper looper, UpdateService context){
//            super(looper);
//            mContext = new WeakReference<UpdateService>(context);
//            this.context = mContext.get();
//            // Setting a sleep time for the th
//            // read, 10 minutes
////            LONG_SLEEP_TIME = 600000;
//            LONG_SLEEP_TIME = 100000;
//
//            // TODO: 11-8-2017 remove after testing notification update
//        }
//
//        public void handleMessage(Message message){
//            // Connecting to the database and getting a list of serviceRequests(id + timestamp).
//            DatabaseHandler db = new DatabaseHandler(context, null, null, 1);
//            databaseList = db.getReports();
//            db.close();
//
//            String sRequestIDQ = ServiceRequest.genServiceRequestIDQ(databaseList);
//            RequestManager requestManager = new RequestManager(context);
//            requestManager.setOnServiceReqReadyCallb(this);
//            requestManager.getServiceRequestsByID(sRequestIDQ);
//
//            try{
//               Thread.sleep(LONG_SLEEP_TIME);
//            }catch (InterruptedException e){
//                e.printStackTrace();
//                Log.i("THREAD","sleep failed");
//                Thread.currentThread().interrupt();
//            }
//
//            // recursice call to keep the service checking for updates
//            Log.i("SERVICE", "checking");
//            Message m = new Message();
//            handleMessage(m);
//
//        }
//
//        @Override
//        public void serviceRequestsReady(ArrayList<ServiceRequest> serviceRequests) {
//
//            for (int i = 0; i < serviceRequests.size(); i++) {
//                String apiDateTime = serviceRequests.get(i).getUpdatedDatetime();
//                String localdbDateTime = databaseList.get(i).getUpdatedDatetime();
//
//                Log.i("UpdateService: ", "apiDateTime: " + apiDateTime);
//                Log.i("UpdateService: ", "localdbDateTime: " + localdbDateTime);
//                Log.i("UpdateService: ", "beschrijving: " + serviceRequests.get(i).getDescription());
//
//                // If the timestamp from the server is different than that from the database
//                // a notification is made en pushed to the user.
//                if (!apiDateTime.equals(localdbDateTime)) {
//                    Log.i("UpdateService", "changed date time = " + apiDateTime);
//
//                    String nTitle = context.getResources().getString(R.string.app_name);
//                    String nContent = context.getResources().getString(R.string.reportUpdated);
//
//                    // after creating a new service request the localdatetime will be null
//                    // and the apidatetime will be the time the service request was posted.
//                    // we dont want to show the user a notification when the service request
//                    // gets created.
//
//                        context.notifyReportChanged(nTitle + " ",
//                                nContent, serviceRequests.get(i));
//
//
//                    // Connecting to the database and updating the local service request
//                    // with a new update time
//                    String localSrID = databaseList.get(i).getServiceRequestId();
//                    DatabaseHandler db = new DatabaseHandler(context, null, null, 1);
//                    db.updateReportUpdatetime(localSrID, apiDateTime);
//                    db.close();
//                }
//            }
//        }
//    }
//
//    @Override
//    public void onCreate(){
//        HandlerThread thread = new HandlerThread("ServiceStartArguments",
//                Process.THREAD_PRIORITY_BACKGROUND);
//
//        thread.start();
//
//        Looper serviceLooper = thread.getLooper();
//        serviceHandler = new ServiceHandler(serviceLooper, this);
//
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        // For each start request, send a message to start a job and deliver the
//        // start ID so we know which request we're stopping when we finish the job
//        Message msg = serviceHandler.obtainMessage();
//        msg.arg1 = startId;
//        serviceHandler.sendMessage(msg);
//
//        // If the service gets killed, after returning from here, restart
//        return START_STICKY;
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        // Binding is not provided in this service, so return null
//        return null;
//    }
//
//    @Override
//    public void onDestroy() {
//        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
//    }
//
//    /**
//     * Push a new notification to the user.
//     * @param title The title of the notification
//     * @param content The content of the notification
//     * @param serviceRequest The serviceRequest that has changed (for the DetailedActivity)
//     */
//    public void notifyReportChanged(String title, String content, ServiceRequest serviceRequest){
//
//        srIDList.add(serviceRequest.getServiceRequestId());
//
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
//        // Sets the icon of the notification in the status bar.
//        mBuilder.setSmallIcon(R.drawable.statusicon);
//        // Set the Title of the notification in the status bar.
//        mBuilder.setContentTitle(title);
//        // Set the content of the notification.
//        mBuilder.setContentText(content);
//        // cancel notification after user clicks on it
//        mBuilder.setAutoCancel(true);
//        // Set number of service requests
//        mBuilder.setNumber(++count);
//
//        // Sets the intent to open when the notification is clicked, using the serviceRequest in the parameters.
//        Intent i = new Intent(this, FollowingActivity.class);
////        i.putExtra("serviceRequest", serviceRequest);
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//
//        stackBuilder.addParentStack(FollowingActivity.class);
//
//        stackBuilder.addNextIntent(i);
//        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
//                0,
//                PendingIntent. FLAG_UPDATE_CURRENT
//        );
//
//        mBuilder.setContentIntent(resultPendingIntent);
//
//        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
//
//        mNotificationManager.notify(Constants.NOTIFICATION_ID, mBuilder.build());
//    }
//
//    public static void resetNotificationCounter() {
//        count = 0;
//    }
//
//    public static ArrayList<String> getUnreadServiceRequests() {
//        return srIDList;
//    }
//
//    /**
//     *
//     * @param sr
//     */
//    public static void resetUnreadServiceRequest(ServiceRequest sr){
//        for ( int i = 0;  i < srIDList.size(); i++){
//            String unreadSR = srIDList.get(i);
//            if(unreadSR.equals(sr.getServiceRequestId())) {
//                srIDList.remove(i);
//                i--;
//            }
//        }
//    }
//
//}
