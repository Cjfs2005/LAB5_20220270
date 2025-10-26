package com.example.lab5_20220270.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

import com.example.lab5_20220270.NotificationHelper;
import com.example.lab5_20220270.R;

public class CourseReminderWorker extends Worker {

    public CourseReminderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
    Data input = getInputData();
    String courseId = input.getString("course_id");
    String courseName = input.getString("course_name");
    if (courseName == null) courseName = "Mi curso";
    String action = input.getString("action");
    if (action == null) action = "Revisar apuntes";
    String type = input.getString("type");
    if (type == null) type = "Teórico";
    int freqValue = input.getInt("frequency_value", 24);
    String freqUnit = input.getString("frequency_unit");
    if (freqUnit == null) freqUnit = "HOURS";

        String channel = NotificationHelper.CHANNEL_THEORETICAL;
        if ("Laboratorio".equalsIgnoreCase(type)) channel = NotificationHelper.CHANNEL_LAB;
        else if ("Electivo".equalsIgnoreCase(type)) channel = NotificationHelper.CHANNEL_ELECTIVE;
        else if ("Otro".equalsIgnoreCase(type)) channel = NotificationHelper.CHANNEL_OTHER;

        // Use a stable notification id per course so each new reminder replaces the previous one
        int notificationId;
        if (courseId != null) {
            notificationId = Math.abs(courseId.hashCode());
            if (notificationId == Integer.MIN_VALUE) notificationId = Math.abs(notificationId + 1); // avoid MIN_VALUE edge
        } else {
            notificationId = (int) (System.currentTimeMillis() & 0x7fffffff);
        }

        NotificationHelper.sendNotification(getApplicationContext(), channel, notificationId, courseName, action, R.drawable.ic_launcher_foreground);

        long addMillis = 0L;
        if ("MINUTES".equalsIgnoreCase(freqUnit)) {
            addMillis = TimeUnit.MINUTES.toMillis(freqValue);
        } else if ("HOURS".equalsIgnoreCase(freqUnit)) {
            addMillis = TimeUnit.HOURS.toMillis(freqValue);
        } else {
            addMillis = TimeUnit.DAYS.toMillis(freqValue);
        }

        long nextMillis = System.currentTimeMillis() + addMillis;

    Data nextData = new Data.Builder()
                .putString("course_id", courseId)
                .putString("course_name", courseName)
                .putString("action", action)
                .putInt("frequency_value", freqValue)
                .putString("frequency_unit", freqUnit)
                .putString("type", type)
                .build();

        OneTimeWorkRequest nextReq = new OneTimeWorkRequest.Builder(CourseReminderWorker.class)
                .setInitialDelay(nextMillis - System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .setInputData(nextData)
                .addTag(courseId)
                .build();

        WorkManager.getInstance(getApplicationContext()).enqueue(nextReq);

        return Result.success();
    }
}
