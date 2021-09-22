package com.example.huji_assistant;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class PersonalInfoFragment extends Fragment {
    private ViewModelApp viewModelApp;
    // private ViewDataBinding binding = null;

    public interface continueButtonClickListener{
        public void continueBtnClicked();
    }


    EditText nameEditText;
    EditText yearEditText;
    EditText degreeNameEditText;



    public PersonalInfoFragment.continueButtonClickListener continueListener = null;

    public PersonalInfoFragment(){
        super(R.layout.personalinfofragment);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //nameEditText = view.findViewById(R.id.editTextTextPersonName);
        // yearEditText = view.findViewById(R.id.editTextTextPersonYear);
        //degreeNameEditText = view.findViewById(R.id.editTextTextPersonDegree);

        viewModelApp = new ViewModelProvider(requireActivity()).get(ViewModelApp.class);
        view.findViewById(R.id.continuePersBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (continueListener != null) {
                    StudentInfo currentStudent = viewModelApp.getStudent().getValue();

                    //  currentStudent.setName(nameEditText.getText().toString());
                    // currentStudent.setYear(yearEditText.getText().toString());
                    //  currentStudent.setDegreeName(degreeNameEditText.getText().toString());

                    continueListener.continueBtnClicked();
                }
            }
        });
    }
}

