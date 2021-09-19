package com.example.huji_assistant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.huji_assistant.databinding.FragmentCoursesBinding;
import com.example.huji_assistant.databinding.FragmentMycoursesBinding;

import java.util.ArrayList;

public class MyCoursesFragment extends Fragment {

    private ViewModelApp viewModelApp;
    FragmentMycoursesBinding binding;

    public CourseItemHolder holder = null;
    LinearLayoutManager coordinatorLayout;
    public CoursesAdapter adapter = null;
    public LocalDataBase dataBase = null;
    RecyclerView recyclerViewMyCourses;

    public interface endRegistrationButtonClickListener{
        public void onEndRegistrationBtnClicked();
    }

    public MyCoursesFragment(){
        super(R.layout.fragment_mycourses);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMycoursesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerViewMyCourses = view.findViewById(R.id.recycleViewMyCourses);
        adapter = new CoursesAdapter(getContext());

        if (holder == null) {
            holder = new CourseItemHolder(recyclerViewMyCourses);
        }

        ArrayList<Course> courseItems = new ArrayList<>(); // Saves the current courses list

        // todo add demo courses
        Course infiC = new Course("אינפי", "0");
        Course linearitC = new Course("לינארית", "1");
        Course cC = new Course("סי", "2");
        courseItems.add(infiC);
        courseItems.add(linearitC);
        courseItems.add(cC);

        // Create the adapter
        adapter.addCoursesListToAdapter(courseItems);
        recyclerViewMyCourses.setAdapter(adapter);

        coordinatorLayout = new LinearLayoutManager(getContext(),
                RecyclerView.VERTICAL, false);

        recyclerViewMyCourses.setLayoutManager(new LinearLayoutManager(getContext(),
                RecyclerView.VERTICAL, false));

    }
}
