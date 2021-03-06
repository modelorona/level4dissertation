package com.anguel.dissertation.persistence.asynctasks.logs;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.anguel.dissertation.persistence.entity.logevent.LogEvent;
import com.anguel.dissertation.persistence.entity.logevent.LogEventDatabase;

import java.util.List;

public class AsyncLogGet extends AsyncTask<Context, Void, List<LogEvent>> {


    @Override
    protected List<LogEvent> doInBackground(Context... contexts) {
        return LogEventDatabase.getInstance(contexts[0]).logEventDao().getAll();
    }

    @Override
    protected void onPreExecute() {
        Log.d("async_task", "started getting data");
    }
}
