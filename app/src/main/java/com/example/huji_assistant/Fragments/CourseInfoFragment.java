package com.example.huji_assistant.Fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.huji_assistant.AfterCourseAdapter;
import com.example.huji_assistant.AfterCourseItemHolder;
import com.example.huji_assistant.Course;
import com.example.huji_assistant.CourseItemHolder;
import com.example.huji_assistant.CoursesAdapter;
import com.example.huji_assistant.HujiAssistentApplication;
import com.example.huji_assistant.KdamCourseItemHolder;
import com.example.huji_assistant.KdamCoursesAdapter;
import com.example.huji_assistant.KdamOrAfterCourse;
import com.example.huji_assistant.LocalDataBase;
import com.example.huji_assistant.R;
import com.example.huji_assistant.ViewModelAppMainScreen;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CourseInfoFragment extends Fragment {

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
    public KdamCourseItemHolder kdamHolder = null;
    public AfterCourseItemHolder afterHolder = null;
    public KdamCoursesAdapter kdamCoursesAdapter = null;
    public AfterCourseAdapter afterCoursesAdapter = null;

    private LocalDataBase db = null;
    FirebaseFirestore firebaseInstancedb = FirebaseFirestore.getInstance();
    RecyclerView recyclerViewKdamCourses;
    RecyclerView recyclerViewAfterCourses;
    FirebaseFirestoreSettings settings;
    LinearLayoutManager coordinatorLayout1;
    LinearLayoutManager coordinatorLayout2;
    String ROOT_COLLECTION = "coursesTestOnlyCs";
    TextView noKdamCoursesTextView;
    TextView noAfterCoursesTextView;

    public CourseInfoFragment(){
        super(R.layout.courseinfo_fragment);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModelAppMainScreen = new ViewModelProvider(requireActivity()).get(ViewModelAppMainScreen.class);
        courseNameTextView = view.findViewById(R.id.courseInfoName);
        courseNumberTextView = view.findViewById(R.id.courseInfoNumber);
        courseTypeTextView = view.findViewById(R.id.courseInfoType1);
        coursePointsTextView = view.findViewById(R.id.courseInfoPoints1);
        courseYearTextView = view.findViewById(R.id.courseInfoYear1);
        courseSemesterTextView = view.findViewById(R.id.courseInfoSemester1);

      //  TextView textView = requireActivity().findViewById(R.id.change_language_textView);
      //  textView.setVisibility(View.INVISIBLE);

        recyclerViewKdamCourses = view.findViewById(R.id.kdamcoursessrecycleview);
        kdamCoursesAdapter = new KdamCoursesAdapter(getContext());
        noKdamCoursesTextView = view.findViewById(R.id.noKdamCoursesTextView);
        noAfterCoursesTextView = view.findViewById(R.id.noAfterCoursesTextView);

        afterCoursesAdapter = new AfterCourseAdapter(getContext());
        recyclerViewAfterCourses = view.findViewById(R.id.aftercoursessrecycleview);


        if (kdamHolder == null) {
            kdamHolder = new KdamCourseItemHolder(recyclerViewKdamCourses);
        }

        if (afterHolder == null) {
            afterHolder = new AfterCourseItemHolder(recyclerViewAfterCourses);
        }

        if (db == null) {
            db = HujiAssistentApplication.getInstance().getDataBase();
        }

        settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firebaseInstancedb.setFirestoreSettings(settings);

        viewModelAppMainScreen.get().observe(getViewLifecycleOwner(), item -> {
            courseName = item.getName();
            courseNumber = item.getNumber();
            coursePoints = item.getPoints();
            type = item.getType();
            semester = item.getSemester();
            year = item.getYear();
            String nametext = courseNameTextView.getText() + " " + courseName;
            courseNameTextView.setText(nametext);
            String numbertext = courseNumberTextView.getText() + " " + courseNumber;
            courseNumberTextView.setText(numbertext);
            String coursetypetext = courseTypeTextView.getText() + ": " + type;
            courseTypeTextView.setText(coursetypetext);
            String coursepointstext = coursePointsTextView.getText() + ": " + coursePoints;
            coursePointsTextView.setText(coursepointstext);
            String courseyearstext= courseYearTextView.getText() + ": " + year;
            courseYearTextView.setText(courseyearstext);
            String coursesemesterstext = courseSemesterTextView.getText() + ": " + semester;
            courseSemesterTextView.setText(coursesemesterstext);
            System.out.println("reached view model33");
            getKdamCourses();
            getAfterCourses();

        });

        // Get kdam courses
    }

    public void getKdamCourses(){

        System.out.println("chug33: " + db.getCurrentStudent().getChugId());// bug - no current student yet
        System.out.println("maslul33: " + db.getCurrentStudent().getMaslulId());
        System.out.println("course33: " + courseNumber);

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
                                // retrieve for each chug id it's name
                                KdamOrAfterCourse course = document1.toObject(KdamOrAfterCourse.class);
                                assert course != null;
                                // String courseTitle = course.getName();
                                // String courseNumber = course.getNumber();
                                // String coursePoints = course.getPoints();
                                System.out.println("kdam course: " + course.getNumber() + " " + course.getName());
                                kdamCourses.add(course);
                            }

                            kdamCoursesAdapter.addKdamCoursesListToAdapter(kdamCourses);
                            recyclerViewKdamCourses.setAdapter(kdamCoursesAdapter);

                            coordinatorLayout1 = new LinearLayoutManager(getContext(),
                                    RecyclerView.VERTICAL, false);

                            recyclerViewKdamCourses.setLayoutManager(new LinearLayoutManager(getContext(),
                                    RecyclerView.VERTICAL, false));

                            if (kdamCourses.size() == 0){
                                noKdamCoursesTextView.setVisibility(View.VISIBLE);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            noKdamCoursesTextView.setVisibility(View.VISIBLE);
                            System.out.println("failed to get kdam courses1");
                        }
                    });
        }
        catch (Exception e){
            noKdamCoursesTextView.setVisibility(View.VISIBLE);
            System.out.println("failed to get kdam courses2");
        }
    }

    public void getAfterCourses(){
        System.out.println("chug: " + db.getCurrentStudent().getChugId());
        System.out.println("maslul: " + db.getCurrentStudent().getMaslulId());
        System.out.println("course: " + courseNumber);

        try {
            Task<QuerySnapshot> document = firebaseInstancedb.collection(ROOT_COLLECTION).document(db.getCurrentStudent().getChugId())
                    .collection("maslulimInChug").document(db.getCurrentStudent().getMaslulId()).collection("coursesInMaslul")
                    .document(courseNumber)
                    .collection("afterCourses")
                    .get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> documents = task.getResult().getDocuments();
                            ArrayList<KdamOrAfterCourse> afterCourses = new ArrayList<>();

                            for (DocumentSnapshot document1 : documents) {
                                // retrieve for each chug id it's name
                                KdamOrAfterCourse course = document1.toObject(KdamOrAfterCourse.class);
                                assert course != null;
                                // String courseTitle = course.getName();
                                // String courseNumber = course.getNumber();
                                // String coursePoints = course.getPoints();
                                System.out.println("kdam course: " + course.getNumber() + " " + course.getName());
                                afterCourses.add(course);
                            }
                            // todo change matod name
                            afterCoursesAdapter.addKdamCoursesListToAdapter(afterCourses);
                            recyclerViewAfterCourses.setAdapter(afterCoursesAdapter);

                            coordinatorLayout2 = new LinearLayoutManager(getContext(),
                                   RecyclerView.VERTICAL, false);

                            recyclerViewAfterCourses.setLayoutManager(new LinearLayoutManager(getContext(),
                                    RecyclerView.VERTICAL, false));

                            if (afterCourses.size() == 0){
                                noAfterCoursesTextView.setVisibility(View.VISIBLE);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            noAfterCoursesTextView.setVisibility(View.VISIBLE);
                            System.out.println("failed to get after courses1");
                        }
                    });
        }
        catch (Exception e){
            noAfterCoursesTextView.setVisibility(View.VISIBLE);
            System.out.println("failed to get after courses2");
        }
    }
}
