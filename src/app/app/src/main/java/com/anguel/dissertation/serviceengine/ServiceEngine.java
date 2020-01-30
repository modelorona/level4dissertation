package com.anguel.dissertation.serviceengine;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.anguel.dissertation.R;
import com.anguel.dissertation.serviceengine.services.EventMonitoringService;
import com.anguel.dissertation.serviceengine.services.KeepAliveService;
import com.anguel.dissertation.serviceengine.services.LocationService;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


public class ServiceEngine {

    private static volatile ServiceEngine instance;
    @SuppressWarnings("CanBeFinal")
    private List<Intent> services;


    private ServiceEngine(Context context) {
//        add the different kinds of services available
        services = new LinkedList<>(Arrays.asList(
                new Intent(context.getApplicationContext(), EventMonitoringService.class).setAction(context.getString(R.string.monitor_service)),
                new Intent(context.getApplicationContext(), KeepAliveService.class).setAction(context.getString(R.string.keep_alive_service)),
                new Intent(context.getApplicationContext(), LocationService.class).setAction(context.getString(R.string.location_service))
        ));
    }

    public static ServiceEngine getInstance(Context context) {
        if (instance == null) {
            Log.d("service_engine", "creating instance");
            synchronized (ServiceEngine.class) {
                instance = new ServiceEngine(context);
            }
        }
        Log.d("service_engine", "reusing instance");
        return instance;
    }

    public void stopServices(Context context) {
        for (Intent service: services) {
            context.stopService(service);
        }
    }

    public void startServices(Context context) {
        for (Intent service: services) {
            context.startService(service);
        }
    }



}
