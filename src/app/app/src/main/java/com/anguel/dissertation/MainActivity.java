package com.anguel.dissertation;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;

import com.anguel.dissertation.workerservice.IntervalGatheringWorker;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        requestUsageStatsPermission();

        createNotificationChannel();

        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(IntervalGatheringWorker.class).build();
        WorkManager.getInstance(this.getApplicationContext()).enqueue(request);

//        RecyclerView recyclerView = findViewById(R.id.data_view);
//
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(layoutManager);
//
//        Logger logger = new Logger();
//
//        try {
//            List<LogEvent> events = logger.getData(this.getApplicationContext());
//            RecyclerView.Adapter mAdapter = new LogEventAdapter(events);
//            recyclerView.setAdapter(mAdapter);
//
//        } catch (ExecutionException | InterruptedException e) {
//            e.printStackTrace();
//        }

    }

    @Override
    public void onResume() {
        super.onResume();
        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(IntervalGatheringWorker.class).build();
        WorkManager.getInstance(this.getApplicationContext()).enqueue(request);
    }


    void requestUsageStatsPermission() {
        if (!hasUsageStatsPermission(this)) {
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Collection Update";
            String description = "Shows a notification is data is collected or not";
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
