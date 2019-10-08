package com.anguel.dissertation;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.rvalerio.fgchecker.AppChecker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        requestUsageStatsPermission();

        final Object k = this.getPackageManager();
        final Object c = this.getSystemService(ACTIVITY_SERVICE);
        final Handler handler = new Handler(Looper.getMainLooper());

        TextView t = (TextView) findViewById(R.id.text);

        UsageStatsManager usm = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);
        long time = System.currentTimeMillis();
        List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - TimeUnit.DAYS.toMillis(1), time);
        if (appList != null && appList.size() == 0) {
            Log.d("Executed", "######### NO APP FOUND ##########");
        }
        if (appList != null && appList.size() > 0) {
            SortedMap<Long, UsageStats> mySortedMap = new TreeMap<>(Collections.<Long>reverseOrder());
            for (UsageStats usageStats : appList) {
                mySortedMap.put(usageStats.getTotalTimeInForeground(), usageStats);
            }
            if (mySortedMap != null && !mySortedMap.isEmpty()) {
                int max = 10;
                int current = 0;
                for (Map.Entry<Long, UsageStats> x : mySortedMap.entrySet()) {
                    if (current == max) {
                        continue;
                    } else {
                        current++;
                        t.append(x.getValue().getPackageName() + "\t " + x.getValue().getTotalTimeInForeground() + "\t " + x.getValue().getLastTimeUsed());
                        t.append(System.lineSeparator());
                        t.append(System.lineSeparator());
                        Log.d("Executed", "usage stats executed : " + x.getValue().getPackageName() + "\t\t totalTimeForeground: " + x.getValue().getTotalTimeInForeground());
                    }
                }
            }
        }

    }


    void requestUsageStatsPermission() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && !hasUsageStatsPermission(this)) {
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    boolean hasUsageStatsPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow("android:get_usage_stats",
                android.os.Process.myUid(), context.getPackageName());
        boolean granted = mode == AppOpsManager.MODE_ALLOWED;
        return granted;
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
}
