package com.example.huji_assistant;

import android.os.Bundle;
import android.view.View;

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

    public CourseInfoFragment(){
        super(R.layout.courseinfo_fragment);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModelAppMainScreen = new ViewModelProvider(requireActivity()).get(ViewModelAppMainScreen.class);

        viewModelAppMainScreen.get().observe(getViewLifecycleOwner(), item -> {
            courseName = item.getName();
            courseNumber = item.getId();
         //   coursePoints = item.getPoints();
            System.out.println("name: " + courseName + " number: " + courseNumber);
        });
        // todo send here the item to display
    }
}
