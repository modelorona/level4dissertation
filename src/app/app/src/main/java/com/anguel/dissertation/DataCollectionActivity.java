package com.anguel.dissertation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatToggleButton;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ToggleButton;

import com.anguel.dissertation.MainActivity;
import com.anguel.dissertation.R;
import com.anguel.dissertation.datacollection.EventMonitoringService;
import com.anguel.dissertation.datacollection.KeepAliveService;
import com.anguel.dissertation.serviceengine.ServiceEngine;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class DataCollectionActivity extends AppCompatActivity {

    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_collection);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle("Data Collection");

        sharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        toggleDataCollectionDisplay();

        Intent keepAliveIntent = new Intent(getApplicationContext(), KeepAliveService.class);
        keepAliveIntent.setAction(getString(R.string.ACTION_KEEP_ALIVE));

        //        start the event monitoring service. started here so that it does not get killed
        Intent monitoringService = new Intent(getApplicationContext(), EventMonitoringService.class);
        monitoringService.setAction(getString(R.string.monitoring_service));

        ServiceEngine engine = ServiceEngine.getInstance(getApplicationContext());

        ToggleButton button = (ToggleButton) findViewById(R.id.toggleButton);
//        todo: fix bug where data collection is toggled on but is not actually happening
        button.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPref.edit();
//            it seems to get checked to on, and then calls this listener, which is why the isChecked is inverted :)
            if (!isChecked) {
//                stop the data collection
//                stopService(keepAliveIntent); // this seems to do the trick?
//                stopService(monitoringService);
                engine.stopServices(getApplicationContext());
                editor.putBoolean(getString(R.string.shprefprefix) + "_RECORDING_DATA", false);
            } else {
//                start the data collection
//                startService(keepAliveIntent);
//                startService(monitoringService);
                engine.startServices(getApplicationContext());
                editor.putBoolean(getString(R.string.shprefprefix) + "_RECORDING_DATA", true);
            }
            editor.apply();
        });

    }

    private void toggleDataCollectionDisplay() {
        boolean recordingData = sharedPref.getBoolean(getString(R.string.shprefprefix) + "_RECORDING_DATA", false);
        ToggleButton button = (ToggleButton) findViewById(R.id.toggleButton);
        button.setChecked(recordingData);
    }

    @Override
    public void onResume() {
        super.onResume();
        toggleDataCollectionDisplay();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent goBackIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(goBackIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}