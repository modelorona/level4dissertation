package com.anguel.dissertation.persistence.logger.asynctasks.sias;

import android.content.Context;
import android.os.AsyncTask;

import com.anguel.dissertation.persistence.database.userdata.UserData;
import com.anguel.dissertation.persistence.database.userdata.UserDataDatabase;

import java.util.List;

public class AsyncSiasGet extends AsyncTask<Context, Void, List<UserData>> {

    @Override
    protected List<UserData> doInBackground(Context... contexts) {
        return UserDataDatabase.getInstance(contexts[0]).userDataDao().getAll();
    }
}
