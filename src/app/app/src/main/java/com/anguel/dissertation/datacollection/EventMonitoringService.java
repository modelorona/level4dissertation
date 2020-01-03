package com.anguel.dissertation.datacollection;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.pranavpandey.android.dynamic.engine.model.DynamicAppInfo;
import com.pranavpandey.android.dynamic.engine.service.DynamicEngine;

import androidx.annotation.Nullable;

public class EventMonitoringService extends DynamicEngine {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onInitialize(boolean charging, boolean headset, boolean docked) {
        Log.d("event_monitoring", "init complete");
    }

    @Override
    public void onCallStateChange(boolean call) {

    }

    @Override
    public void onScreenStateChange(boolean screenOff) {

    }

    @Override
    public void onLockStateChange(boolean locked) {

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
}
