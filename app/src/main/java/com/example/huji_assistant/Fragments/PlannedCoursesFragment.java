package com.example.huji_assistant.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.huji_assistant.HujiAssistentApplication;
import com.example.huji_assistant.LocalDataBase;
import com.example.huji_assistant.PlanCoursesAdapter;
import com.example.huji_assistant.PlanCourseItemHolder;
import com.example.huji_assistant.R;
import com.example.huji_assistant.StudentInfo;
import com.example.huji_assistant.ViewModelAppMainScreen;
import com.example.huji_assistant.databinding.FragmentPlancoursesBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.ArrayList;

public class PlannedCoursesFragment extends Fragment {

    private ViewModelAppMainScreen viewModelAppGradeScreen;
    private LocalDataBase dataBase = null;
    FirebaseFirestore firebaseInstancedb = FirebaseFirestore.getInstance();
    FirebaseFirestoreSettings settings;
    FragmentPlancoursesBinding binding;
    private ViewModelAppMainScreen viewModelAppMainScreen;

    public PlannedCoursesFragment(){
        super(R.layout.fragment_plannedcourses);
    }

    public PlanCourseItemHolder holder = null;
    LinearLayoutManager coordinatorLayout;
    public PlanCoursesAdapter adapter = null;
  // public CoursesAdapter.OnCheckBoxClickListener onCheckBoxClickListener = null;
  //  public CoursesAdapter.AddGradeListener addGradeListener = null;
    RecyclerView recyclerViewPlannedCourses;
    SearchView searchView;
    AutoCompleteTextView autocompletechoosetype;
    ArrayList<String> coursesId = new ArrayList<>();
  // public CoursesAdapter.OnItemClickListener onItemClickListener = null;
  //  public CoursesAdapter.DeleteClickListener deleteClickListener = null;
   // FirebaseFirestore firebaseInstancedb = FirebaseFirestore.getInstance();
//    FirebaseFirestore firebaseInstancedb = HujiAssistentApplication.getInstance().getDataBase().getFirestoreDB();

    TextView studentNameTextView;
    TextView facultyTextView;
    TextView chugTextView;
    TextView maslulTextView;
    TextView degreeTextView;
    TextView yearTextView;
    TextView textViewTotalPoints;
    TextView textViewTotalPointsDegree;
    TextView textViewTotalHovaPoints;
    TextView textViewTotalHovaChoosePoints;
    TextView textViewTotalChoosePoints;
    TextView textViewTotalSuppPoints;
    TextView textViewTotalCornerStonePoints;
    TextView averageTxt;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPlancoursesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
      //  viewModelAppGradeScreen = new ViewModelProvider(requireActivity()).get(ViewModelAppMainScreen.class);
        viewModelAppMainScreen = new ViewModelProvider(requireActivity()).get(ViewModelAppMainScreen.class);
        recyclerViewPlannedCourses = view.findViewById(R.id.recycleViewPlannedCourses2);
        adapter = new PlanCoursesAdapter(getContext());
        FloatingActionButton addCourseBtn = view.findViewById(R.id.addCourseBtn);
        androidx.appcompat.widget.SearchView searchView = view.findViewById(R.id.search1);
        averageTxt = view.findViewById(R.id.textViewDegreeAverage);

        if (dataBase == null){
            dataBase = HujiAssistentApplication.getInstance().getDataBase();
        }

        settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firebaseInstancedb.setFirestoreSettings(settings);

        StudentInfo currentStudent = dataBase.getCurrentUser();
        studentNameTextView = view.findViewById(R.id.nameOfStudentTextView);
        facultyTextView = view.findViewById(R.id.nameOfFaculty);
        chugTextView = view.findViewById(R.id.nameOfChug);
        maslulTextView = view.findViewById(R.id.nameOfMaslul);
        degreeTextView = view.findViewById(R.id.nameOfDegree);
        yearTextView = view.findViewById(R.id.yearOfDegree);
        autocompletechoosetype = view.findViewById(R.id.autocompletechoosetype1);

        // Update data on screen
        /**
        String name = studentNameTextView.getText() +" " + currentStudent.getPersonalName() + " " + currentStudent.getFamilyName();
        studentNameTextView.setText(name);

        String faculty = facultyTextView.getText() + " " + currentStudent.getFacultyId() + " " + dataBase.getCurrentFaculty().getTitle();
        facultyTextView.setText(faculty);

        String chug = chugTextView.getText() + " " + currentStudent.getChugId() + " " + dataBase.getCurrentChug().getTitle();
        chugTextView.setText(chug);

        String maslul = maslulTextView.getText() + " " + currentStudent.getMaslulId() + " " + dataBase.getCurrentMaslul().getTitle();
        maslulTextView.setText(maslul);

        String degree = currentStudent.getDegree();
        if (degree != null){
            degree = degreeTextView.getText() + " " + currentStudent.getDegree();
            degreeTextView.setText(degree);
        }

        String year = currentStudent.getYear();
        if (year != null) {
            year = yearTextView.getText() + " " + currentStudent.getYear();
            yearTextView.setText(year);
        }*/

    }

}
