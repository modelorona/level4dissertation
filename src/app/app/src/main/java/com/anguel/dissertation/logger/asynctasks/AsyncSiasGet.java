package com.anguel.dissertation.logger.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.anguel.dissertation.persistence.userdata.UserData;
import com.anguel.dissertation.persistence.userdata.UserDataDatabase;

import java.util.List;

public class AsyncSiasGet extends AsyncTask<Context, Void, List<UserData>> {

    @Override
    protected List<UserData> doInBackground(Context... contexts) {
        return UserDataDatabase.getInstance(contexts[0]).userDataDao().getAll();
    }
}
