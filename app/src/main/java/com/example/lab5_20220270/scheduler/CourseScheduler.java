package com.example.lab5_20220270.scheduler;

import android.content.Context;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.lab5_20220270.model.Course;
import com.example.lab5_20220270.workers.CourseReminderWorker;

import java.util.concurrent.TimeUnit;

public class CourseScheduler {

    public static void scheduleCourseReminder(Context ctx, Course c) {
        long now = System.currentTimeMillis();
        long delay = c.getNextSessionMillis() - now;
        if (delay < 0) delay = 0;

        Data data = new Data.Builder()
                .putString("course_id", c.getId())
                .putString("course_name", c.getName())
                .putString("action", c.getActionSuggestion())
                .putInt("frequency_value", c.getFrequencyValue())
                .putString("frequency_unit", c.getFrequencyUnit())
                .putString("type", c.getType())
                .build();

        OneTimeWorkRequest req = new OneTimeWorkRequest.Builder(CourseReminderWorker.class)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .addTag(c.getId())
                .build();

        WorkManager.getInstance(ctx.getApplicationContext()).enqueue(req);
    }

    public static void cancelCourseReminder(Context ctx, String courseId) {
        WorkManager.getInstance(ctx.getApplicationContext()).cancelAllWorkByTag(courseId);
    }
}
