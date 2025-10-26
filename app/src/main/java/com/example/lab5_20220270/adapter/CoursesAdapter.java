package com.example.lab5_20220270.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab5_20220270.R;
import com.example.lab5_20220270.model.Course;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.List;

public class CoursesAdapter extends RecyclerView.Adapter<CoursesAdapter.CourseViewHolder> {

/*
Modelo: GPT-5 (mediante Github Copilot para brindarle contexto del proyecto)
Prompt: Eres un programador Android en Java. Implementa un Adapter llamado para un RecyclerView que muestre una lista de `Course` con las siguientes reglas:
 - Mostrar el nombre del curso y su tipo.
 - Mostrar la próxima sesión con el texto exacto: "Próxima sesión: dd/MM/yyyy HH:mm" usando el formato `dd/MM/yyyy HH:mm`.
 - Mostrar la frecuencia con el texto exacto: "Frecuencia: Cada X horas" o "Frecuencia: Cada X días" según la unidad almacenada en el `Course`.
 - Incluir un botón de eliminar a la derecha que invoque `onItemDelete(Course)` en un listener pasado al Adapter, y mantener `onItemClick(Course)` para clicks en el item.
 - Mantener un método `setList(List<Course>)` para actualizar datos y refrescar la vista.

Correcciones: Al integrar el código generado fue necesario:
 - Importar y usar `SimpleDateFormat` y `Locale.getDefault()` para formateo consistente.
 - Asegurarse de que `Course.getNextSessionMillis()` devuelve un `long` y formatearlo.
 - Vincular correctamente los ids de la vista (`textCourseName`, `textCourseType`, `textNextSession`, `textFrequency`, `btnDelete`) con el ViewHolder.
 - Mantener las cadenas de UI si se desea internacionalizar posteriormente en `strings.xml`.
*/
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
        String freqUnit = "HOURS".equals(c.getFrequencyUnit()) ? "horas" : "días";
        String freq = "Frecuencia: Cada " + c.getFrequencyValue() + " " + freqUnit;
        holder.textFrequency.setText(freq);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String next = "Próxima sesión: " + sdf.format(c.getNextSessionMillis());
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
