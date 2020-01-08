package com.anguel.dissertation.reboot;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.anguel.dissertation.R;
import com.anguel.dissertation.serviceengine.ServiceEngine;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class BootService extends JobIntentService {

    public static final int JOB_ID = R.integer.BOOT_SERVICE;

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, BootService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.d("on_boot", "boot completed");

        SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        boolean recordingData = sharedPref.getBoolean(getString(R.string.shpref_prefix) + "_RECORDING_DATA", false);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), getApplicationContext().getString(R.string.channel_id))
                .setSmallIcon(R.drawable.ic_done_black_24dp)
                .setContentTitle(getString(R.string.boot_notif_title))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());

        if (recordingData) {
            ServiceEngine.getInstance(getApplicationContext()).startServices(getApplicationContext());
            builder.setContentText("Data collection has resumed");
        } else {
            builder.setContentText("Data collection has not resumed.");
            builder.setStyle(new NotificationCompat.BigTextStyle()
                              .bigText(getString(R.string.boot_data_disabled)));
        }

        notificationManagerCompat.notify(1, builder.build());
    }
}
