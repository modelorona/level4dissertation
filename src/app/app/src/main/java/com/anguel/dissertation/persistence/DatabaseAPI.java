package com.anguel.dissertation.persistence;

import android.content.Context;

import com.anguel.dissertation.persistence.entity.calls.Call;
import com.anguel.dissertation.persistence.entity.location.Location;
import com.anguel.dissertation.persistence.asynctasks.calls.AsyncCallGet;
import com.anguel.dissertation.persistence.asynctasks.calls.AsyncCallSave;
import com.anguel.dissertation.persistence.asynctasks.categories.AsyncCategoryGet;
import com.anguel.dissertation.persistence.asynctasks.categories.AsyncCategorySave;
import com.anguel.dissertation.persistence.asynctasks.location.AsyncLocationGet;
import com.anguel.dissertation.persistence.asynctasks.location.AsyncLocationSave;
import com.anguel.dissertation.persistence.asynctasks.location.AsyncLocationsSaveMultiple;
import com.anguel.dissertation.persistence.asynctasks.logs.AsyncLogGet;
import com.anguel.dissertation.persistence.asynctasks.logs.AsyncLogSave;
import com.anguel.dissertation.persistence.asynctasks.sias.AsyncSiasGet;
import com.anguel.dissertation.persistence.asynctasks.sias.AsyncSiasSave;
import com.anguel.dissertation.persistence.entity.appcategory.AppCategory;
import com.anguel.dissertation.persistence.entity.logevent.LogEvent;
import com.anguel.dissertation.persistence.entity.userdata.UserData;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class DatabaseAPI {

    private static volatile DatabaseAPI instance;

    public static DatabaseAPI getInstance() {
        if (instance == null) {
            synchronized (DatabaseAPI.class) {
                instance = new DatabaseAPI();
            }
        } return instance;
    }

    private DatabaseAPI() {}

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

    public Boolean saveMultipleLocations(Context context, List<Location> data) throws ExecutionException, InterruptedException {
        AsyncLocationsSaveMultiple runner = new AsyncLocationsSaveMultiple();
        return runner.execute(context, data).get();
    }
}
