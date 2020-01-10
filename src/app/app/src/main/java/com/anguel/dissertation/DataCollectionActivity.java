package com.anguel.dissertation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ToggleButton;

import com.anguel.dissertation.serviceengine.ServiceEngine;
import com.jakewharton.threetenabp.AndroidThreeTen;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;

public class DataCollectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_collection);
        AndroidThreeTen.init(this);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.data_collection_activity_title));

        toggleDataCollection(isRecordingData());

        ToggleButton button = (ToggleButton) findViewById(R.id.toggleButton);

        button.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = this.getSharedPreferences(
                    getString(R.string.preference_file_key), Context.MODE_PRIVATE).edit();
            editor.putBoolean(getString(R.string.shpref_prefix) + getString(R.string.pref_data_record), isChecked);
            editor.apply();
            toggleDataCollection(isRecordingData());
        });

    }

    private void toggleDataCollection(boolean recordingData) {
        ToggleButton button = (ToggleButton) findViewById(R.id.toggleButton);
        button.setChecked(recordingData);
        ServiceEngine engine = ServiceEngine.getInstance(getApplicationContext());
        if (recordingData) {
            engine.startServices(getApplicationContext());
        } else {
            engine.stopServices(getApplicationContext());
        }
    }

    private boolean isRecordingData() {
        return getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
                .getBoolean(getString(R.string.shpref_prefix) + getString(R.string.pref_data_record), false);
    }

    @Override
    public void onResume() {
        super.onResume();
        toggleDataCollection(isRecordingData());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent goBackIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(goBackIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}
