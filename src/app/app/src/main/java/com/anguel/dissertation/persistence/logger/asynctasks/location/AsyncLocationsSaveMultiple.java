package com.anguel.dissertation.persistence.logger.asynctasks.location;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.anguel.dissertation.persistence.database.location.LocationDatabase;

import java.util.List;

public class AsyncLocationsSaveMultiple extends AsyncTask<Object, Void, Boolean> {
    @Override
    protected Boolean doInBackground(Object... objects) {
        Context c = (Context) objects[0];
        List locations = (List) objects[1];

        LocationDatabase.getInstance(c).locationDao().insertLocations(locations);
        return true;
    }

    @Override
    protected void onPreExecute() {
        Log.d("async_location", "started saving multiple locations");
    }
}
