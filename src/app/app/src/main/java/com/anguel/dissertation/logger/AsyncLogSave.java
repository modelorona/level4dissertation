package com.anguel.dissertation.logger;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.anguel.dissertation.persistence.LogEvent;
import com.anguel.dissertation.persistence.LogEventDatabase;

class AsyncLogSave extends AsyncTask<Object, Void, Boolean> {

    @Override
    protected Boolean doInBackground(Object... params) {
//        params[0] will be the context, params[1] will be the data to save
//        where the data is saved, received as a parameter
        Context c = (Context) params[0];
        LogEvent data = (LogEvent) params[1];

        LogEventDatabase.getInstance(c).logEventDao().insertLogEvent(data);

        return true;
    }

    @Override
    protected void onPreExecute() {
        Log.d("async_task", "started saving data");
    }
}
