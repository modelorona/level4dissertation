package com.anguel.dissertation.logger;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

class AsyncLogSave extends AsyncTask<Object, Void, Void> {

    @Override
    protected Void doInBackground(Object... params) {
//        params[0] will be the context, params[1] will be the data to save
//        where the data is saved, received as a parameter
        Context c = (Context) params[0];


        return null;
    }

    @Override
    protected void onPreExecute() {
        Log.d("async_task", "started saving data");
    }
}
