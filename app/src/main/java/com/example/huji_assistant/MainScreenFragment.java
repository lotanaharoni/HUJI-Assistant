package com.example.huji_assistant;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainScreenFragment extends Fragment {

    private ViewModelApp viewModelApp;

    public interface endRegistrationButtonClickListener{
        public void onEndRegistrationBtnClicked();
    }
    public MainScreenFragment.endRegistrationButtonClickListener newUserBtnListener = null;
    public MainScreenFragment.myCoursesButtonListener myCoursesButtonListenerBtn = null;

    public interface myCoursesButtonListener{
        public void onMyCoursesButtonClicked();
    }
    public MainScreenFragment(){
        super(R.layout.mainscreen);
    }
    Spinner dropdown;
    Button myCourses;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModelApp = new ViewModelProvider(requireActivity()).get(ViewModelApp.class);
        myCourses = view.findViewById(R.id.myCoursesButton);

        FloatingActionButton openCameraFloatingButton = view.findViewById(R.id.open_camera_floating_button);
        viewModelApp.get().observe(getViewLifecycleOwner(), item->{

        });

        myCourses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myCoursesButtonListenerBtn != null) {
                    myCoursesButtonListenerBtn.onMyCoursesButtonClicked();
                }
            }
        });

        openCameraFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(requireActivity(), "Camera is clicked", Toast.LENGTH_SHORT).show();
                // todo add action
                //askCameraPermissions();
            }
        });

        //spinner
    }

}
