package com.anguel.dissertation.persistence.logger.asynctasks.location;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.anguel.dissertation.persistence.database.location.Location;
import com.anguel.dissertation.persistence.database.location.LocationDatabase;

public class AsyncLocationSave extends AsyncTask<Object, Void, Boolean> {
    @Override
    protected Boolean doInBackground(Object... objects) {
        Context c = (Context) objects[0];
        Location location = (Location) objects[1];

        LocationDatabase.getInstance(c).locationDao().insertLocationEvent(location);
        return true;
    }

    @Override
    protected void onPreExecute() {
        Log.d("async_location", "started saving location");
    }
}
