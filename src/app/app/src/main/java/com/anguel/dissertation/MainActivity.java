package com.anguel.dissertation;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import io.sentry.Sentry;
import io.sentry.android.AndroidSentryClientFactory;
import io.sentry.event.Breadcrumb;
import io.sentry.event.BreadcrumbBuilder;
import io.sentry.event.UserBuilder;

import android.os.PowerManager;
import android.view.Menu;
import android.view.MenuItem;

import com.anguel.dissertation.settings.SettingsActivity;
import com.jakewharton.threetenabp.AndroidThreeTen;

import java.util.Objects;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Sentry.init(getString(R.string.dsn), new AndroidSentryClientFactory(this));
        AndroidThreeTen.init(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        createNotificationChannel();

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

        findViewById(R.id.startDataCollectionButton).setOnClickListener(v -> {
            if (!hasUsageStatsPermission(this) || !Objects.requireNonNull(pm).isIgnoringBatteryOptimizations(getPackageName())) {
                showPermissionsMissingDialog();
            } else {
                Intent dataColIntent = new Intent(getApplicationContext(), DataCollectionActivity.class);
                startActivity(dataColIntent);
            }
        });

        //      start the quiz
        findViewById(R.id.start_test).setOnClickListener(v -> {
//            see if permissions are enabled or not. prevent taking the test unless they are fin
            if (!hasUsageStatsPermission(this) || !Objects.requireNonNull(pm).isIgnoringBatteryOptimizations(getPackageName())) {
                showPermissionsMissingDialog();
            } else {
                Intent startTestIntent = new Intent(this, QuizActivity.class);
                startActivity(startTestIntent);
            }
        });
    }

    private void showPermissionsMissingDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle("Please enable permissions")
                .setMessage("Please make sure to enable the Usage Statistics permisssion and then disable battery optimisations for the app to work as intended. You can find this " +
                        "in the settings.");
//                        .setCancelable(false);

        alert.setPositiveButton("Go to Settings", ((dialog, which) -> {
            Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(settingsIntent);
        }));


        Dialog d = alert.create();
//                d.setCanceledOnTouchOutside(false);
        d.show();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    boolean hasUsageStatsPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = Objects.requireNonNull(appOps).checkOpNoThrow("android:get_usage_stats",
                android.os.Process.myUid(), context.getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Collection Update";
            String description = "Shows a notification if data is collected or not";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(getString(R.string.channel_id), name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
            Objects.requireNonNull(notificationManager).createNotificationChannel(channel);
        }
    }
}
