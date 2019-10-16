package com.anguel.dissertation.workerservice;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.anguel.dissertation.logger.Logger;
import com.anguel.dissertation.persistence.LogEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class IntervalGatheringWorker extends Worker {

    public IntervalGatheringWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Logger logger = new Logger();

        UsageStatsManager usm = (UsageStatsManager) getApplicationContext().getSystemService(Context.USAGE_STATS_SERVICE);
        long time = System.currentTimeMillis();
        List<UsageStats> appList = Objects.requireNonNull(usm).queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - TimeUnit.HOURS.toMillis(4), time);

        if (appList != null && appList.size() == 0) {
            Log.d("Executed", "######### NO APP FOUND ##########");
        }

        LogEvent logEvent = new LogEvent();
        logEvent.setTimestamp(time);

        if (Objects.requireNonNull(appList).size() > 0) {
            List<Map<String, String>> logEventData = new ArrayList<>();
            for (UsageStats usageStats : appList) {
                Map<String, String> appData = new HashMap<>();

                appData.put("name", appName(usageStats.getPackageName()));
                appData.put("lastTimeUsed", String.valueOf(usageStats.getLastTimeUsed()));
                appData.put("totalTimeInForeground", String.valueOf(usageStats.getTotalTimeInForeground()));


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
            boolean res = logger.saveData(getApplicationContext(), logEvent);
            if (res) {
                return Result.success();
            } return Result.failure();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return Result.failure();
        }

    }

    private String appName(String pack) {
        String name = null;

        try {
            PackageManager packManager = getApplicationContext().getPackageManager();
            ApplicationInfo app = getApplicationContext().getPackageManager().getApplicationInfo(pack, 0);
            name = packManager.getApplicationLabel(app).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return name;
    }
}
