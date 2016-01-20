package com.pexample.videocompressdemo;

/**
 * Created by priyasindkar on 20-01-2016.
 */
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class NotificationHelper {
    private Context mContext;
    private int NOTIFICATION_ID = 1;
    private Notification mNotification;
    private NotificationManager mNotificationManager;
    private PendingIntent mContentIntent;
    private CharSequence mContentTitle;
    NotificationCompat.Builder builder;

    public NotificationHelper(Context context)
    {
        mContext = context;
    }

    /**
     * Put the notification into the status bar
     */
    public void createNotification() {
        //get the notification manager
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        //create the notification
        int icon = android.R.drawable.stat_sys_download;
        CharSequence tickerText = "Compressing Video Started..."; //Initial text that appears in the status bar
        long when = System.currentTimeMillis();
        mNotification = new Notification(icon, tickerText, when);



        // Open NotificationView.java Activity
        Intent notificationIntent = new Intent();
        PendingIntent pIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, 0);


        //Create Notification using NotificationCompat.Builder
        builder = new NotificationCompat.Builder(mContext)
                // Set Icon
                .setSmallIcon(android.R.drawable.stat_sys_download)
                        // Set Ticker Message
                .setTicker("Compressing Video Started...")
                        // Set Title
                .setContentTitle("")
                        // Set Text
                .setContentText("Compressing Video...")
                        // Add an Action Button below Notification
               /* .addAction(R.drawable.ic_launcher, "Action Button", pIntent)*/
                        // Set PendingIntent into Notification
                .setContentIntent(pIntent)
                        // Dismiss Notification
                .setAutoCancel(true);




        // Create Notification Manager
        NotificationManager notificationmanager = (NotificationManager) mContext.getSystemService(mContext.NOTIFICATION_SERVICE);
        // Build Notification with Notification Manager
        notificationmanager.notify(0, builder.build());



        //create the content which is shown in the notification pulldown
        mContentTitle = "Compressing Video.."; //Full title of the notification in the pull down
        CharSequence contentText = "0% complete"; //Text of the notification in the pull down

        //you have to set a PendingIntent on a notification to tell the system what you want it to do when the notification is selected
        //I don't want to use this here so I'm just creating a blank one
       /* Intent notificationIntent1 = new Intent();
        mContentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent1, 0);*/
        //add the additional content and intent to the notification
        //mNotification.setLatestEventInfo(mContext, mContentTitle, contentText, mContentIntent);

        //make this notification appear in the 'Ongoing events' section
        mNotification.flags = Notification.FLAG_ONGOING_EVENT;

        //show the notification
       // mNotificationManager.notify(NOTIFICATION_ID, mNotification);


        notificationmanager.notify(NOTIFICATION_ID, builder.build());


    }

    /**
     * Receives progress updates from the background task and updates the status bar notification appropriately
     * @param percentageComplete
     */
    public void progressUpdate(int percentageComplete) {
        //build up the new status message
        CharSequence contentText = percentageComplete + "% complete";
        //publish it to the status bar
        //mNotification.setLatestEventInfo(mContext, mContentTitle, contentText, mContentIntent);
       // mNotificationManager.notify(NOTIFICATION_ID, mNotification);
    }

    /**
     * called when the background task is complete, this removes the notification from the status bar.
     * We could also use this to add a new ‘task complete’ notification
     */
    public void completed()    {
        //remove the notification from the status bar
        builder.setTicker("Video Compression Completed");
        builder.setContentTitle("Video Compress Complete");
        builder.setContentText("");
        builder.setSmallIcon(android.R.drawable.stat_sys_download_done);
        mNotificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
