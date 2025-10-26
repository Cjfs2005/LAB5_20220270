package com.example.lab5_20220270.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import androidx.work.Data;

import com.example.lab5_20220270.NotificationHelper;
import com.example.lab5_20220270.R;
import com.example.lab5_20220270.storage.PreferencesManager;

public class MotivationWorker extends Worker {

    public MotivationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
    Data input = getInputData();
    String message = input.getString("message");
    if (message == null) message = "Un paso más hacia la meta";
    NotificationHelper.sendNotification(getApplicationContext(), NotificationHelper.CHANNEL_MOTIVATION, 1000, "Motivación", message, R.drawable.ic_launcher_foreground);

    PreferencesManager prefs = new PreferencesManager(getApplicationContext());
    int interval = prefs.getMotivationIntervalHours();
    long addMillis = java.util.concurrent.TimeUnit.HOURS.toMillis(interval);

    androidx.work.Data nextData = new androidx.work.Data.Builder()
        .putString("message", prefs.getMotivationMessage())
        .build();

    androidx.work.OneTimeWorkRequest nextReq = new androidx.work.OneTimeWorkRequest.Builder(MotivationWorker.class)
        .setInitialDelay(addMillis, java.util.concurrent.TimeUnit.MILLISECONDS)
        .setInputData(nextData)
        .addTag("motivation_worker")
        .build();

    androidx.work.WorkManager.getInstance(getApplicationContext()).enqueue(nextReq);

    return Result.success();
    }
}
