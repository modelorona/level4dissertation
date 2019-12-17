package com.anguel.dissertation;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.anguel.dissertation.services.AlarmReceiver;
import com.anguel.dissertation.settings.SettingsActivity;
import com.jakewharton.threetenabp.AndroidThreeTen;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidThreeTen.init(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        createNotificationChannel();

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

        //      start the quiz
        findViewById(R.id.start_test).setOnClickListener(v -> {
//            see if permissions are enabled or not. prevent taking the test unless they are fin
            if (!hasUsageStatsPermission(this) || !Objects.requireNonNull(pm).isIgnoringBatteryOptimizations(getPackageName())) {
                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                alert.setTitle("Please enable permissions")
                        .setMessage("Please make sure to enable the Usage Statistics permisssion and then disable battery optimisations for the app to work as intended. You can find this " +
                                "in the settings.")
                        .setCancelable(false);

                alert.setPositiveButton("Go to Settings", ((dialog, which) -> {
                    Intent settingsIntent = new Intent(this, SettingsActivity.class);
                    startActivity(settingsIntent);
                }));

                Dialog d = alert.create();
                d.setCanceledOnTouchOutside(false);
                d.show();

            } else {
                Intent startTestIntent = new Intent(this, QuizActivity.class);
                startActivity(startTestIntent);
            }
        });

    }

    public void setUpBackgroundService() {
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("startTime", -1L);
        intent.putExtra("endTime", -1L);

        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

//        https://stackoverflow.com/a/25120314/4004697

        long midnight;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            midnight = LocalDateTime.now().until(LocalDate.now().plusDays(1).atStartOfDay(), ChronoUnit.MILLIS);
        } else { // use backported version only when necessary
            Log.d("BP", "using backported version");
            midnight = org.threeten.bp.LocalDateTime.now().until(org.threeten.bp.LocalDate.now().plusDays(1).atStartOfDay(), org.threeten.bp.temporal.ChronoUnit.MILLIS);
        }
        midnight += System.currentTimeMillis();
        Log.d("midnightTime", String.valueOf(System.currentTimeMillis() + midnight));

//        create the background task to start at midnight and then run every 4 hours.
        Objects.requireNonNull(alarmManager).setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 2000, AlarmManager.INTERVAL_HOUR * 4, pendingIntent);
    }

    public String getUserID() {
//        preferencemanager was deprecated
        SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String id = sharedPref.getString(getString(R.string.shprefprefix)+"_ID", "");
        if (id.equalsIgnoreCase("")) {
            UUID g = UUID.randomUUID();
            id = g.toString();
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(getString(R.string.shprefprefix)+"_ID", id);
            editor.apply();
        }
        return id;
    }

//    @SuppressLint("BatteryLife")
    public void disableBatteryOptimisation() {
        startActivityForResult(new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS), REQUEST_CODE+1);
    }

    @Override
    public void onResume() {
        super.onResume();
//        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(IntervalGatheringWorker.class).build();
//        WorkManager.getInstance(this.getApplicationContext()).enqueue(request);
    }


    void requestUsageStatsPermission() {
        if (!hasUsageStatsPermission(this)) {
            startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), REQUEST_CODE);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    boolean hasUsageStatsPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = Objects.requireNonNull(appOps).checkOpNoThrow("android:get_usage_stats",
                android.os.Process.myUid(), context.getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            disableBatteryOptimisation();
        } else if (requestCode == REQUEST_CODE+1) {
            setUpBackgroundService();
        }

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
            NotificationChannel channel = new NotificationChannel(getApplicationContext().getString(R.string.channel_id), name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
            Objects.requireNonNull(notificationManager).createNotificationChannel(channel);
        }
    }
}
