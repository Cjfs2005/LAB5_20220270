package com.example.lab5_20220270.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.lab5_20220270.model.Course;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PreferencesManager {
    private static final String PREFS_NAME = "study_prefs";
    private static final String KEY_COURSES = "courses";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_MOTIVATION_MESSAGE = "motivation_message";
    private static final String KEY_MOTIVATION_INTERVAL_HOURS = "motivation_interval_hours";

    private final SharedPreferences prefs;
    private final Gson gson = new Gson();

    public PreferencesManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveCourses(List<Course> courses) {
        String json = gson.toJson(courses);
        prefs.edit().putString(KEY_COURSES, json).apply();
    }

    public List<Course> getCourses() {
        String json = prefs.getString(KEY_COURSES, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<List<Course>>() {}.getType();
        List<Course> list = gson.fromJson(json, type);
        if (list == null) return new ArrayList<>();
        return list;
    }

    public void saveUserName(String name) {
        prefs.edit().putString(KEY_USER_NAME, name).apply();
    }

    public String getUserName() {
        return prefs.getString(KEY_USER_NAME, "Christian");
    }

    public void saveMotivationMessage(String msg) {
        prefs.edit().putString(KEY_MOTIVATION_MESSAGE, msg).apply();
    }

    public String getMotivationMessage() {
        return prefs.getString(KEY_MOTIVATION_MESSAGE, "Hoy es un gran día para aprender");
    }

    public void saveMotivationIntervalHours(int hours) {
        prefs.edit().putInt(KEY_MOTIVATION_INTERVAL_HOURS, hours).apply();
    }

    public int getMotivationIntervalHours() {
        return prefs.getInt(KEY_MOTIVATION_INTERVAL_HOURS, 24);
    }
}
