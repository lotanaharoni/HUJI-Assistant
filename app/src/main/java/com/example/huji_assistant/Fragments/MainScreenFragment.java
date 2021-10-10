package com.example.huji_assistant.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.huji_assistant.Course;
import com.example.huji_assistant.CoursesAdapter;
import com.example.huji_assistant.HujiAssistentApplication;
import com.example.huji_assistant.LocalDataBase;
import com.example.huji_assistant.R;
import com.example.huji_assistant.ViewModelApp;
import com.example.huji_assistant.ViewModelAppMainScreen;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.ArrayList;

public class MainScreenFragment extends Fragment {

    private ViewModelApp viewModelApp;
    private ActivityResultLauncher<Intent> cameraUploadActivityResultLauncher;

    public interface endRegistrationButtonClickListener{
        public void onEndRegistrationBtnClicked();
    }
    public MainScreenFragment.endRegistrationButtonClickListener newUserBtnListener = null;
    public MainScreenFragment.myCoursesButtonListener myCoursesButtonListenerBtn = null;
    public MainScreenFragment.UploadPictureButtonListener uploadPicturesButtonListenerBtn = null;
    public MainScreenFragment.editInfoButtonListener editInfoButtonListener = null;
    public MainScreenFragment.coursesPlanButtonListenerBtn coursesPlanButtonListenerBtn = null;
    public MainScreenFragment.coursesPlanScreenListenerBtn coursesPlanScreenListenerBtn = null;
    public MainScreenFragment.showAttendanceButtonListener showAttendanceButtonListener = null;
    public LocalDataBase dataBase = null;

    public interface myCoursesButtonListener{
        public void onMyCoursesButtonClicked();
    }

    public interface coursesPlanButtonListenerBtn{
        public void onPlanCoursesButtonClicked();
    }

    public interface coursesPlanScreenListenerBtn{
        public void onPlanCoursesScreenClicked();
    }

    public interface editInfoButtonListener{
        public void onEditInfoButtonClicked();
    }

    public interface showAttendanceButtonListener{
        public void onShowAttendanceButtonClicked();
    }

    public interface onSearchClickListener{
        public void onSearchClick();
    }


    public interface UploadPictureButtonListener{
        public void onUploadPictureButtonClicked();
    }
    public MainScreenFragment(){
        super(R.layout.mainscreen);
    }
    Spinner dropdown;
    Button myCourses;
    Button privateInfoEditBtn;
    Button uploadPicturesButton;
    Button coursesPlanBtn;
    FirebaseFirestoreSettings settings;
    Button showAttendanceButton;
    Button circulumButtonBtn;
    Button showCourseInfo;
    SearchView seachViewMainFragment;
    public onSearchClickListener onSearchClickListener = null;
    String textToSearch;
    FirebaseFirestore firebaseInstancedb = FirebaseFirestore.getInstance();
    private ViewModelAppMainScreen viewModelAppMainScreen;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModelAppMainScreen = new ViewModelProvider(requireActivity()).get(ViewModelAppMainScreen.class);
        viewModelApp = new ViewModelProvider(requireActivity()).get(ViewModelApp.class);
        myCourses = view.findViewById(R.id.myCoursesButton);
        coursesPlanBtn = view.findViewById(R.id.coursesPlan);
        uploadPicturesButton = view.findViewById(R.id.uploadPicturesButton);
        privateInfoEditBtn = view.findViewById(R.id.privateInfoEditButton);
        showAttendanceButton = view.findViewById(R.id.showAttendanceButton);
        circulumButtonBtn = view.findViewById(R.id.circulumButton);
        seachViewMainFragment = view.findViewById(R.id.searchViewMainScreen);
        //androidx.appcompat.widget.SearchView searchView
        showCourseInfo = view.findViewById(R.id.searchBtnMainScreen);
        showCourseInfo.setEnabled(false);

        if (dataBase == null){
            dataBase = HujiAssistentApplication.getInstance().getDataBase();
        }

        settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firebaseInstancedb.setFirestoreSettings(settings);

        FloatingActionButton openCameraFloatingButton = view.findViewById(R.id.open_camera_floating_button);
        viewModelApp.getStudent().observe(getViewLifecycleOwner(), item->{

        });

        showCourseInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isFoundCourse = false;
                Course toFind = null;
                ArrayList<Course> coursesFromFireBase = dataBase.getCoursesFromFireBase();
                for (Course c : coursesFromFireBase){
                    if ((c.getName().equals(textToSearch)) || (c.getNumber().equals(textToSearch))){
                        isFoundCourse = true;
                        toFind = c;
                    }
                }

                if (!isFoundCourse){
                    Toast.makeText(getContext(), getResources().getString(R.string.course_number_is_not_valid), Toast.LENGTH_LONG).show();
                }

                if (toFind != null){
                    if (onSearchClickListener != null){
                        viewModelAppMainScreen.set(toFind);
                        onSearchClickListener.onSearchClick();
                    }
                }
            }
        });

        seachViewMainFragment.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // todo if text valid enable btn
                textToSearch = newText;
                showCourseInfo.setEnabled(true);
                return false;
            }
        });


        circulumButtonBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (coursesPlanScreenListenerBtn != null) {
                    coursesPlanScreenListenerBtn.onPlanCoursesScreenClicked();
                }
            }
        });

        coursesPlanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (coursesPlanButtonListenerBtn != null) {
                    coursesPlanButtonListenerBtn.onPlanCoursesButtonClicked();
                }
            }
        });

        myCourses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myCoursesButtonListenerBtn != null) {
                    myCoursesButtonListenerBtn.onMyCoursesButtonClicked();
                }
            }
        });

        uploadPicturesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uploadPicturesButtonListenerBtn != null) {
                    uploadPicturesButtonListenerBtn.onUploadPictureButtonClicked();
                }
            }
        });

        privateInfoEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editInfoButtonListener != null) {
                    editInfoButtonListener.onEditInfoButtonClicked();
                }
            }
        });

        showAttendanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showAttendanceButtonListener != null) {
                    showAttendanceButtonListener.onShowAttendanceButtonClicked();
                }
            }
        });

        //spinner
    }
}
