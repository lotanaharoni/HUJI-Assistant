package com.example.huji_assistant.Fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.huji_assistant.Course;
import com.example.huji_assistant.CourseItemHolder;
import com.example.huji_assistant.CoursesAdapter;
import com.example.huji_assistant.HujiAssistentApplication;
import com.example.huji_assistant.LocalDataBase;
import com.example.huji_assistant.PlanCourseAdapter;
import com.example.huji_assistant.PlanCourseItemHolder;
import com.example.huji_assistant.R;
import com.example.huji_assistant.ViewModelApp;
import com.example.huji_assistant.ViewModelAppMainScreen;
import com.example.huji_assistant.databinding.FragmentCoursesBinding;
import com.example.huji_assistant.databinding.FragmentMycoursesBinding;
import com.example.huji_assistant.databinding.FragmentPlancoursesBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.ArrayList;

public class PlanCoursesFragment extends Fragment {

    private ViewModelAppMainScreen viewModelAppMainScreen;
    FirebaseFirestoreSettings settings;
    FragmentPlancoursesBinding binding;
    // private ViewModelApp viewModelApp;
    //  private ViewModelAppMainScreen viewModelAppCourse;

    public PlanCourseItemHolder holder = null;
    public PlanCourseAdapter adapter = null;
    private LocalDataBase db = null;

    public interface endRegistrationButtonClickListener {
        public void onEndRegistrationBtnClicked();
    }

    LinearLayoutManager coordinatorLayout;

    public PlanCourseAdapter.OnCheckBoxClickListener onCheckBoxClickListener = null;
    RecyclerView recyclerViewMyPlanCourses;
    ArrayList<String> coursesId = new ArrayList<>();
    public CoursesAdapter.OnItemClickListener onItemClickListener = null;
    public CoursesAdapter.DeleteClickListener deleteClickListener = null;
    FirebaseFirestore firebaseInstancedb = FirebaseFirestore.getInstance();

    public PlanCoursesFragment() {
        super(R.layout.fragment_plancourses);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //viewModelApp = new ViewModelProvider(requireActivity()).get(ViewModelApp.class);
        recyclerViewMyPlanCourses = view.findViewById(R.id.recycleViewPlanCourses);
        adapter = new PlanCourseAdapter(getContext());

        if (holder == null) {
            holder = new PlanCourseItemHolder(recyclerViewMyPlanCourses);
        }

        ArrayList<Course> courseItems = new ArrayList<>(); // Saves the current courses list

        if (db == null) {
            db = HujiAssistentApplication.getInstance().getDataBase();
        }
        settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firebaseInstancedb.setFirestoreSettings(settings);

        ArrayList<Course> coursesFromFireBase = db.getCoursesFromFireBase(); // all courses in current maslul
        ArrayList<Course> coursesOfCurrentStudent = db.getCoursesOfCurrentStudent(); // all courses of the student (finished and not finished)

        ArrayList<Course> plannedCourses = new ArrayList<>();

        adapter.addCoursesListToAdapter(plannedCourses);//todo
        recyclerViewMyPlanCourses.setAdapter(adapter);

        coordinatorLayout = new LinearLayoutManager(getContext(),
                RecyclerView.VERTICAL, false);

        recyclerViewMyPlanCourses.setLayoutManager(new LinearLayoutManager(getContext(),
                RecyclerView.VERTICAL, false));

    }
}
