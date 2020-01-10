package com.anguel.dissertation;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import io.sentry.Sentry;
import io.sentry.android.AndroidSentryClientFactory;

import android.os.PowerManager;
import android.view.Menu;
import android.view.MenuItem;

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

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

        Utils utils = new Utils();

        findViewById(R.id.startDataCollectionButton).setOnClickListener(v -> {
            if (!utils.hasUsageStatsPermission(getApplicationContext()) || !Objects.requireNonNull(pm).isIgnoringBatteryOptimizations(getPackageName())) {
                showPermissionsMissingDialog();
            } else {
                Intent dataColIntent = new Intent(getApplicationContext(), DataCollectionActivity.class);
                startActivity(dataColIntent);
            }
        });

        //      start the quiz
        findViewById(R.id.start_test).setOnClickListener(v -> {
//            see if permissions are enabled or not. prevent taking the test unless they are fin
            if (!utils.hasUsageStatsPermission(getApplicationContext()) || !Objects.requireNonNull(pm).isIgnoringBatteryOptimizations(getPackageName())) {
                showPermissionsMissingDialog();
            } else {
                Intent startTestIntent = new Intent(this, QuizActivity.class);
                startActivity(startTestIntent);
            }
        });
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

        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
        Objects.requireNonNull(notificationManager).createNotificationChannel(persistentCollectionChannel);
        Objects.requireNonNull(notificationManager).createNotificationChannel(onBootCollectionChannel);
        Objects.requireNonNull(notificationManager).createNotificationChannel(onCollectionChannel);
    }
}
