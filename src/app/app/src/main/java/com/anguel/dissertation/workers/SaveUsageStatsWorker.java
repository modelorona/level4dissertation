package com.anguel.dissertation.workers;

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
import io.sentry.Sentry;
import io.sentry.event.BreadcrumbBuilder;

import com.anguel.dissertation.R;
import com.anguel.dissertation.persistence.DatabaseAPI;
import com.anguel.dissertation.persistence.entity.appcategory.AppCategory;
import com.anguel.dissertation.persistence.entity.logevent.LogEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

public class SaveUsageStatsWorker extends Worker {

    @SuppressWarnings("CanBeFinal")
//    private AtomicInteger nId = new AtomicInteger();

    public SaveUsageStatsWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    private Map<String, String> getAdditionalAppDetails(String packageName) {
        Map<String, String> details = new HashMap<>();
        details.put(getString(R.string.package_name), packageName);
        try {
            PackageManager packManager = getApplicationContext().getPackageManager();
            ApplicationInfo app = packManager.getApplicationInfo(packageName, 0);
            details.put(getString(R.string.name), packManager.getApplicationLabel(app).toString());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                damn getCategoryTitle throws a null, got to check that if it's null, the category is UNDEFINED.
                String category = getString(R.string.undefined);
                if (ApplicationInfo.getCategoryTitle(getApplicationContext(), app.category) != null) {
                    category = ApplicationInfo.getCategoryTitle(getApplicationContext(), app.category).toString();
                }
                details.put(getString(R.string.category), category);
            }
        } catch (Exception e) {
            Sentry.capture(e);
            Log.e("worker_appDetailsFail", Objects.requireNonNull(e.getLocalizedMessage()));
            e.printStackTrace();
            details.put(getString(R.string.name), String.format("%s_%s", getString(R.string.undefined), packageName)); // in this case we have only the package name. the app may have been recently uninstalled. add undefined in front as to differentiate it
            details.put(getString(R.string.category), getString(R.string.undefined));
        }

        return details;
    }

    private String getString(int id) {
        return getApplicationContext().getString(id);
    }


    @NonNull
    @Override
    public Result doWork() {
        DatabaseAPI databaseAPI = DatabaseAPI.getInstance();

        UsageStatsManager usm = (UsageStatsManager) getApplicationContext().getSystemService(Context.USAGE_STATS_SERVICE);

        long startTime = getInputData().getLong(getString(R.string.session_start), -1L);
        long endTime = getInputData().getLong(getString(R.string.session_end), -1L);
//todo: check this out https://developer.android.com/reference/android/app/usage/UsageStatsManager#queryAndAggregateUsageStats(long,%20long)
        List<UsageStats> appList = Objects.requireNonNull(usm).queryUsageStats(UsageStatsManager.INTERVAL_BEST, startTime, endTime);

//        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), getApplicationContext().getString(R.string.on_collection_id))
//                .setSmallIcon(R.drawable.ic_done_black_24dp)
//                .setContentTitle(getString(R.string.collection_occurred))
//                .setAutoCancel(true)
//                .setGroup(getApplicationContext().getString(R.string.on_collection_group))
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

//        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());

        if (appList != null && appList.size() == 0) {
            Sentry.getContext().recordBreadcrumb(
                    new BreadcrumbBuilder().setMessage(getString(R.string.sentry_fail_usagestats)).build()
            );
            Sentry.capture(getString(R.string.sentry_forgot_permissions));
//            builder.setContentText(getString(R.string.fail_no_size));
//            notificationManagerCompat.notify(nId.getAndIncrement(), builder.build());
            Log.d("Executed", "######### NO APP FOUND ##########");
            return Result.failure();
        }

        LogEvent logEvent = new LogEvent();
        logEvent.setSessionStart(getInputData().getLong(getString(R.string.session_start), -1L));
        logEvent.setSessionEnd(getInputData().getLong(getString(R.string.session_end), -1L));

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

                appData.put(getString(R.string.name), additionalDetails.get(getString(R.string.name)));
                appData.put(getString(R.string.last_time_used), String.valueOf(usageStats.getLastTimeUsed()));
                appData.put(getString(R.string.total_time_in_foreground), String.valueOf(usageStats.getTotalTimeInForeground()));

                try {
                    @SuppressWarnings("unused") boolean res = databaseAPI.saveAppCategory(getApplicationContext(), AppCategory.builder().category(additionalDetails.get(getString(R.string.category))).appName(additionalDetails.get(getString(R.string.name))).packageName(additionalDetails.get(getString(R.string.package_name))).build());
                } catch (ExecutionException | InterruptedException e) {
                    Sentry.capture(e);
                    e.printStackTrace();
                }

//                with android Q, more usage data is available, but we need to check to make sure the device actually supports it as the current min
//                target version is 24
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                    appData.put(getString(R.string.last_time_visible), String.valueOf(usageStats.getLastTimeVisible()));
                    appData.put(getString(R.string.last_time_foreground_service_used), String.valueOf(usageStats.getLastTimeForegroundServiceUsed()));
                    appData.put(getString(R.string.total_time_foreground_service_used), String.valueOf(usageStats.getTotalTimeForegroundServiceUsed()));
                    appData.put(getString(R.string.total_time_visible), String.valueOf(usageStats.getTotalTimeVisible()));
                }

                logEventData.add(appData);
            }

            logEvent.setData(logEventData);
        }

        try {
            boolean res = databaseAPI.saveLogData(getApplicationContext(), logEvent);

            if (res) {
//                builder.setContentText(getString(R.string.success));
//                notificationManagerCompat.notify(nId.getAndIncrement(), builder.build());
                return Result.success();
            }
//            builder.setContentText(getString(R.string.failure));
//            notificationManagerCompat.notify(nId.getAndIncrement(), builder.build());
            Sentry.capture(getString(R.string.unknown_fail_save));
            return Result.failure();

        } catch (ExecutionException | InterruptedException e) {
            Sentry.capture(e);
            e.printStackTrace();

//            builder.setContentText(getString(R.string.fail_crash));
//            notificationManagerCompat.notify(nId.getAndIncrement(), builder.build());
            return Result.failure();
        }
    }
}
