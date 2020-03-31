package com.anguel.dissertation;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import io.sentry.Sentry;
import io.sentry.android.AndroidSentryClientFactory;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ToggleButton;

import com.anguel.dissertation.export.ExportService;
import com.anguel.dissertation.serviceengine.ServiceEngine;
import com.anguel.dissertation.settings.SettingsActivity;
import com.anguel.dissertation.utils.Utils;

import java.util.Objects;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Sentry.init(getString(R.string.dsn), new AndroidSentryClientFactory(this));
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setUpNotificationChannels();
        }

        Utils utils = Utils.getInstance();

        ToggleButton button = findViewById(R.id.startDataCollectionButton);

        button.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!utils.areAllPermissionsEnabled(getApplicationContext())) {
                button.setChecked(false);
                showPermissionsMissingDialog();
            } else {
                SharedPreferences.Editor editor = this.getSharedPreferences(
                        getString(R.string.preference_file_key), Context.MODE_PRIVATE).edit();
                editor.putBoolean(getString(R.string.shpref_prefix) + getString(R.string.pref_data_record), isChecked);
                editor.apply();
                toggleDataCollection(isRecordingData());
            }
        });

        //      start the quiz
        findViewById(R.id.start_test).setOnClickListener(v -> {
//            see if permissions are enabled or not. prevent taking the test unless they are on
            if (!utils.areAllPermissionsEnabled(getApplicationContext())) {
                showPermissionsMissingDialog();
            } else {
                Intent startTestIntent = new Intent(this, QuizActivity.class);
                startActivity(startTestIntent);
            }
        });

        findViewById(R.id.exportDataButton).setOnClickListener(v ->
                startService(new Intent(getApplicationContext(), ExportService.class).setAction(getString(R.string.upload_data_service))));

        toggleDataCollection(isRecordingData());


    }

    private boolean isRecordingData() {
        return getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
                .getBoolean(getString(R.string.shpref_prefix) + getString(R.string.pref_data_record), false);
    }

    private void toggleDataCollection(boolean recordingData) {
        ToggleButton button = findViewById(R.id.startDataCollectionButton);
        button.setChecked(recordingData);
        ServiceEngine engine = ServiceEngine.getInstance(getApplicationContext());
        if (recordingData) {
            engine.startServices(getApplicationContext());
        } else {
            engine.stopServices(getApplicationContext());
        }
    }

    private void showPermissionsMissingDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle(getString(R.string.permissions))
                .setMessage(getString(R.string.enable_permissions));
//                        .setCancelable(false);

        alert.setPositiveButton(getString(R.string.go_to_settings), ((dialog, which) -> {
            Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(settingsIntent);
        }));


        Dialog d = alert.create();
        d.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private NotificationChannel createChannel(CharSequence name, String description, int importance, String channelId) {
        NotificationChannel channel = new NotificationChannel(channelId, name, importance);
        channel.setDescription(description);
        return channel;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setUpNotificationChannels() {
        NotificationChannel persistentCollectionChannel = createChannel(
                getString(R.string.persistence_notif_name), getString(R.string.persistence_notif_desc),
                NotificationManager.IMPORTANCE_HIGH, getString(R.string.persistence_notif_id)
        );

        NotificationChannel onBootCollectionChannel = createChannel(
                getString(R.string.collection_on_boot_name), getString(R.string.collection_on_boot_desc),
                NotificationManager.IMPORTANCE_DEFAULT, getString(R.string.collection_on_boot_id)
        );

        NotificationChannel onCollectionChannel = createChannel(
                getString(R.string.on_collection_name), getString(R.string.on_collection_desc),
                NotificationManager.IMPORTANCE_LOW, getString(R.string.on_collection_id)
        );

        NotificationChannel onDataExportChannel = createChannel(
                getString(R.string.on_data_export_name), getString(R.string.on_data_export_desc),
                NotificationManager.IMPORTANCE_LOW, getString(R.string.on_data_export_id)
        );

        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
        Objects.requireNonNull(notificationManager).createNotificationChannel(persistentCollectionChannel);
        Objects.requireNonNull(notificationManager).createNotificationChannel(onBootCollectionChannel);
        Objects.requireNonNull(notificationManager).createNotificationChannel(onCollectionChannel);
        Objects.requireNonNull(notificationManager).createNotificationChannel(onDataExportChannel);
    }
}
