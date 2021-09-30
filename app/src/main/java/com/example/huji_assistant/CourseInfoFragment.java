package com.example.huji_assistant;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CourseInfoFragment extends Fragment {

    private ViewModelAppMainScreen viewModelAppMainScreen;
    String courseName;
    String courseNumber;
    String coursePoints;
    TextView courseNameTextView;
    TextView courseNumberTextView;

    public CourseInfoFragment(){
        super(R.layout.courseinfo_fragment);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModelAppMainScreen = new ViewModelProvider(requireActivity()).get(ViewModelAppMainScreen.class);
        courseNameTextView = view.findViewById(R.id.courseInfoName);
        courseNumberTextView = view.findViewById(R.id.courseInfoNumber);

        viewModelAppMainScreen.get().observe(getViewLifecycleOwner(), item -> {
            courseName = item.getName();
            courseNumber = item.getNumber();
         //   coursePoints = item.getPoints();
           // System.out.println("name: " + courseName + " number: " + courseNumber);
            courseNameTextView.setText(courseName);
            courseNumberTextView.setText(courseNumber);
        });
        // todo send here the item to display
    }
}
