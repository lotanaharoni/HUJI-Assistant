package com.example.huji_assistant;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CourseItemHolder extends RecyclerView.ViewHolder {

    protected ArrayList<Course> coursesList;

    LocalDataBase dataBase;
    TextView name;
    TextView number;

    public CourseItemHolder(View view) {
        super(view);
        this.coursesList = new ArrayList<>();
        dataBase = HujiAssistentApplication.getInstance().getDataBase();
        name = view.findViewById(R.id.courseNameHolder);
        number = view.findViewById(R.id.courseNumberHolder);
    }

    public ArrayList<Course> getCurrentItems() {
        return this.coursesList;
    }

}
