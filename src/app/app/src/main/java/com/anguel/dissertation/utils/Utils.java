package com.anguel.dissertation.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.PowerManager;

import androidx.core.content.ContextCompat;

import com.anguel.dissertation.R;

import java.util.Objects;
import java.util.UUID;

public class Utils {

    private static Utils INSTANCE;

    public static Utils getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Utils();
        } return INSTANCE;
    }

    private Utils() {}

    public String getUserID(Context ctx) {
//        preferencemanager was deprecated
        SharedPreferences sharedPref = ctx.getSharedPreferences(
                ctx.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String id = sharedPref.getString(ctx.getString(R.string.shpref_prefix)+"_ID", "");
        if (id.equalsIgnoreCase("")) {
            UUID g = UUID.randomUUID();
            id = g.toString();
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(ctx.getString(R.string.shpref_prefix)+"_ID", id);
            editor.apply();
        }
        return id;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public boolean hasUsageStatsPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = Objects.requireNonNull(appOps).checkOpNoThrow("android:get_usage_stats",
                android.os.Process.myUid(), context.getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    public boolean isCallPermissionEnabled(Context context) {
        return (ContextCompat.checkSelfPermission(Objects.requireNonNull(context), Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(Objects.requireNonNull(context), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED);
    }

    public boolean isBatteryOptDisabled(Context context) {
        PowerManager pm = (PowerManager) Objects.requireNonNull(context).getSystemService(Context.POWER_SERVICE);
        return Objects.requireNonNull(pm).isIgnoringBatteryOptimizations(context.getPackageName());
    }

    public boolean isLocationPermissionEnabled(Context context) {
        boolean x = ContextCompat.checkSelfPermission(Objects.requireNonNull(context), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return x &&
                    (ContextCompat.checkSelfPermission(Objects.requireNonNull(context), Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED);
        } else {
            return x;
        }
    }

    public boolean areAllPermissionsEnabled(Context context) {
        return hasUsageStatsPermission(context) && isCallPermissionEnabled(context) && isBatteryOptDisabled(context) && isLocationPermissionEnabled(context);
    }

}
