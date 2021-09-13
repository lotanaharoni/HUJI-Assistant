package com.example.huji_assistant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.huji_assistant.databinding.FragmentInfoBinding;


public class InfoFragment extends Fragment {

    public InfoFragment(){
        super(R.layout.personalinfofragment);
    }
    FragmentInfoBinding binding;
    AutoCompleteTextView dropdownhugim;
    private ViewModelApp viewModelApp;

    public interface continueButtonClickListener{
        public void continueBtnClicked();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // return super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentInfoBinding.inflate(inflater, container, false);

        // Get values recourse
        String[] facultyArray = getResources().getStringArray(R.array.faculty);
        String[] computerScienceHugimArray = getResources().getStringArray(R.array.school_science_faculty);
        String[] ruachHugimArray = getResources().getStringArray(R.array.ruach_faculty);

        // Get items to show in drop down faculty
        binding.autoCompleteTextViewFaculty.setAdapter(new ArrayAdapter(requireContext(), R.layout.dropdownfacultyitem, facultyArray));

        // binding.autoCompleteTextViewChug.setAdapter(new ArrayAdapter(requireContext(), R.layout.dropdownhugimitem, computerScienceHugimArray));

        return binding.getRoot();
    }

    public InfoFragment.continueButtonClickListener continueListener = null;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AutoCompleteTextView autoCompleteTextViewChug = (AutoCompleteTextView) getView().findViewById(R.id.autoCompleteTextViewChug);
        AutoCompleteTextView autoCompleteTextViewDegree = (AutoCompleteTextView) getView().findViewById(R.id.autoCompleteTextViewDegree);
        AutoCompleteTextView autoCompleteTextViewMaslul = (AutoCompleteTextView) getView().findViewById(R.id.autoCompleteTextViewMaslul);
        AutoCompleteTextView autoCompleteTextViewYear = (AutoCompleteTextView) getView().findViewById(R.id.autoCompleteTextViewYear);

        autoCompleteTextViewChug.setEnabled(false);
        autoCompleteTextViewDegree.setEnabled(false);
        autoCompleteTextViewMaslul.setEnabled(false);
        autoCompleteTextViewYear.setEnabled(false);

        viewModelApp = new ViewModelProvider(requireActivity()).get(ViewModelApp.class);
        view.findViewById(R.id.continuePersBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (continueListener != null) {
                    StudentInfo currentStudent = viewModelApp.get().getValue();

                    //  currentStudent.setName(nameEditText.getText().toString());
                    // currentStudent.setYear(yearEditText.getText().toString());
                    //  currentStudent.setDegreeName(degreeNameEditText.getText().toString());

                    continueListener.continueBtnClicked();
                }
            }
        });

        dropdownhugim = view.findViewById(R.id.autoCompleteTextViewChug);
        dropdownhugim.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String)parent.getItemAtPosition(position);
                System.out.println("selection"+ selection);
            }
        });


        dropdownhugim.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String)parent.getItemAtPosition(position);
                System.out.println("selection"+ selection);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
