package com.anguel.dissertation.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.anguel.dissertation.R;
import com.anguel.dissertation.persistence.database.calls.Call;
import com.anguel.dissertation.persistence.logger.Logger;

import java.util.concurrent.ExecutionException;

import io.sentry.Sentry;

public class SaveCallTimesWorker extends Worker {

    public SaveCallTimesWorker(@NonNull Context context, @NonNull WorkerParameters workerParameters) {
        super(context, workerParameters);
    }

    private String getString(int id) {
        return getApplicationContext().getString(id);
    }

    @NonNull
    @Override
    public Result doWork() {
        Logger logger = new Logger();

        Call call = new Call();
        call.setStartTime(getInputData().getLong(getString(R.string.call_start), -1L));
        call.setEndTime(getInputData().getLong(getString(R.string.call_end), -1L));

        try {
            boolean res = logger.saveCall(getApplicationContext(), call);
            if (res) {
                return Result.success();
            }

            Sentry.capture(getString(R.string.unknown_fail_save));

        } catch (InterruptedException | ExecutionException e) {
            Sentry.capture(e);
            e.printStackTrace();
        }

        return Result.failure();
    }
}
