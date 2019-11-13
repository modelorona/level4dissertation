package com.anguel.dissertation.logger;

import android.content.Context;

import com.anguel.dissertation.persistence.appcategory.AppCategory;
import com.anguel.dissertation.persistence.logevent.LogEvent;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class Logger {

    public Boolean saveAppStatistics(Context context, LogEvent data) throws ExecutionException, InterruptedException {
        AsyncLogSave runner = new AsyncLogSave();
        return runner.execute(context, data).get();
    }

    public List<LogEvent> getData(Context context) throws ExecutionException, InterruptedException {
        AsyncLogGet runner = new AsyncLogGet();
        return runner.execute(context).get();
    }

    public Boolean saveAppCategory(Context context, AppCategory data) throws ExecutionException, InterruptedException {
        AsyncCategorySave runner = new AsyncCategorySave();
        return runner.execute(context, data).get();
    }
}
