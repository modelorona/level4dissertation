package com.anguel.dissertation.settings;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import com.anguel.dissertation.App;
import com.anguel.dissertation.BuildConfig;
import com.anguel.dissertation.persistence.entity.appcategory.AppCategory;
import com.anguel.dissertation.persistence.entity.calls.Call;
import com.anguel.dissertation.persistence.entity.location.Location;
import com.anguel.dissertation.persistence.entity.logevent.LogEvent;
import com.anguel.dissertation.persistence.entity.userdata.UserData;
import com.anguel.dissertation.utils.Utils;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.anguel.dissertation.R;
import com.anguel.dissertation.persistence.DatabaseAPI;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class SettingsFragment extends PreferenceFragmentCompat {
    private SwitchPreferenceCompat batteryOpt;
    private SwitchPreferenceCompat usageStatsPms;
    private SwitchPreferenceCompat callPms;
    private SwitchPreferenceCompat locationPms;
    private Utils utils;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        batteryOpt = findPreference(getString(R.string.battery_opt_pref));
        usageStatsPms = findPreference(getString(R.string.usage_stats_pref));
        callPms = findPreference(getString(R.string.call_pms));
        locationPms = findPreference(getString(R.string.location_pref));
        Preference personSias = findPreference(getString(R.string.see_personal_sias_pref));
        Preference licenseInfo = findPreference(getString(R.string.license_pref));
        Preference optOut = findPreference(getString(R.string.opt_out_pref));
        Preference id = findPreference(getString(R.string.see_personal_id_pref));
        Preference csvExport = findPreference(getString(R.string.see_your_data_pref));
        utils = Utils.getInstance();

//        toggle their values based on the current setting
        togglePreferenceValues();

        batteryOpt.setOnPreferenceClickListener(preference -> {
//            even if they try to disable it, it won't do anything
            disableBatteryOptimisation();
            return true;
        });

        usageStatsPms.setOnPreferenceClickListener(preference -> {
            requestUsageStatsPermission();
            return true;
        });

        callPms.setOnPreferenceClickListener(preference -> {
            requestCallPermissions();
            return true;
        });

        locationPms.setOnPreferenceClickListener(preference -> {
            requestLocationPermissions();
            return true;
        });

        try {
            Objects.requireNonNull(personSias).setSummary(String.valueOf(DatabaseAPI.getInstance().getUserData(Objects.requireNonNull(getActivity()).getApplicationContext()).get(0).getSias()));
        } catch (ExecutionException | InterruptedException | IndexOutOfBoundsException e) {
            Objects.requireNonNull(personSias).setSummary(getString(R.string.not_yet_taken_test));
            e.printStackTrace();
        }

        try {
            Objects.requireNonNull(id).setSummary("Your identifier is: " + Utils.getInstance().getUserID(Objects.requireNonNull(getActivity()).getApplicationContext()) + ". Click to copy it.");
            Objects.requireNonNull(id).setOnPreferenceClickListener(preference -> {
                ClipboardManager clipboard = (ClipboardManager) Objects.requireNonNull(getActivity()).getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData userId = ClipData.newPlainText(getString(R.string.cliptext_id), utils.getUserID(getActivity().getApplicationContext()));
                Objects.requireNonNull(clipboard).setPrimaryClip(userId);

                CharSequence text = getString(R.string.id_copied);
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), text, duration);
                toast.show();

                return true;
            });
        } catch (Exception e) {
            Objects.requireNonNull(id).setSummary(R.string.pref_no_id);
            e.printStackTrace();
        }

        Objects.requireNonNull(licenseInfo).setOnPreferenceClickListener(preference -> {
            startActivity(new Intent(Objects.requireNonNull(getActivity()).getApplicationContext(), OssLicensesMenuActivity.class));
            return true;
        });

        Objects.requireNonNull(optOut).setOnPreferenceClickListener(preference -> {
            ClipboardManager clipboard = (ClipboardManager) Objects.requireNonNull(getActivity()).getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData userId = ClipData.newPlainText(getString(R.string.cliptext_id), utils.getUserID(getActivity().getApplicationContext()));
            Objects.requireNonNull(clipboard).setPrimaryClip(userId);

            CharSequence text = getString(R.string.id_copied);
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), text, duration);
            toast.show();

            Uri webpage = Uri.parse(getString(R.string.opt_out_form));
            Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(intent);
            }

            return true;
        });

        Objects.requireNonNull(csvExport).setOnPreferenceClickListener(preference -> {
            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 99);
            String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();

            String fileName = getString(R.string.csv_file_name);
            String filePath = baseDir + File.separator + "Documents" + File.separator + fileName;

            try (FileWriter writer = new FileWriter(filePath); CSVWriter csvWriter = new CSVWriter(writer)) {
//                try writing each individual database one by one
                try {
                    UserData userData = DatabaseAPI.getInstance().getUserData(getContext()).get(0);
                    csvWriter.writeNext(new String[]{"personal identifier", "SIAS score"});
                    csvWriter.writeNext(new String[]{userData.getUserId(), String.valueOf(userData.getSias())});
                    csvWriter.writeNext(new String[]{""});
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    List<Call> calls = DatabaseAPI.getInstance().getCallData(getContext());
                    List<String[]> lines = new ArrayList<>(calls.size());

                    calls.forEach(call -> lines.add(new String[]{String.valueOf(call.getStartTime()), String.valueOf(call.getEndTime())}));

                    csvWriter.writeNext(new String[]{"call start time", "call end time"});
                    csvWriter.writeAll(lines);
                    csvWriter.writeNext(new String[]{""});

                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    List<AppCategory> appCategories = DatabaseAPI.getInstance().getAppCategories(getContext());
                    List<String[]> lines = new ArrayList<>(appCategories.size());

                    appCategories.forEach(appCategory -> lines.add(new String[]{appCategory.getAppName(), appCategory.getCategory(), appCategory.getPackageName()}));
                    csvWriter.writeNext(new String[]{"app name", "app category", "app package name"});
                    csvWriter.writeAll(lines);
                    csvWriter.writeNext(new String[]{""});
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    List<LogEvent> logEvents = DatabaseAPI.getInstance().getLogData(getContext());
                    List<String[]> lines = new ArrayList<>(logEvents.size());
                    logEvents.forEach(ev -> lines.add(new String[]{ev.toString()}));
                    csvWriter.writeNext(new String[]{"session data"});
                    csvWriter.writeAll(lines);
                    csvWriter.writeNext(new String[]{""});
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    List<Location> locations = DatabaseAPI.getInstance().getLocationData(getContext());
                    List<String[]> lines = new ArrayList<>(locations.size());
                    locations.forEach(l -> lines.add(new String[]{l.toString()}));
                    csvWriter.writeNext(new String[]{"location data"});
                    csvWriter.writeAll(lines);
                    csvWriter.writeNext(new String[]{""});
                } catch (Exception e) {
                    e.printStackTrace();
                }


            } catch (IOException e) {
                e.printStackTrace();
            }

            AlertDialog.Builder alert = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
            alert.setTitle(getString(R.string.data_export_success_title))
                    .setMessage(getString(R.string.data_export_success_message) + " " + filePath);
//                        .setCancelable(false);

            alert.setPositiveButton(getString(R.string.data_export_view_file), ((dialog, which) -> {
                final Uri data = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".myprovider", new File(filePath));
                getContext().grantUriPermission(getContext().getPackageName(), data, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Intent intent = new Intent(Intent.ACTION_VIEW)
                        .setDataAndType(data, "text/csv")
                        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
            }));

            alert.create().show();

            return true;
        });
    }

    @Override
    public void onResume() {
        super.onResume();
//        to make sure it cannot be toggled right after
        togglePreferenceValues();
    }

    private void togglePreferenceValues() {
        if (utils.hasUsageStatsPermission(Objects.requireNonNull(getActivity()).getApplicationContext())) {
            Objects.requireNonNull(usageStatsPms).setChecked(true);
            Objects.requireNonNull(usageStatsPms).setEnabled(false);
        } else {
            Objects.requireNonNull(usageStatsPms).setChecked(false);
        }

        if (utils.isBatteryOptDisabled(getActivity().getApplicationContext())) {
            Objects.requireNonNull(batteryOpt).setChecked(true);
            Objects.requireNonNull(batteryOpt).setEnabled(false);
        } else {
            Objects.requireNonNull(batteryOpt).setChecked(false);
        }

        if (utils.isCallPermissionEnabled(getActivity().getApplicationContext())) {
            Objects.requireNonNull(callPms).setChecked(true);
            Objects.requireNonNull(callPms).setEnabled(false);
        } else {
            Objects.requireNonNull(callPms).setChecked(false);
        }

        if (utils.isLocationPermissionEnabled(getActivity().getApplicationContext())) {
            Objects.requireNonNull(locationPms).setChecked(true);
            Objects.requireNonNull(locationPms).setEnabled(false);
        } else {
            Objects.requireNonNull(locationPms).setChecked(false);
        }
    }

    private void requestLocationPermissions() {
        List<String> permissions = new LinkedList<>(Collections.singletonList(Manifest.permission.ACCESS_FINE_LOCATION));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        }

        ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()),
                permissions.toArray(new String[0]),
                getResources().getInteger(R.integer.request_location_permissions));
    }

    private void requestCallPermissions() {
        ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()),
                new String[]{Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_PHONE_STATE},
                getResources().getInteger(R.integer.request_call_permissions));
    }

    private void requestUsageStatsPermission() {
        if (!utils.hasUsageStatsPermission(Objects.requireNonNull(getActivity()).getApplicationContext())) {
            startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), getActivity().getResources().getInteger(R.integer.request_usage_stats_code));
        }
    }

    @SuppressLint("BatteryLife")
    private void disableBatteryOptimisation() {
        startActivityForResult(new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS), Objects.requireNonNull(getActivity()).getResources().getInteger(R.integer.request_battery_optimisation_code));
    }

}
