package com.example.lab5_20220270.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab5_20220270.R;
import com.example.lab5_20220270.model.Course;

import java.text.DateFormat;
import java.util.List;

public class CoursesAdapter extends RecyclerView.Adapter<CoursesAdapter.CourseViewHolder> {
    private List<Course> list;
    private final OnItemActionListener listener;

    public interface OnItemActionListener {
        void onItemClick(Course course);
        void onItemDelete(Course course);
    }

    public CoursesAdapter(List<Course> list, OnItemActionListener listener) {
        this.list = list;
        this.listener = listener;
    }

    public void setList(List<Course> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course c = list.get(position);
        holder.textCourseName.setText(c.getName());
        holder.textCourseType.setText(c.getType());
        String freq = "Cada " + c.getFrequencyValue() + " " + ("HOURS".equals(c.getFrequencyUnit())? "horas" : "días");
        holder.textFrequency.setText(freq);
        String next = "Próx: " + DateFormat.getDateTimeInstance().format(c.getNextSessionMillis());
        holder.textNextSession.setText(next);
        holder.itemView.setOnClickListener(v -> listener.onItemClick(c));
        holder.btnDelete.setOnClickListener(v -> listener.onItemDelete(c));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView textCourseName;
        TextView textCourseType;
        TextView textNextSession;
        TextView textFrequency;
        android.widget.ImageButton btnDelete;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            textCourseName = itemView.findViewById(R.id.textCourseName);
            textCourseType = itemView.findViewById(R.id.textCourseType);
            textNextSession = itemView.findViewById(R.id.textNextSession);
            textFrequency = itemView.findViewById(R.id.textFrequency);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
