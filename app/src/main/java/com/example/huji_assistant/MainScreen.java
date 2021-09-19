package com.example.huji_assistant;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainScreen extends Fragment {

    private ViewModelApp viewModelApp;
    public interface endRegistrationButtonClickListener{
        public void onEndRegistrationBtnClicked();
    }

    public MainScreen.endRegistrationButtonClickListener endRegistrationBtnListener = null;

    public MainScreen(){
        super(R.layout.mainscreen);
    }
    Spinner dropdown;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModelApp = new ViewModelProvider(requireActivity()).get(ViewModelApp.class);
        FloatingActionButton openCameraFloatingButton = view.findViewById(R.id.open_camera_floating_button);

        viewModelApp.get().observe(getViewLifecycleOwner(), item->{

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
