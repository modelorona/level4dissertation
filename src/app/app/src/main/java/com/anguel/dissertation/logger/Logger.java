package com.anguel.dissertation.logger;

import android.content.Context;

import com.anguel.dissertation.persistence.LogEvent;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class Logger {

    public void saveData(Context context) {
        AsyncLogSave runner = new AsyncLogSave();
        runner.execute(context);
    }

    public List<LogEvent> getData(Context context) throws ExecutionException, InterruptedException {
        AsyncLogGet runner = new AsyncLogGet();
        return runner.execute(context).get();
    }
}
