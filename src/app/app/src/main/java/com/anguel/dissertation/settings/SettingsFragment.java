package com.anguel.dissertation.settings;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.anguel.dissertation.R;
import com.anguel.dissertation.logger.Logger;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class SettingsFragment extends PreferenceFragmentCompat {
    private SwitchPreferenceCompat batteryOpt;
    private SwitchPreferenceCompat usageStatsPms;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        batteryOpt = findPreference(getString(R.string.battery_opt_pref));
        usageStatsPms = findPreference(getString(R.string.usage_stats_pref));
        Preference personSias = findPreference(getString(R.string.see_personal_sias_pref));

//        toggle their values based on the current setting
        togglePreferenceValues();

        batteryOpt.setOnPreferenceClickListener(preference -> {
//            even if they try to disable it, it won't do anything. better to let them know instead of mislead
            disableBatteryOptimisation();
            return true;
        });

        usageStatsPms.setOnPreferenceClickListener(preference -> {
            requestUsageStatsPermission();
            return true;
        });

        try {
            Objects.requireNonNull(personSias).setSummary(String.valueOf(new Logger().getUserData(Objects.requireNonNull(getActivity()).getApplicationContext()).get(0).getSias()));
        } catch (ExecutionException | InterruptedException e) {
            Objects.requireNonNull(personSias).setSummary("You have not yet taken the test.");
            e.printStackTrace();
        }

    }

    private void togglePreferenceValues() {
        if (hasUsageStatsPermission(Objects.requireNonNull(getActivity()))) {
            Objects.requireNonNull(usageStatsPms).setChecked(true);
            Objects.requireNonNull(usageStatsPms).setEnabled(false);
        } else {
            Objects.requireNonNull(usageStatsPms).setChecked(false);
        }

        if (isBatteryOptDisabled()) {
            Objects.requireNonNull(batteryOpt).setChecked(true);
            Objects.requireNonNull(batteryOpt).setEnabled(false);
        } else {
            Objects.requireNonNull(batteryOpt).setChecked(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        to make sure it cannot be toggled right after
        togglePreferenceValues();
    }

    private void requestUsageStatsPermission() {
        if (!hasUsageStatsPermission(Objects.requireNonNull(getActivity()))) {
            startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), 102);
        }
    }

    @SuppressLint("BatteryLife")
    private void disableBatteryOptimisation() {
        startActivityForResult(new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS), 101);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private boolean hasUsageStatsPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = Objects.requireNonNull(appOps).checkOpNoThrow("android:get_usage_stats",
                android.os.Process.myUid(), context.getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    private boolean isBatteryOptDisabled() {
        PowerManager pm = (PowerManager) Objects.requireNonNull(getActivity()).getSystemService(Context.POWER_SERVICE);
        return Objects.requireNonNull(pm).isIgnoringBatteryOptimizations(getActivity().getPackageName());
    }
}
