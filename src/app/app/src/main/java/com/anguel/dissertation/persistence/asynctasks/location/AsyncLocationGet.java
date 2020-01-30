package com.anguel.dissertation.persistence.asynctasks.location;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.anguel.dissertation.persistence.entity.location.Location;
import com.anguel.dissertation.persistence.entity.location.LocationDatabase;

import java.util.List;

public class AsyncLocationGet extends AsyncTask<Context, Void, List<Location>> {
    @Override
    protected List<Location> doInBackground(Context... contexts) {
        return LocationDatabase.getInstance(contexts[0]).locationDao().getAll();
    }

    @Override
    protected void onPreExecute() {
        Log.d("async_location", "started getting location");
    }
}
