package com.anguel.dissertation.workers;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.anguel.dissertation.R;
import com.anguel.dissertation.logger.Logger;
import com.anguel.dissertation.persistence.appcategory.AppCategory;
import com.anguel.dissertation.persistence.logevent.LogEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

public class SaveUsageStatsWorker extends Worker {

    private AtomicInteger nId = new AtomicInteger();

    public SaveUsageStatsWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
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


    @NonNull
    @Override
    public Result doWork() {
        Logger logger = new Logger();

        UsageStatsManager usm = (UsageStatsManager) getApplicationContext().getSystemService(Context.USAGE_STATS_SERVICE);

        long startTime = getInputData().getLong("sessionStart", -1L);
        long endTime = getInputData().getLong("sessionEnd", -1L);

        List<UsageStats> appList = Objects.requireNonNull(usm).queryUsageStats(UsageStatsManager.INTERVAL_BEST, startTime, endTime);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), getApplicationContext().getString(R.string.channel_id))
                .setSmallIcon(R.drawable.ic_done_black_24dp)
                .setContentTitle("Dissertation App Data Collection")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());

        if (appList != null && appList.size() == 0) {
            builder.setContentText("failure - no size");
            notificationManagerCompat.notify(nId.getAndIncrement(), builder.build());
            Log.d("Executed", "######### NO APP FOUND ##########");
            return Result.failure();
        }

        LogEvent logEvent = new LogEvent();
        logEvent.setSessionStart(getInputData().getLong("sessionStart", -1L));
        logEvent.setSessionEnd(getInputData().getLong("sessionEnd", -1L));

        if (Objects.requireNonNull(appList).size() > 0) {
            List<Map<String, String>> logEventData = new ArrayList<>();

            SortedMap<Long, UsageStats> sortedAppsWithStats = new TreeMap<>();

            for (UsageStats usageStats : appList) {
                sortedAppsWithStats.put(usageStats.getTotalTimeInForeground(), usageStats);
            }

            for (Map.Entry<Long, UsageStats> longUsageStatsEntry : sortedAppsWithStats.entrySet()) {
                UsageStats usageStats = longUsageStatsEntry.getValue();
//                skip if the total time used is 0
                if (usageStats.getTotalTimeInForeground() == 0L) {
                    continue;
                }
                Map<String, String> additionalDetails = getAdditionalAppDetails(usageStats.getPackageName());
                Map<String, String> appData = new HashMap<>();

                appData.put("name", additionalDetails.get("name"));
                appData.put("lastTimeUsed", String.valueOf(usageStats.getLastTimeUsed()));
                appData.put("totalTimeInForeground", String.valueOf(usageStats.getTotalTimeInForeground()));

                try {
                    boolean res = logger.saveAppCategory(getApplicationContext(), AppCategory.builder().category(additionalDetails.get("category")).appName(additionalDetails.get("name")).packageName(additionalDetails.get("packageName")).build());
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }

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

        try {
            boolean res = logger.saveAppStatistics(getApplicationContext(), logEvent);

            if (res) {
                builder.setContentText("success");
                notificationManagerCompat.notify(nId.getAndIncrement(), builder.build());
                return Result.success();
            }
            builder.setContentText("failure");
            notificationManagerCompat.notify(nId.getAndIncrement(), builder.build());
            return Result.failure();

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();

            builder.setContentText("failure - crash");
            notificationManagerCompat.notify(nId.getAndIncrement(), builder.build());
            return Result.failure();
        }
    }
}
