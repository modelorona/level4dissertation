package com.anguel.dissertation.persistence.logger;

import android.content.Context;

import com.anguel.dissertation.persistence.database.calls.Call;
import com.anguel.dissertation.persistence.database.location.Location;
import com.anguel.dissertation.persistence.logger.asynctasks.calls.AsyncCallGet;
import com.anguel.dissertation.persistence.logger.asynctasks.calls.AsyncCallSave;
import com.anguel.dissertation.persistence.logger.asynctasks.categories.AsyncCategoryGet;
import com.anguel.dissertation.persistence.logger.asynctasks.categories.AsyncCategorySave;
import com.anguel.dissertation.persistence.logger.asynctasks.location.AsyncLocationGet;
import com.anguel.dissertation.persistence.logger.asynctasks.location.AsyncLocationSave;
import com.anguel.dissertation.persistence.logger.asynctasks.logs.AsyncLogGet;
import com.anguel.dissertation.persistence.logger.asynctasks.logs.AsyncLogSave;
import com.anguel.dissertation.persistence.logger.asynctasks.sias.AsyncSiasGet;
import com.anguel.dissertation.persistence.logger.asynctasks.sias.AsyncSiasSave;
import com.anguel.dissertation.persistence.database.appcategory.AppCategory;
import com.anguel.dissertation.persistence.database.logevent.LogEvent;
import com.anguel.dissertation.persistence.database.userdata.UserData;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class Logger {

    public Boolean saveLogData(Context context, LogEvent data) throws ExecutionException, InterruptedException {
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

    public List<AppCategory> getAppCategories(Context context) throws ExecutionException, InterruptedException {
        AsyncCategoryGet runner = new AsyncCategoryGet();
        return runner.execute(context).get();
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

    public Boolean saveCall(Context context, Call data) throws ExecutionException, InterruptedException {
        AsyncCallSave runner = new AsyncCallSave();
        return runner.execute(context, data).get();
    }

    public List<Call> getCallData(Context context) throws ExecutionException, InterruptedException {
        AsyncCallGet runner = new AsyncCallGet();
        return runner.execute(context).get();
    }

    public Boolean saveLocation(Context context, Location data) throws ExecutionException, InterruptedException {
        AsyncLocationSave runner = new AsyncLocationSave();
        return runner.execute(context, data).get();
    }

    public List<Location> getLocationData(Context context) throws ExecutionException, InterruptedException {
        AsyncLocationGet runner = new AsyncLocationGet();
        return runner.execute(context).get();
    }
}
