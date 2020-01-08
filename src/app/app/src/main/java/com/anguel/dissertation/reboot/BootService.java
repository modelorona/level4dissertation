package com.anguel.dissertation.reboot;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.anguel.dissertation.R;
import com.anguel.dissertation.datacollection.EventMonitoringService;
import com.anguel.dissertation.datacollection.KeepAliveService;

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
//        restart all of the collectors. ideally i should want something like collectors.start()
        Log.d("on_boot", "boot completed");

        SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        boolean recordingData = sharedPref.getBoolean(getString(R.string.shprefprefix) + "_RECORDING_DATA", false);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), getApplicationContext().getString(R.string.channel_id))
                .setSmallIcon(R.drawable.ic_done_black_24dp)
                .setContentTitle("Dissertation App Data Collection")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());

        if (recordingData) {
            Intent keepAliveIntent = new Intent(getApplicationContext(), KeepAliveService.class);
            keepAliveIntent.setAction(getString(R.string.ACTION_KEEP_ALIVE));

            //        start the event monitoring service. started here so that it does not get killed
            Intent monitoringService = new Intent(getApplicationContext(), EventMonitoringService.class);
            monitoringService.setAction(getString(R.string.monitoring_service));

            startService(keepAliveIntent);
            startService(monitoringService);

            builder.setContentText("Data collection has resumed");
        } else {
            builder.setContentText("Data collection has not resumed.");
        }

        notificationManagerCompat.notify(1, builder.build());
    }
}
