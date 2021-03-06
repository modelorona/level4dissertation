package com.anguel.dissertation.persistence.asynctasks.calls;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.anguel.dissertation.persistence.entity.calls.Call;
import com.anguel.dissertation.persistence.entity.calls.CallDatabase;

public class AsyncCallSave extends AsyncTask<Object, Void, Boolean> {
    @Override
    protected Boolean doInBackground(Object... objects) {
        Context c = (Context) objects[0];
        Call call = (Call) objects[1];

        CallDatabase.getInstance(c).callDao().insertCallEvent(call);
        return true;
    }

    @Override
    protected void onPreExecute() {
        Log.d("async_call", "started saving call");
    }
}
