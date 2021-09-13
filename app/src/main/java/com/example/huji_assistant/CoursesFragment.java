package com.example.huji_assistant;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class CoursesFragment extends Fragment {
    private ViewModelApp viewModelApp;
    public interface endRegistrationButtonClickListener{
        public void onEndRegistrationBtnClicked();
    }

    public CoursesFragment.endRegistrationButtonClickListener endRegistrationBtnListener = null;

    public CoursesFragment(){
        super(R.layout.courselayout);
    }
    Spinner dropdown;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewModelApp viewModelApp = new ViewModelProvider(requireActivity()).get(ViewModelApp.class);
        viewModelApp.studentInfoMutableLiveData.observe(getViewLifecycleOwner(), new Observer<StudentInfo>() {
            @Override
            public void onChanged(StudentInfo studentInfo) {
                StudentInfo currentStudent = viewModelApp.studentInfoMutableLiveData.getValue();
                String name = currentStudent.getName();
             //   TextView nameTextView = view.findViewById(R.id.studentNameTextView);
              //  nameTextView.setText(name);
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(false) {
            @Override
            public void handleOnBackPressed() {

            }
        };

        //spinner
       // dropdown = view.findViewById(R.id.coursespinner);
        String[] items = new String[]{"1", "2", "three"};
        //  ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, dropdown, items);


        Button endRegistrationBtn = view.findViewById(R.id.buttonEndRegistration);
        endRegistrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (endRegistrationBtnListener != null){
                    endRegistrationBtnListener.onEndRegistrationBtnClicked();
                }
            }
        });



//create an adapter to describe how the items are displayed, adapters are used in several places in android.
//There are multiple variations of this, but this is the basic variant.
        //  ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
//set the spinners adapter to the previously created one.
        // dropdown.setAdapter(adapter);

    }


}
