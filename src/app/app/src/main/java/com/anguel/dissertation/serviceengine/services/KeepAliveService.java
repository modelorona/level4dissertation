package com.anguel.dissertation.serviceengine.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.anguel.dissertation.DataCollectionActivity;
import com.anguel.dissertation.R;

import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class KeepAliveService extends Service {

    private static boolean isServiceRunning = false;

    @Override
    public void onCreate() {
        super.onCreate();
        startServiceWithNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && Objects.requireNonNull(intent.getAction()).equals(getString(R.string.keep_alive_service))) {
            startServiceWithNotification();
        }
        else stopMyService();
        return START_STICKY;
    }

    private void startServiceWithNotification() {
        if (isServiceRunning) return;
        isServiceRunning = true;

        Intent notificationIntent = new Intent(getApplicationContext(), DataCollectionActivity.class);
        notificationIntent.setAction(getString(R.string.keep_alive_service));  // A string containing the action name
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent contentPendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, getString(R.string.persistence_notif_id))
                .setContentTitle(getResources().getString(R.string.app_name))
                .setTicker(getResources().getString(R.string.app_name))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentText(getResources().getString(R.string.alive_notif_text))
                .setSmallIcon(R.drawable.ic_death_star)
                .setContentIntent(contentPendingIntent)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setAutoCancel(false)
                .setGroup(getString(R.string.persistence_notif_group))
                .build();

        notification.flags = notification.flags | Notification.FLAG_NO_CLEAR;     // NO_CLEAR makes the notification stay when the user performs a "delete all" command
        startForeground(R.integer.keep_alive_notif_channel, notification);

    }

    // In case the service is deleted or crashes some how
    @Override
    public void onDestroy() {
        stopMyService();
        super.onDestroy();
    }

    private void stopMyService() {
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
