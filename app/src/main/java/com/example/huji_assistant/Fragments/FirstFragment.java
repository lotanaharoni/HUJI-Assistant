package com.example.huji_assistant.Fragments;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.huji_assistant.R;
import com.example.huji_assistant.ViewModelApp;

public class FirstFragment extends Fragment {

    private ViewModelApp viewModelApp;

    public interface newUserButtonClickListener{
        public void onNewUserBtnClicked();
    }

    public interface existingUserButtonClickListener{
        public void onExistingUserBtnClicked();
    }

    public newUserButtonClickListener newUserBtnListener = null;
    public existingUserButtonClickListener existingUserBtnListener = null;
    public FirstFragment(){
        super(R.layout.login);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //ViewModelApp viewModelApp = new ViewModelProvider(requireActivity()).get(ViewModelApp.class);
        ViewModelApp viewModelApp = new ViewModelProvider(requireActivity()).get(ViewModelApp.class);

        view.findViewById(R.id.newUser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newUserBtnListener != null){
                    newUserBtnListener.onNewUserBtnClicked();
                }
            }
        });

        view.findViewById(R.id.exisitingUser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (existingUserBtnListener != null){
                    existingUserBtnListener.onExistingUserBtnClicked();
                }
            }
        });
    }
}