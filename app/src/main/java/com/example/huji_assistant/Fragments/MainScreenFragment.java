package com.example.huji_assistant.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.huji_assistant.R;
import com.example.huji_assistant.ViewModelApp;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

    public interface myCoursesButtonListener{
        public void onMyCoursesButtonClicked();
    }

    public interface editInfoButtonListener{
        public void onEditInfoButtonClicked();
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModelApp = new ViewModelProvider(requireActivity()).get(ViewModelApp.class);
        myCourses = view.findViewById(R.id.myCoursesButton);
        uploadPicturesButton = view.findViewById(R.id.uploadPicturesButton);
        privateInfoEditBtn = view.findViewById(R.id.privateInfoEditButton);

        FloatingActionButton openCameraFloatingButton = view.findViewById(R.id.open_camera_floating_button);
        viewModelApp.getStudent().observe(getViewLifecycleOwner(), item->{

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

        //spinner
    }
}
