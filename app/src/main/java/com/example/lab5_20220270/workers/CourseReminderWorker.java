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
import com.example.lab5_20220270.storage.PreferencesManager;
import com.example.lab5_20220270.model.Course;

import java.util.List;
import java.util.ArrayList;

/*
Modelo: GPT-5 (mediante Github Copilot para brindarle contexto del proyecto)
Prompt: Eres un programador de aplicaciones Android en Java. Crea un Worker que al ejecutarse:
 - Envíe una notificación para el curso identificado por `course_id`.
 - Lea el objeto `Course` persistido (PreferencesManager) y use su `nextSessionMillis` como base.
 - Calcule la siguiente sesión sumando la frecuencia configurada (solo HOURS o DAYS) a la `nextSessionMillis` almacenada.
 - Si la siguiente sesión calculada queda en el pasado, avanza en bucle hasta que quede en el futuro.
 - Persista el nuevo `nextSessionMillis` en la lista de cursos (PreferencesManager.saveCourses).
 - Encole un OneTimeWorkRequest para la siguiente ejecución con tag igual al courseId.
 - Use un id de notificación estable derivado del `courseId` para que las notificaciones del mismo curso se reemplacen.

Correcciones: Se tuvo que adaptar el código generado por la IA para:
 - Añadir y ajustar imports (`PreferencesManager`, `Course`, `ArrayList`, `List`).
 - Evitar caso extremo de `Integer.MIN_VALUE` al calcular el id de notificación.
 - Remover soporte a minutos si existía y garantizar que la suma de tiempos use TimeUnit.HOURS/DAYS.
 - Persistir la lista completa actualizada con `prefs.saveCourses(newList)`.
 - Asegurar que el cálculo de nextMillis avance si hay ejecuciones perdidas.
*/

public class CourseReminderWorker extends Worker {

    public CourseReminderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
    Data input = getInputData();
    String courseId = input.getString("course_id");

    PreferencesManager prefs = new PreferencesManager(getApplicationContext());
    List<Course> courses = prefs.getCourses();
    Course target = null;
    if (courses != null) {
        for (Course cc : courses) {
            if (cc.getId().equals(courseId)) {
                target = cc;
                break;
            }
        }
    }

    String courseName = (target != null) ? target.getName() : input.getString("course_name");
    if (courseName == null) courseName = "Mi curso";
    String action = (target != null) ? target.getActionSuggestion() : input.getString("action");
    if (action == null) action = "Revisar apuntes";
    String type = (target != null) ? target.getType() : input.getString("type");
    if (type == null) type = "Teórico";
    int freqValue = (target != null) ? target.getFrequencyValue() : input.getInt("frequency_value", 24);
    String freqUnit = (target != null) ? target.getFrequencyUnit() : input.getString("frequency_unit");
    if (freqUnit == null) freqUnit = "HOURS";

        String channel = NotificationHelper.CHANNEL_THEORETICAL;
        if ("Laboratorio".equalsIgnoreCase(type)) channel = NotificationHelper.CHANNEL_LAB;
        else if ("Electivo".equalsIgnoreCase(type)) channel = NotificationHelper.CHANNEL_ELECTIVE;
        else if ("Otro".equalsIgnoreCase(type)) channel = NotificationHelper.CHANNEL_OTHER;

        int notificationId;
        if (courseId != null) {
            notificationId = Math.abs(courseId.hashCode());
            if (notificationId == Integer.MIN_VALUE) notificationId = Math.abs(notificationId + 1);
        } else {
            notificationId = (int) (System.currentTimeMillis() & 0x7fffffff);
        }

        NotificationHelper.sendNotification(getApplicationContext(), channel, notificationId, courseName, action, R.drawable.ic_launcher_foreground);

        long addMillis = 0L;
        if ("HOURS".equalsIgnoreCase(freqUnit)) {
            addMillis = TimeUnit.HOURS.toMillis(freqValue);
        } else {
            addMillis = TimeUnit.DAYS.toMillis(freqValue);
        }

        long baseNext = (target != null) ? target.getNextSessionMillis() : System.currentTimeMillis();
        long nextMillis = baseNext + addMillis;
        long now = System.currentTimeMillis();
        while (nextMillis <= now) {
            nextMillis += addMillis;
        }

        if (target != null) {
            target.setNextSessionMillis(nextMillis);
            List<Course> newList = courses != null ? courses : new ArrayList<>();
            for (int i = 0; i < newList.size(); i++) {
                if (newList.get(i).getId().equals(target.getId())) {
                    newList.set(i, target);
                    break;
                }
            }
            prefs.saveCourses(newList);
        }

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
