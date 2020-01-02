package com.anguel.dissertation.logger.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.anguel.dissertation.persistence.userdata.UserData;
import com.anguel.dissertation.persistence.userdata.UserDataDatabase;

public class AsyncSiasSave extends AsyncTask<Object, Void, Boolean> {
    @Override
    protected Boolean doInBackground(Object... params) {
        Context c = (Context) params[0];
        UserData userData = (UserData) params[1];
        UserDataDatabase.getInstance(c).userDataDao().insertUserData(userData);
        return true;
    }

    @Override
    protected void onPreExecute() {
        Log.d("async_task", "started saving sias score");
    }
}
