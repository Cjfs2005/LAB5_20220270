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

        // Date picker for next session date
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

        // Time picker for next session time
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

            // Combine date and time into a single millis
            Calendar combined = Calendar.getInstance();
            combined.setTimeInMillis(selectedDateMillis);
            combined.set(Calendar.HOUR_OF_DAY, selectedHour);
            combined.set(Calendar.MINUTE, selectedMinute);
            combined.set(Calendar.SECOND, 0);
            combined.set(Calendar.MILLISECOND, 0);
            long nextMillis = combined.getTimeInMillis();

            long now = System.currentTimeMillis();
            if (nextMillis < now + 60_000L) { // allow starting from next minute
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
