package com.anguel.dissertation.serviceengine;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.anguel.dissertation.R;
import com.anguel.dissertation.serviceengine.services.EventMonitoringService;
import com.anguel.dissertation.serviceengine.services.KeepAliveService;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


public class ServiceEngine {

    private static ServiceEngine instance;
    private List<Intent> services;
    private boolean servicesEnabled;


    private ServiceEngine(Context context) {
//        add the different kinds of services available
        services = new LinkedList<>(Arrays.asList(
//                new Intent(context.getApplicationContext(), EventMonitoringService.class).putExtra("name", "EventMonitoring"),
                new Intent(context.getApplicationContext(), KeepAliveService.class).setAction(context.getString(R.string.ACTION_KEEP_ALIVE))
        ));
    }

    public static ServiceEngine getInstance(Context context) {
        if (instance == null) {
            Log.d("service_engine", "creating instance");
            instance = new ServiceEngine(context);
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

    public boolean areServicesEnabled() {
        return servicesEnabled;
    }


}
