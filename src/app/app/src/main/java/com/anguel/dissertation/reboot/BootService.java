package com.anguel.dissertation.reboot;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.anguel.dissertation.DataCollectionActivity;
import com.anguel.dissertation.R;
import com.anguel.dissertation.serviceengine.ServiceEngine;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

public class BootService extends JobIntentService {

    private static final int JOB_ID = R.integer.BOOT_SERVICE;

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, BootService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.d("on_boot", "boot completed");

        SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        boolean recordingData = sharedPref.getBoolean(getString(R.string.shpref_prefix) + getString(R.string.pref_data_record), false);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), getApplicationContext().getString(R.string.collection_on_boot_id))
                .setSmallIcon(R.drawable.ic_done_black_24dp)
                .setGroup(getString(R.string.collection_on_boot_group))
                .setContentTitle(getString(R.string.boot_notif_title))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());

        if (recordingData) {
            ServiceEngine.getInstance(getApplicationContext()).startServices(getApplicationContext());
            builder.setContentText(getString(R.string.data_col_resumed));
        } else {
            builder.setContentText(getString(R.string.data_col_not_resumed));
            builder.setStyle(new NotificationCompat.BigTextStyle()
                              .bigText(getString(R.string.boot_data_disabled)));

            Intent dataCollectionIntent = new Intent(getApplicationContext(), DataCollectionActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
            stackBuilder.addNextIntentWithParentStack(dataCollectionIntent);
            PendingIntent result = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            
            builder.setContentIntent(result);
        }

        notificationManagerCompat.notify(1, builder.build());
    }
}
