package com.anguel.dissertation.serviceengine.services;

import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.anguel.dissertation.R;
import com.anguel.dissertation.workers.SaveCallTimesWorker;
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
    private long callStartTime = getTime();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onInitialize(boolean charging, boolean headset, boolean docked) {
        super.onInitialize(charging, headset, docked);
    }

    @Override
    public void onCallStateChange(boolean call) {
        super.onCallStateChange(call);
//        if the device is in a call, then the start time is recorded
//        else the end time is recorded and then save the start/end times in the database
        long endTime = getTime();
        if (!call) {
            Data workerData = new Data.Builder().putLong(getString(R.string.call_start), callStartTime).putLong(getString(R.string.call_end), endTime).build();
            OneTimeWorkRequest saveCallData = new OneTimeWorkRequest.Builder(SaveCallTimesWorker.class)
                    .setInputData(workerData)
                    .build();
            WorkManager.getInstance(this).enqueue(saveCallData);
            Log.d("event_monitor", "call worker enqueued");
        } else {
            callStartTime = getTime();
        }
    }

    @Override
    public void onScreenStateChange(boolean screenOff) {
        super.onScreenStateChange(screenOff);
    }

    @Override
    public void onLockStateChange(boolean locked) {
        super.onLockStateChange(locked);
//        if screen is unlocked, record start time
//        else, record the end time and create a background task to record the data. then reset the variables
        long endTime = getTime();
        if (locked) {
//            Log.d("event_monitor", String.format("session_end: %s", endTime));
            Data workerData = new Data.Builder().putLong(getString(R.string.session_start), startTime).putLong(getString(R.string.session_end), endTime).build();
            OneTimeWorkRequest saveSessionData = new OneTimeWorkRequest.Builder(SaveUsageStatsWorker.class)
                    .setInputData(workerData)
//                    .setInitialDelay(2, TimeUnit.HOURS)
                    .build();
            WorkManager.getInstance(this).enqueue(saveSessionData);
            Log.d("event_monitor", "session worker enqueued");
        } else {
            startTime = getTime();
//            Log.d("event_monitor", String.format("session_start: %s", startTime));
        }
    }

    @Override
    public void onHeadsetStateChange(boolean connected) {
        super.onHeadsetStateChange(connected);
    }

    @Override
    public void onChargingStateChange(boolean charging) {
        super.onChargingStateChange(charging);
    }

    @Override
    public void onDockStateChange(boolean docked) {
        super.onDockStateChange(docked);
    }

    @Override
    public void onAppChange(@Nullable DynamicAppInfo dynamicAppInfo) {
        super.onAppChange(dynamicAppInfo);
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
