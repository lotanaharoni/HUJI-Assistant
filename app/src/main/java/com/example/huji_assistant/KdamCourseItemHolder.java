package com.example.huji_assistant;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class KdamCourseItemHolder extends RecyclerView.ViewHolder {

    protected ArrayList<KdamOrAfterCourse> coursesList;

    LocalDataBase dataBase;
    TextView name;
    TextView number;
    TextView group;
    TextView points;
    TextView semester;
    CardView cardView;
    CheckBox checkBox;

    public KdamCourseItemHolder(View view) {
        super(view);
        this.coursesList = new ArrayList<>();
        dataBase = HujiAssistentApplication.getInstance().getDataBase();
        name = view.findViewById(R.id.courseNameHolderKdam);
        number = view.findViewById(R.id.courseNumberHolderKdam);
        group = view.findViewById(R.id.courseGroupHolderKdam);
        points = view.findViewById(R.id.coursepointsKdam);
        semester = view.findViewById(R.id.courseSemesterHolderKdam);
        cardView = view.findViewById(R.id.courseitemkdamcard);
    }

    public ArrayList<KdamOrAfterCourse> getCurrentItems() {
        return this.coursesList;
    }

}
