package com.example.huji_assistant;

import android.view.View;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CourseItemHolder extends RecyclerView.ViewHolder  {

    protected ArrayList<Course> coursesList;

    LocalDataBase dataBase;
    TextView name;
    TextView number;
    TextView type;
    CardView cardView;

    public CourseItemHolder(View view) {
        super(view);
        this.coursesList = new ArrayList<>();
        dataBase = HujiAssistentApplication.getInstance().getDataBase();
        name = view.findViewById(R.id.courseNameHolder);
        number = view.findViewById(R.id.courseNumberHolder);
        type = view.findViewById(R.id.courseTypeHolder);
        cardView = view.findViewById(R.id.courseitemcard);

    }

    public ArrayList<Course> getCurrentItems() {
        return this.coursesList;
    }

}
