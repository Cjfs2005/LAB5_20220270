package com.example.lab5_20220270;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.lab5_20220270.adapter.CoursesAdapter;
import com.example.lab5_20220270.databinding.ActivityCoursesBinding;
import com.example.lab5_20220270.model.Course;
import com.example.lab5_20220270.viewmodel.CoursesViewModel;

import java.util.ArrayList;

public class CoursesActivity extends AppCompatActivity {
    private ActivityCoursesBinding binding;
    private CoursesViewModel viewModel;
    private CoursesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCoursesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(CoursesViewModel.class);

        adapter = new CoursesAdapter(new ArrayList<>(), new CoursesAdapter.OnItemActionListener() {
            @Override
            public void onItemClick(Course course) {
                // TODO: abrir detalle/editar
            }

            @Override
            public void onItemDelete(Course course) {
                new androidx.appcompat.app.AlertDialog.Builder(CoursesActivity.this)
                        .setTitle("Eliminar curso")
                        .setMessage("¿Desea eliminar el curso '" + course.getName() + "'? Esta acción no se puede deshacer.")
                        .setNegativeButton("Cancelar", (d, w) -> d.dismiss())
                        .setPositiveButton("Eliminar", (d, w) -> {
                            com.example.lab5_20220270.scheduler.CourseScheduler.cancelCourseReminder(CoursesActivity.this, course.getId());
                            viewModel.removeCourse(course.getId());
                        }).show();
            }
        });

        binding.recyclerViewCourses.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewCourses.setAdapter(adapter);

        viewModel.getCourses().observe(this, courses -> adapter.setList(courses));

        binding.fabAddCourse.setOnClickListener(v -> {
            Intent i = new Intent(CoursesActivity.this, CourseCreateActivity.class);
            startActivity(i);
        });
    }
}
