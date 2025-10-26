package com.example.lab5_20220270.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.lab5_20220270.model.Course;
import com.example.lab5_20220270.storage.PreferencesManager;

import java.util.ArrayList;
import java.util.List;

public class CoursesViewModel extends AndroidViewModel {
    private final MutableLiveData<List<Course>> courses = new MutableLiveData<>();
    private final PreferencesManager preferencesManager;

    public CoursesViewModel(@NonNull Application application) {
        super(application);
        preferencesManager = new PreferencesManager(application);
        List<Course> loaded = preferencesManager.getCourses();
        if (loaded == null) loaded = new ArrayList<>();
        courses.setValue(loaded);
    }

    public LiveData<List<Course>> getCourses() {
        return courses;
    }

    public void addCourse(Course c) {
        List<Course> list = courses.getValue();
        if (list == null) list = new ArrayList<>();
        list.add(c);
        courses.setValue(list);
        preferencesManager.saveCourses(list);
    }

    public void updateCourse(Course c) {
        List<Course> list = courses.getValue();
        if (list == null) return;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(c.getId())) {
                list.set(i, c);
                break;
            }
        }
        courses.setValue(list);
        preferencesManager.saveCourses(list);
    }

    public void removeCourse(String id) {
        List<Course> list = courses.getValue();
        if (list == null) return;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(id)) {
                list.remove(i);
                break;
            }
        }
        courses.setValue(list);
        preferencesManager.saveCourses(list);
    }
}
