package com.anguel.dissertation.persistence.logger.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.anguel.dissertation.persistence.database.calls.Call;
import com.anguel.dissertation.persistence.database.calls.CallDatabase;

import java.util.List;

public class AsyncCallGet extends AsyncTask<Context, Void, List<Call>> {
    @Override
    protected List<Call> doInBackground(Context... contexts) {
        return CallDatabase.getInstance(contexts[0]).callDao().getAll();
    }

    @Override
    protected void onPreExecute() {
        Log.d("async_call", "started getting call");
    }
}
