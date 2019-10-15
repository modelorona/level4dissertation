package com.anguel.dissertation;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.anguel.dissertation.logger.Logger;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        requestUsageStatsPermission();

        Logger logger = new Logger();

        try {
            Toast.makeText(this, logger.getData(this).toString(), Toast.LENGTH_LONG).show();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }


//        TextView t = (TextView) findViewById(R.id.text);
//
//        UsageStatsManager usm = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);
//        long time = System.currentTimeMillis();
//        List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - TimeUnit.DAYS.toMillis(1), time);
//        if (appList != null && appList.size() == 0) {
//            Log.d("Executed", "######### NO APP FOUND ##########");
//        }
//        if (appList != null && appList.size() > 0) {
//            SortedMap<Long, UsageStats> mySortedMap = new TreeMap<>(Collections.<Long>reverseOrder());
//            for (UsageStats usageStats : appList) {
//                mySortedMap.put(usageStats.getTotalTimeInForeground(), usageStats);
//            }
//            if (!mySortedMap.isEmpty()) {
//                int max = 10;
//                int current = 0;
//                for (Map.Entry<Long, UsageStats> x : mySortedMap.entrySet()) {
//                    if (current == max) {
//                        continue;
//                    } else {
//                        current++;
//                        t.append("app name: " + appName(x.getValue().getPackageName()) + System.lineSeparator() + "minutes: " + TimeUnit.MILLISECONDS.toMinutes(x.getValue().getTotalTimeInForeground()) + System.lineSeparator() + "last used: " + new Date(x.getValue().getLastTimeUsed()).toString());
//                        t.append(System.lineSeparator());
//                        t.append(System.lineSeparator());
//                        Log.d("Executed", "usage stats executed : " + x.getValue().getPackageName() + "\t\t totalTimeForeground: " + x.getValue().getTotalTimeInForeground());
//                    }
//                }
//            }
//        }

    }

    public String appName(String pack) {
        String name = null;

        try {
            PackageManager packManager = getBaseContext().getPackageManager();
            ApplicationInfo app = getBaseContext().getPackageManager().getApplicationInfo(pack, 0);
            name = packManager.getApplicationLabel(app).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return name;
    }


    void requestUsageStatsPermission() {
        if (!hasUsageStatsPermission(this)) {
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    boolean hasUsageStatsPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow("android:get_usage_stats",
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
}
