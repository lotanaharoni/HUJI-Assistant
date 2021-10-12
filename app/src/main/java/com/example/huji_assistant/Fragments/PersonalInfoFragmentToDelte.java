package com.example.huji_assistant.Fragments;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.huji_assistant.R;
import com.example.huji_assistant.StudentInfo;
import com.example.huji_assistant.ViewModelApp;

/**
 * This fragment is used in the registration screens, to add details about the degree:
 * The user is able to choose it's faculty, chug, maslul and add more details about it's degree.
 */
public class PersonalInfoFragmentToDelte extends Fragment {
    private ViewModelApp viewModelApp;

    public interface continueButtonClickListener{
        public void continueBtnClicked();
    }

    /* Continue button listener*/
    public PersonalInfoFragmentToDelte.continueButtonClickListener continueListener = null;

    public PersonalInfoFragmentToDelte(){
        super(R.layout.fragment_info);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModelApp = new ViewModelProvider(requireActivity()).get(ViewModelApp.class);
        view.findViewById(R.id.continuePersBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (continueListener != null) {
                    StudentInfo currentStudent = viewModelApp.getStudent().getValue();
                    continueListener.continueBtnClicked();
                }
            }
        });
    }
}

