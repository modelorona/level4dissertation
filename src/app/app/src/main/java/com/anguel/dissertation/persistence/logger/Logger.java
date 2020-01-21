package com.anguel.dissertation.persistence.logger;

import android.content.Context;

import com.anguel.dissertation.persistence.logger.asynctasks.AsyncCategoryGet;
import com.anguel.dissertation.persistence.logger.asynctasks.AsyncCategorySave;
import com.anguel.dissertation.persistence.logger.asynctasks.AsyncLogGet;
import com.anguel.dissertation.persistence.logger.asynctasks.AsyncLogSave;
import com.anguel.dissertation.persistence.logger.asynctasks.AsyncSiasGet;
import com.anguel.dissertation.persistence.logger.asynctasks.AsyncSiasSave;
import com.anguel.dissertation.persistence.database.appcategory.AppCategory;
import com.anguel.dissertation.persistence.database.logevent.LogEvent;
import com.anguel.dissertation.persistence.database.userdata.UserData;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class Logger {

    public Boolean saveAppStatistics(Context context, LogEvent data) throws ExecutionException, InterruptedException {
        AsyncLogSave runner = new AsyncLogSave();
        return runner.execute(context, data).get();
    }

    public List<LogEvent> getLogData(Context context) throws ExecutionException, InterruptedException {
        AsyncLogGet runner = new AsyncLogGet();
        return runner.execute(context).get();
    }

    public Boolean saveAppCategory(Context context, AppCategory data) throws ExecutionException, InterruptedException {
        AsyncCategorySave runner = new AsyncCategorySave();
        return runner.execute(context, data).get();
    }

    @SuppressWarnings("UnusedReturnValue")
    public Boolean saveSiasScore(Context context, UserData data) throws ExecutionException, InterruptedException {
        AsyncSiasSave runner = new AsyncSiasSave();
        return runner.execute(context, data).get();
    }

    public List<UserData> getUserData(Context context) throws ExecutionException, InterruptedException {
        AsyncSiasGet runner = new AsyncSiasGet();
        return runner.execute(context).get();
    }

    public List<AppCategory> getAppCategories(Context context) throws ExecutionException, InterruptedException {
        AsyncCategoryGet runner = new AsyncCategoryGet();
        return runner.execute(context).get();
    }
}
