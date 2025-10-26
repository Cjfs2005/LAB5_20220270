package com.example.lab5_20220270;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import android.view.MenuItem;

import com.example.lab5_20220270.databinding.ActivityCreateCourseBinding;
import com.example.lab5_20220270.model.Course;
import com.example.lab5_20220270.viewmodel.CoursesViewModel;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.UUID;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Locale;

/*
Modelo: GPT-5 (mediante Github Copilot para brindarle contexto del proyecto)
Prompt: Eres un programador de aplicaciones para Android en Java y necesitas implementar la pantalla de creación de cursos. Requisitos:
 - Usar Material TextInputLayout + TextInputEditText para los campos con label persistente.
 - Incluir MaterialDatePicker y MaterialTimePicker (separados) para que el usuario seleccione la fecha y hora de la próxima sesión.
 - Validar que la fecha/hora combinada sea al menos 1 minuto en el futuro.
 - Eliminar la opción de 'Minutos' en la frecuencia y aceptar sólo 'Horas' o 'Días' con valor entero >= 1.
 - Al guardar, construir un objeto Course con `nextSessionMillis` igual a la fecha/hora seleccionada, persistir con ViewModel/Preferences y programar el primer recordatorio con CourseScheduler.
 - Mostrar fecha en formato dd/MM/yyyy y hora HH:mm en los campos.

Correcciones: Se tuvo que ajustar lo generado por la IA para:
 - Añadir imports de Material pickers y TimeFormat.
 - Crear variables auxiliares (`selectedDateMillis`, `selectedHour`, `selectedMinute`) y formateadores (`SimpleDateFormat`).
 - Ajustar los ids de binding y el layout (TextInputLayout/TextInputEditText) para que coincidan con el binding generado.
 - Asegurar que el valor `nextSessionMillis` se combine correctamente y que la validación permita al menos 1 minuto en el futuro.
 - Manejar el caso donde el usuario no selecciona fecha/hora mostrando un Toast y bloqueando el guardado.
 - Comprobar dependencias en `build.gradle` para Material Components.
*/

public class CourseCreateActivity extends AppCompatActivity {
    private ActivityCreateCourseBinding binding;
    private CoursesViewModel viewModel;
    private long selectedDateMillis = -1;
    private int selectedHour = -1;
    private int selectedMinute = -1;
    private final SimpleDateFormat dateFmt = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private final SimpleDateFormat timeFmt = new SimpleDateFormat("HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateCourseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(CoursesViewModel.class);

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Crear curso");
        }

        String[] types = new String[]{"Teórico", "Laboratorio", "Electivo", "Otro"};
        binding.spinnerType.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, types));

        binding.editTextNextDate.setOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Seleccione la fecha")
                    .build();
            datePicker.addOnPositiveButtonClickListener(selection -> {
                selectedDateMillis = selection;
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(selectedDateMillis);
                binding.editTextNextDate.setText(dateFmt.format(c.getTime()));
            });
            datePicker.show(getSupportFragmentManager(), "date_picker");
        });

        binding.editTextNextTime.setOnClickListener(v -> {
            MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(12)
                    .setMinute(0)
                    .setTitleText("Seleccione la hora")
                    .build();
            timePicker.addOnPositiveButtonClickListener(view -> {
                selectedHour = timePicker.getHour();
                selectedMinute = timePicker.getMinute();
                Calendar t = Calendar.getInstance();
                t.set(Calendar.HOUR_OF_DAY, selectedHour);
                t.set(Calendar.MINUTE, selectedMinute);
                binding.editTextNextTime.setText(timeFmt.format(t.getTime()));
            });
            timePicker.show(getSupportFragmentManager(), "time_picker");
        });

        binding.buttonSaveCourse.setOnClickListener(v -> {
            String name = binding.editTextCourseName.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, "Ingrese el nombre del curso", Toast.LENGTH_SHORT).show();
                return;
            }
            String type = binding.spinnerType.getSelectedItem().toString();
            String freqText = binding.editTextFrequency.getText().toString().trim();
            int freq = freqText.isEmpty() ? 1 : Integer.parseInt(freqText);
            if (freq < 1) {
                Toast.makeText(this, "La frecuencia mínima es 1", Toast.LENGTH_SHORT).show();
                return;
            }
            String unit = binding.radioHours.isChecked() ? "HOURS" : "DAYS";

            if (selectedDateMillis < 0 || selectedHour < 0 || selectedMinute < 0) {
                Toast.makeText(this, "Seleccione fecha y hora de la próxima sesión", Toast.LENGTH_SHORT).show();
                return;
            }

            Calendar combined = Calendar.getInstance();
            combined.setTimeInMillis(selectedDateMillis);
            combined.set(Calendar.HOUR_OF_DAY, selectedHour);
            combined.set(Calendar.MINUTE, selectedMinute);
            combined.set(Calendar.SECOND, 0);
            combined.set(Calendar.MILLISECOND, 0);
            long nextMillis = combined.getTimeInMillis();

            long now = System.currentTimeMillis();
            if (nextMillis < now + 60_000L) {
                Toast.makeText(this, "La fecha/hora debe ser al menos 1 minuto en el futuro", Toast.LENGTH_SHORT).show();
                return;
            }

            Course c = new Course(UUID.randomUUID().toString(), name, type, freq, unit, nextMillis, "Revisar apuntes");
            viewModel.addCourse(c);
            com.example.lab5_20220270.scheduler.CourseScheduler.scheduleCourseReminder(this, c);
            finish();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
