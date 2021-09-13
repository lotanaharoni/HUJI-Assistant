package com.example.huji_assistant;

import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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
        //spinner
    }
}
