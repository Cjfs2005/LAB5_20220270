package com.example.lab5_20220270;

import android.app.Application;

import com.example.lab5_20220270.storage.PreferencesManager;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        NotificationHelper.createChannels(this);
        PreferencesManager prefs = new PreferencesManager(this);

        int interval = prefs.getMotivationIntervalHours();
        long delayMillis = java.util.concurrent.TimeUnit.HOURS.toMillis(interval);

        androidx.work.Data data = new androidx.work.Data.Builder()
                .putString("message", prefs.getMotivationMessage())
                .build();

        androidx.work.OneTimeWorkRequest req = new androidx.work.OneTimeWorkRequest.Builder(com.example.lab5_20220270.workers.MotivationWorker.class)
                .setInitialDelay(delayMillis, java.util.concurrent.TimeUnit.MILLISECONDS)
                .setInputData(data)
                .addTag("motivation_worker")
                .build();

        androidx.work.WorkManager.getInstance(this).enqueue(req);
    }
}
