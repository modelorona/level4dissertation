package com.anguel.dissertation.utils;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.anguel.dissertation.R;

import java.util.Objects;
import java.util.UUID;

public class Utils {

    public Utils() {}

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


}
