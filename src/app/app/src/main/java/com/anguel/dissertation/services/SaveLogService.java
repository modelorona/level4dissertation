package com.anguel.dissertation.services;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.ListenableWorker;

import com.anguel.dissertation.R;
import com.anguel.dissertation.logger.Logger;
import com.anguel.dissertation.persistence.AppCategory;
import com.anguel.dissertation.persistence.LogEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SaveLogService extends JobIntentService {

    static final int JOB_ID = 300;
    private AtomicInteger nId = new AtomicInteger();

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, SaveLogService.class, JOB_ID, work);
    }

    private Map<String, String> getAdditionalAppDetails(String packageName) {
        Map<String, String> details = new HashMap<>();
        details.put("packageName", packageName);
        try {
            PackageManager packManager = getApplicationContext().getPackageManager();
            ApplicationInfo app = packManager.getApplicationInfo(packageName, 0);
            details.put("name", packManager.getApplicationLabel(app).toString());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                damn getCategoryTitle throws a null, got to check that if it's null, the category is UNDEFINED.
                String category = "UNDEFINED";
                if (ApplicationInfo.getCategoryTitle(getApplicationContext(), app.category) != null) {
                    category = ApplicationInfo.getCategoryTitle(getApplicationContext(), app.category).toString();
                }
                details.put("category", category);
            }
        } catch (Exception e) {
            Log.e("worker_appDetailsFail", Objects.requireNonNull(e.getLocalizedMessage()));
            e.printStackTrace();
            details.put("name", String.format("UNDEFINED_%s", packageName)); // in this case we have only the package name. the app may have been recently uninstalled. add undefined in front as to differentiate it
            details.put("category", "UNDEFINED");
        }

        return details;
    }


    @Override
    protected void onHandleWork(@NonNull Intent intent) {
//        copied from worker service
        Logger logger = new Logger();

        UsageStatsManager usm = (UsageStatsManager) getApplicationContext().getSystemService(Context.USAGE_STATS_SERVICE);
        long time = System.currentTimeMillis();
        List<UsageStats> appList = Objects.requireNonNull(usm).queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - TimeUnit.HOURS.toMillis(4), time);

        if (appList != null && appList.size() == 0) {
            Log.d("Executed", "######### NO APP FOUND ##########");
//            AlarmReceiver.setAlarm(false);
//            stopSelf();
            return;
        }

        LogEvent logEvent = new LogEvent();
        logEvent.setTimestamp(time);

        if (Objects.requireNonNull(appList).size() > 0) {
            List<Map<String, String>> logEventData = new ArrayList<>();

            SortedMap<Long, UsageStats> sortedAppsWithStats = new TreeMap<>();

            for (UsageStats usageStats : appList) {
                sortedAppsWithStats.put(usageStats.getTotalTimeInForeground(), usageStats);
            }

            for (Map.Entry<Long, UsageStats> longUsageStatsEntry : sortedAppsWithStats.entrySet()) {
                UsageStats usageStats = longUsageStatsEntry.getValue();
//                skip if the total time used is 0
                if (usageStats.getTotalTimeInForeground() == 0) {
                    continue;
                }
                Map<String, String> additionalDetails = getAdditionalAppDetails(usageStats.getPackageName());
                Map<String, String> appData = new HashMap<>();

                appData.put("name", additionalDetails.get("name"));
                appData.put("lastTimeUsed", String.valueOf(usageStats.getLastTimeUsed()));
                appData.put("totalTimeInForeground", String.valueOf(usageStats.getTotalTimeInForeground()));

//                todo: make this better. it's currently a quick fix, due to the fact that there's too much happening in my life, no time for a break even
//                if (additionalDetails.containsKey("category")) {
//                    if we have a category, then we save it to the database. otherwise, for now, do nothing
                try {
                    boolean res = logger.saveAppCategory(getApplicationContext(), AppCategory.builder().category(additionalDetails.get("category")).appName(additionalDetails.get("name")).packageName(additionalDetails.get("packageName")).build());
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
//                }

//                now this will be the interesting part. with android Q, more usage data is available, but we need to check to make sure the device actually supports it as the current min
//                target version is 24
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                    appData.put("lastTimeVisible", String.valueOf(usageStats.getLastTimeVisible()));
                    appData.put("lastTimeForegroundServiceUsed", String.valueOf(usageStats.getLastTimeForegroundServiceUsed()));
                    appData.put("totalTimeForegroundServiceUsed", String.valueOf(usageStats.getTotalTimeForegroundServiceUsed()));
                    appData.put("totalTimeVisible", String.valueOf(usageStats.getTotalTimeVisible()));
                }

                logEventData.add(appData);
            }

            logEvent.setData(logEventData);
        }

        // todo: fix the way notifications are shown, this is ugly and a quick hack
        try {
            boolean res = logger.saveAppStatistics(getApplicationContext(), logEvent);
            if (res) {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), getApplicationContext().getString(R.string.channel_id))
                        .setSmallIcon(R.drawable.ic_done_black_24dp)
                        .setContentTitle("Dissertation App Data Collection")
                        .setContentText("ISSA SUCCESS")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
                notificationManagerCompat.notify(nId.getAndIncrement(), builder.build());
//                AlarmReceiver.setAlarm(false);
//                stopSelf();
                return;
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), getApplicationContext().getString(R.string.channel_id))
                    .setSmallIcon(R.drawable.ic_done_black_24dp)
                    .setContentTitle("Dissertation App Data Collection")
                    .setContentText("ISSA FAILURE")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
            notificationManagerCompat.notify(nId.getAndIncrement(), builder.build());

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), getApplicationContext().getString(R.string.channel_id))
                    .setSmallIcon(R.drawable.ic_done_black_24dp)
                    .setContentTitle("Dissertation App Data Collection")
                    .setContentText("ISSA FAILURE")
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(e.getLocalizedMessage()))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
            notificationManagerCompat.notify(nId.getAndIncrement(), builder.build());
//            AlarmReceiver.setAlarm(false);
//            stopSelf();
        }
    }
}
