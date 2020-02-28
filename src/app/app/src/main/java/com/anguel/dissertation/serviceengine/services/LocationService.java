package com.anguel.dissertation.serviceengine.services;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;

import com.anguel.dissertation.R;
import com.anguel.dissertation.persistence.DatabaseAPI;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.sentry.Sentry;

public class LocationService extends Service {

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && Objects.requireNonNull(intent.getAction()).equals(getString(R.string.location_service))) {
            startService();
        }
        return START_STICKY;
    }

    private long getTime() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Instant.now().toEpochMilli();
        } return org.threeten.bp.Instant.now().toEpochMilli();
    }

    private void startService() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Log.d("location_service", "locationResult is null");
                }
                List<com.anguel.dissertation.persistence.entity.location.Location> locations = new ArrayList<>(Objects.requireNonNull(locationResult).getLocations().size());
                DatabaseAPI databaseAPI = DatabaseAPI.getInstance();

                long timeReceived = getTime();

                for (Location loc : Objects.requireNonNull(locationResult).getLocations()) {
                    com.anguel.dissertation.persistence.entity.location.Location.LocationBuilder location = com.anguel.dissertation.persistence.entity.location.Location.builder();
//                    accuracy MAY be useful, collect it for now
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        location.bearingAccuracy(loc.getBearingAccuracyDegrees()).vAccuracy(loc.getVerticalAccuracyMeters()).speedAccuracy(loc.getSpeedAccuracyMetersPerSecond());
                    }
                    location.altitude(loc.getAltitude())
                            .hAccuracy(loc.getAccuracy())
                            .bearing(loc.getBearing())
                            .latitude(loc.getLatitude())
                            .longitude(loc.getLongitude())
                            .speed(loc.getSpeed())
                            .systemTimestamp(timeReceived)
                            .locationTimestamp(loc.getTime())
                            .elapsedNanosLocation(loc.getElapsedRealtimeNanos())
                            .elapsedNanosSinceBoot(SystemClock.elapsedRealtimeNanos())
                            .provider(loc.getProvider());
                    locations.add(location.build());
                }

                try {
                    databaseAPI.saveMultipleLocations(getApplicationContext(), locations);
                } catch (Exception e) {
                    e.printStackTrace();
                    Sentry.capture(e);
                }
            }
        };

        fusedLocationProviderClient.requestLocationUpdates(createLocationRequest(), locationCallback, Looper.getMainLooper());
    }

    private LocationRequest createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000)
                .setFastestInterval(5000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    @Override
    public void onDestroy() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        stopSelf();
    }
}
