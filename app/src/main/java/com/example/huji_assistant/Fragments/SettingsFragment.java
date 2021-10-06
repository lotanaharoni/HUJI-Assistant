package com.example.huji_assistant.Fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.huji_assistant.Course;
import com.example.huji_assistant.CourseItemHolder;
import com.example.huji_assistant.CoursesAdapter;
import com.example.huji_assistant.HujiAssistentApplication;
import com.example.huji_assistant.LocalDataBase;
import com.example.huji_assistant.R;
import com.example.huji_assistant.StudentInfo;
import com.example.huji_assistant.ViewModelApp;
import com.example.huji_assistant.ViewModelAppMainScreen;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.ArrayList;

public class SettingsFragment extends Fragment {

    private LocalDataBase db = null;
    FirebaseFirestore firebaseInstancedb = FirebaseFirestore.getInstance();
    FirebaseFirestoreSettings settings;
    public SettingsFragment(){
        super(R.layout.fragment_settings);
    }
    TextView currentUserTxt;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (db == null) {
            db = HujiAssistentApplication.getInstance().getDataBase();
        }
        settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firebaseInstancedb.setFirestoreSettings(settings);
        currentUserTxt = view.findViewById(R.id.nameOfCurrentUser);

        StudentInfo currentUser = db.getCurrentUser();
        String email = currentUser.getEmail();
        String text = currentUserTxt.getText() + " " + email;
        currentUserTxt.setText(text);

    }
}
