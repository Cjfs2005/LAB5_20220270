package com.example.lab5_20220270;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.lab5_20220270.databinding.ActivityCreateCourseBinding;
import com.example.lab5_20220270.model.Course;
import com.example.lab5_20220270.viewmodel.CoursesViewModel;

import java.util.UUID;

public class CourseCreateActivity extends AppCompatActivity {
    private ActivityCreateCourseBinding binding;
    private CoursesViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateCourseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(CoursesViewModel.class);

        String[] types = new String[]{"Teórico", "Laboratorio", "Electivo", "Otro"};
        binding.spinnerType.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, types));

        binding.buttonSaveCourse.setOnClickListener(v -> {
            String name = binding.editTextCourseName.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, "Ingrese el nombre del curso", Toast.LENGTH_SHORT).show();
                return;
            }
            String type = binding.spinnerType.getSelectedItem().toString();
            int freq = Integer.parseInt(binding.editTextFrequency.getText().toString().isEmpty() ? "1" : binding.editTextFrequency.getText().toString());
            String unit;
            if (binding.radioMinutes.isChecked()) unit = "MINUTES";
            else unit = binding.radioHours.isChecked() ? "HOURS" : "DAYS";
            Course c = new Course(UUID.randomUUID().toString(), name, type, freq, unit, System.currentTimeMillis(), "Revisar apuntes");
            viewModel.addCourse(c);
            com.example.lab5_20220270.scheduler.CourseScheduler.scheduleCourseReminder(this, c);
            finish();
        });
    }
}
