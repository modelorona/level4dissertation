package com.anguel.dissertation.workerservice;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.anguel.dissertation.logger.Logger;
import com.anguel.dissertation.persistence.LogEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
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
            return Result.retry();
        }

        LogEvent logEvent = new LogEvent();
        logEvent.setTimestamp(time);

        if (Objects.requireNonNull(appList).size() > 0) {
            List<Map<String, String>> logEventData = new ArrayList<>();

            SortedMap<Long, UsageStats> sortedAppsWithStats = new TreeMap<>();
//            Set<String> systemApps = getSystemApps();

            for (UsageStats usageStats : appList) {
                // try to filter out the system apps, although this can be bad depending on which apps are considered "system".
//                if (!systemApps.contains(usageStats.getPackageName())) {
                    sortedAppsWithStats.put(usageStats.getTotalTimeInForeground(), usageStats);
//                }
            }

            //            int current = sortedAppsWithStats.size()>10?10:sortedAppsWithStats.size();  // see if it will get the top 10 or less
            for (Map.Entry<Long, UsageStats> longUsageStatsEntry : sortedAppsWithStats.entrySet()) {
                UsageStats usageStats = longUsageStatsEntry.getValue();
//                skip if the total time used is 0
                if (usageStats.getTotalTimeInForeground() == 0) {
                    continue;
                }
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

    private Map<String, String> getAdditionalAppDetails(String packageName) {
        Map<String, String> details = new HashMap<>();
        try {
            PackageManager packManager = getApplicationContext().getPackageManager();
            ApplicationInfo app = packManager.getApplicationInfo(packageName, 0);
            details.put("name", packManager.getApplicationLabel(app).toString());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                details.put("category", ApplicationInfo.getCategoryTitle(getApplicationContext(), app.category).toString());
            }
        } catch (Exception e) {
            Log.e("worker_appDetailsFail", Objects.requireNonNull(e.getLocalizedMessage()));
            e.printStackTrace();
        }

        return details;
    }

//    private Set<String> getSystemApps() {
//        List<ApplicationInfo> applicationInfo = getApplicationContext().getPackageManager().getInstalledApplications(0);
//        Set<String> systemApps = new HashSet<>();
//        for (ApplicationInfo x: applicationInfo) {
//            if ((x.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
//                systemApps.add(x.packageName);
//            }
//        }
//
//        return systemApps;
//    }
}
