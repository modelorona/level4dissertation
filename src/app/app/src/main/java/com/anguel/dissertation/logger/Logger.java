package com.anguel.dissertation.logger;

import android.content.Context;

import com.anguel.dissertation.persistence.LogEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Logger {

    public Boolean saveData(Context context, ArrayList<HashMap<String, String>> data) throws ExecutionException, InterruptedException {
        AsyncLogSave runner = new AsyncLogSave();
        return runner.execute(context, data).get();
    }

    public List<LogEvent> getData(Context context) throws ExecutionException, InterruptedException {
        AsyncLogGet runner = new AsyncLogGet();
        return runner.execute(context).get();
    }
}
