package com.example.huji_assistant.Fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.huji_assistant.Faculty;
import com.example.huji_assistant.HujiAssistentApplication;
import com.example.huji_assistant.LocalDataBase;
import com.example.huji_assistant.R;
import com.example.huji_assistant.StudentInfo;
import com.example.huji_assistant.ViewModelAppMainScreen;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.ArrayList;
import java.util.HashMap;

public class GradeFragment extends Fragment {

    private ViewModelAppMainScreen viewModelAppGradeScreen;
    private LocalDataBase db = null;
    FirebaseFirestore firebaseInstancedb = FirebaseFirestore.getInstance();
    FirebaseFirestoreSettings settings;

    public GradeFragment(){
        super(R.layout.fragment_grade);
    }
    String courseName;
    String courseNumber;
    TextView gradeInput;
    Button approveGradeBtn;
    StudentInfo currentStudent;
    boolean isEmailValid = false;

    public interface sendEmailBtnListener{
        public void onSendEmailBtnClicked(String email);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModelAppGradeScreen = new ViewModelProvider(requireActivity()).get(ViewModelAppMainScreen.class);
        gradeInput = view.findViewById(R.id.gradetextview);
        approveGradeBtn = view.findViewById(R.id.approveGradeBtn);
        if (db == null) {
            db = HujiAssistentApplication.getInstance().getDataBase();
        }
        settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();

        viewModelAppGradeScreen.get().observe(getViewLifecycleOwner(), item -> {
                    courseName = item.getName();
                    courseNumber = item.getNumber();
                });

        approveGradeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String grade = gradeInput.getText().toString();
                // todo maybe move field to info course fragment
                // todo validation check
                // todo verify that the course exists in the list of courses
                if (!courseNumber.equals("")) {
                    //db.addGradeToCourse(grade, courseNumber);
                    System.out.println("attempt to save grade: " + grade + " to course: " + courseNumber);
                    String studentsCollection = "students";

                    currentStudent = db.getCurrentStudent();

                    HashMap<String, String> grades = new HashMap<>();
                    grades.put(courseNumber, grade);

                    currentStudent.setCoursesGrades(grades);

                    // todo move this to db
                    // todo save in db later upload
                    firebaseInstancedb.collection(studentsCollection).document(currentStudent.getId())
                            .set(currentStudent).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                }
                            });
                }
            }
        });
    }
}
