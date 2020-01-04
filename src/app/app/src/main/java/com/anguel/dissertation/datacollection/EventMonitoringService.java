package com.anguel.dissertation.datacollection;

import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.anguel.dissertation.workers.SaveUsageStatsWorker;
import com.pranavpandey.android.dynamic.engine.model.DynamicAppInfo;
import com.pranavpandey.android.dynamic.engine.service.DynamicEngine;

import java.time.Instant;

import androidx.annotation.Nullable;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class EventMonitoringService extends DynamicEngine {

    private long startTime = getTime();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onInitialize(boolean charging, boolean headset, boolean docked) {

    }

    @Override
    public void onCallStateChange(boolean call) {

    }

    @Override
    public void onScreenStateChange(boolean screenOff) {

    }

    @Override
    public void onLockStateChange(boolean locked) {
//        if screen is unlocked, record start time
//        else, record the end time and create a background task to record the data. then reset the variables
        long endTime = getTime();
        if (locked) {
            Log.d("event_monitor", String.format("session_end: %s", endTime));
            Data workerData = new Data.Builder().putLong("sessionStart", startTime).putLong("sessionEnd", endTime).build();
            OneTimeWorkRequest saveSessionData = new OneTimeWorkRequest.Builder(SaveUsageStatsWorker.class)
                    .setInputData(workerData)
//                    .setInitialDelay(2, TimeUnit.HOURS)
                    .build();
            WorkManager.getInstance(this).enqueue(saveSessionData);
        } else {
            startTime = getTime();
            Log.d("event_monitor", String.format("session_start: %s", startTime));
        }
    }

    @Override
    public void onHeadsetStateChange(boolean connected) {

    }

    @Override
    public void onChargingStateChange(boolean charging) {

    }

    @Override
    public void onDockStateChange(boolean docked) {

    }

    @Override
    public void onAppChange(@Nullable DynamicAppInfo dynamicAppInfo) {

    }

    @Override
    public void onPackageUpdated(@Nullable DynamicAppInfo dynamicAppInfo, boolean newPackage) {

    }

    @Override
    public void onPackageRemoved(@Nullable String packageName) {

    }

    private long getTime() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Instant.now().toEpochMilli();
        } return org.threeten.bp.Instant.now().toEpochMilli();
    }
}
