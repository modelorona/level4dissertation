package com.anguel.dissertation.datacollection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.anguel.dissertation.MainActivity;
import com.anguel.dissertation.R;

import java.util.Objects;

public class DataCollectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_collection);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle("Data Collection");

        Intent keepAliveIntent = new Intent(getApplicationContext(), KeepAliveService.class);
        keepAliveIntent.setAction(getString(R.string.ACTION_KEEP_ALIVE));
        startService(keepAliveIntent);

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
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
