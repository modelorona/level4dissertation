package com.anguel.dissertation.serviceengine.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.anguel.dissertation.R;

import java.util.Objects;

public class LocationService extends Service {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && Objects.requireNonNull(intent.getAction()).equals(getString(R.string.location_service))) {
            startService();
        }
        else stopMyService();
        return START_STICKY;
    }

    private void startService() {

    }

    // In case the service is deleted or crashes some how
    @Override
    public void onDestroy() {
        stopMyService();
        super.onDestroy();
    }

    private void stopMyService() {
//        stopForeground(true);
        stopSelf();
    }
}
