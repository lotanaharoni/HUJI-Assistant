package com.example.huji_assistant.Fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.huji_assistant.CourseScheduleEntry;
import com.example.huji_assistant.HujiAssistentApplication;
import com.example.huji_assistant.KdamCoursesAdapter;
import com.example.huji_assistant.KdamOrAfterCourse;
import com.example.huji_assistant.LocalDataBase;
import com.example.huji_assistant.R;
import com.example.huji_assistant.ScheduleAdapter;
import com.example.huji_assistant.ViewModelAppMainScreen;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlanCourseInfoFragment extends Fragment {

    private ViewModelAppMainScreen viewModelAppMainScreen;
    String courseName;
    String courseNumber;
    String coursePoints;
    String type;
    String semester;
    String year;
    TextView courseNameTextView;
    TextView courseNumberTextView;
    TextView courseTypeTextView;
    TextView coursePointsTextView;
    TextView courseYearTextView;
    TextView courseSemesterTextView;
    ProgressBar kdamLoadingBar;
    ProgressBar scheduleLoadingBar;
    public ScheduleAdapter scheduleAdapter = null;
    public KdamCoursesAdapter.OnCheckBoxClickListener onCheckBoxClickListener = null;

    private LocalDataBase db = null;
    FirebaseFirestore firebaseInstancedb = FirebaseFirestore.getInstance();
    RecyclerView recyclerViewKdamCourses;
    RecyclerView recyclerViewSchedule;
    FirebaseFirestoreSettings settings;
    LinearLayoutManager coordinatorLayout1;
    LinearLayoutManager coordinatorLayout2;
    String ROOT_COLLECTION = "coursesTestOnlyCs";
    TextView noKdamCoursesTextView;
    KdamCoursesAdapter kdamCoursesAdapter;

    public PlanCourseInfoFragment() {
        super(R.layout.plancourseinfo_fragment);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        courseNameTextView = view.findViewById(R.id.courseInfoName);
        courseNumberTextView = view.findViewById(R.id.courseInfoNumber);
        courseTypeTextView = view.findViewById(R.id.courseInfoType1);
        coursePointsTextView = view.findViewById(R.id.courseInfoPoints1);
        courseYearTextView = view.findViewById(R.id.courseInfoYear1);
        courseSemesterTextView = view.findViewById(R.id.courseInfoSemester1);
        kdamLoadingBar = view.findViewById(R.id.kdamProgressBar);
        scheduleLoadingBar = view.findViewById(R.id.scheduleProgressBar);
        noKdamCoursesTextView = view.findViewById(R.id.noKdamCoursesTextView);
        recyclerViewSchedule = view.findViewById(R.id.aftercoursessrecycleview);

        recyclerViewKdamCourses = view.findViewById(R.id.kdamcoursessrecycleview);
        kdamCoursesAdapter = new KdamCoursesAdapter(getContext());
        viewModelAppMainScreen = new ViewModelProvider(requireActivity()).get(ViewModelAppMainScreen.class);
        scheduleAdapter = new ScheduleAdapter(); // schedule Adapter


        if (db == null) {
            db = HujiAssistentApplication.getInstance().getDataBase();
        }

        settings = new FirebaseFirestoreSettings.Builder().setPersistenceEnabled(true).build();
        firebaseInstancedb.setFirestoreSettings(settings);

        viewModelAppMainScreen.get().observe(getViewLifecycleOwner(), item -> {
            courseName = item.getName();
            courseNumber = item.getNumber();
            coursePoints = item.getPoints();
            type = item.getType();
            semester = item.getSemester();
            year = item.getYear();
            courseNameTextView.setText(courseName);
            courseNumberTextView.setText(courseNumber);
            courseTypeTextView.setText(courseTypeTextView.getText() + ": " + type);
            coursePointsTextView.setText(coursePointsTextView.getText() + ": " + coursePoints);
            courseYearTextView.setText(courseYearTextView.getText() + ": " + year);
            courseSemesterTextView.setText(courseSemesterTextView.getText() + ": " + semester);

            kdamLoadingBar.setVisibility(View.VISIBLE);
            scheduleLoadingBar.setVisibility(View.VISIBLE);


            getKdamCourses();
            getSchedule();

        });
    }

    public void getKdamCourses() {

        try {
            Task<QuerySnapshot> document = firebaseInstancedb.collection(ROOT_COLLECTION).document(db.getCurrentStudent().getChugId())
                    .collection("maslulimInChug").document(db.getCurrentStudent().getMaslulId()).collection("coursesInMaslul")
                    .document(courseNumber)
                    .collection("kdamCourses")
                    .get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> documents = task.getResult().getDocuments();
                            ArrayList<KdamOrAfterCourse> kdamCourses = new ArrayList<>();

                            for (DocumentSnapshot document1 : documents) {
                                KdamOrAfterCourse course = document1.toObject(KdamOrAfterCourse.class);
                                assert course != null;
                                System.out.println("kdam course: " + course.getNumber() + " " + course.getName());
                                kdamCourses.add(course);
                            }
                            kdamLoadingBar.setVisibility(View.INVISIBLE);

                            kdamCoursesAdapter.addKdamCoursesListToAdapter(kdamCourses);
                            recyclerViewKdamCourses.setAdapter(kdamCoursesAdapter);

                            coordinatorLayout1 = new LinearLayoutManager(getContext(),
                                    RecyclerView.VERTICAL, false);

                            recyclerViewKdamCourses.setLayoutManager(new LinearLayoutManager(getContext(),
                                    RecyclerView.VERTICAL, false));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            noKdamCoursesTextView.setVisibility(View.VISIBLE);
                            System.out.println("failed to get kdam courses1");
                        }
                    });
        } catch (Exception e) {
            noKdamCoursesTextView.setVisibility(View.VISIBLE);
            System.out.println("failed to get kdam courses2");
        }
    }


    public void getSchedule() {
        // TODO: this is really ugly code and need to be fixed, the FireStore Database Schedule section need to be rebuilt
        ArrayList<String> types = new ArrayList<>(Arrays.asList("שעור", "תרגיל", "סמינר", "מעבדה", "סמינר ושעור", "סמינר ותרגול"));
        ArrayList<String> groups = new ArrayList<>(Arrays.asList("א'", "ב'", "ג'", "ד'", "ה'", "ו'", "ז'", "ח'", "ט'", "י'"));
        ArrayList<String> days = new ArrayList<>(Arrays.asList("א", "ב", "ג", "ד", "ה", "ו"));
//        scheduleLoadingBar.setVisibility(View.VISIBLE);

        ArrayList<CourseScheduleEntry> coursesSchedule = new ArrayList<>();


        for (String type : types) {
            for (String group : groups) {
                for (String day : days) {

                    try {
                        firebaseInstancedb.collection(ROOT_COLLECTION).document(db.getCurrentStudent().getChugId())
                                .collection("maslulimInChug").document(db.getCurrentStudent().getMaslulId()).collection("coursesInMaslul")
                                .document(courseNumber)
                                .collection("schedule").document(type).collection(group).document(day)
                                .get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                if (task.getResult().exists()) {
                                    CourseScheduleEntry entry = task.getResult().toObject(CourseScheduleEntry.class);
                                    coursesSchedule.add(entry);
                                    System.out.println(entry.toString());
                                }
                            }

                            scheduleAdapter.addScheduleListToAdapter(coursesSchedule);
                            recyclerViewSchedule.setAdapter(scheduleAdapter);

                            coordinatorLayout2 = new LinearLayoutManager(getContext(),
                                    RecyclerView.VERTICAL, false);

                            recyclerViewSchedule.setLayoutManager(new LinearLayoutManager(getContext(),
                                    RecyclerView.VERTICAL, false));

                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                noKdamCoursesTextView.setVisibility(View.VISIBLE);
                                System.out.println("failed to get kdam courses1");
                            }
                        });
                    } catch (Exception e) {
                        noKdamCoursesTextView.setVisibility(View.VISIBLE);
                        System.out.println("failed to get kdam courses2");
                    }

                }
            }
        }
        scheduleLoadingBar.setVisibility(View.INVISIBLE);
    }
}
