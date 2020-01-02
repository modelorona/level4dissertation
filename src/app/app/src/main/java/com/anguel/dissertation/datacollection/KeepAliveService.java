package com.anguel.dissertation.datacollection;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.anguel.dissertation.R;

import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class KeepAliveService extends Service {

    public static boolean isServiceRunning = false;
    private static PhoneUnlockedReceiver receiver;

    @Override
    public void onCreate() {
        super.onCreate();
        startServiceWithNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && Objects.requireNonNull(intent.getAction()).equals(getString(R.string.ACTION_KEEP_ALIVE))) {
            startServiceWithNotification();
        }
        else stopMyService();
        return START_STICKY;
    }

    void startServiceWithNotification() {
        if (isServiceRunning) return;
        isServiceRunning = true;

        Intent notificationIntent = new Intent(getApplicationContext(), DataCollectionActivity.class);
        notificationIntent.setAction(getString(R.string.ACTION_KEEP_ALIVE));  // A string containing the action name
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent contentPendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, getString(R.string.channel_id))
                .setContentTitle(getResources().getString(R.string.app_name))
                .setTicker(getResources().getString(R.string.app_name))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentText(getResources().getString(R.string.alive_notif_text))
                .setSmallIcon(R.drawable.ic_assignment_turned_in_black_24dp)
                .setContentIntent(contentPendingIntent)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setAutoCancel(false)
                .build();

        notification.flags = notification.flags | Notification.FLAG_NO_CLEAR;     // NO_CLEAR makes the notification stay when the user performs a "delete all" command
        startForeground(Integer.parseInt(getString(R.string.keep_alive_notif_channel)), notification);

//        set up the receiver to receive all the required broadcasts
        receiver = new PhoneUnlockedReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_BOOT_COMPLETED);
        registerReceiver(receiver, filter);
    }

    // In case the service is deleted or crashes some how
    @Override
    public void onDestroy() {
        stopMyService();
        super.onDestroy();
    }

    void stopMyService() {
        unregisterReceiver(receiver);
        stopForeground(true);
        stopSelf();
        isServiceRunning = false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
