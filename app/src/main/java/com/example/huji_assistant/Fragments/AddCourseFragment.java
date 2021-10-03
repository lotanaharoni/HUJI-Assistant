package com.example.huji_assistant.Fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.huji_assistant.Course;
import com.example.huji_assistant.HujiAssistentApplication;
import com.example.huji_assistant.LocalDataBase;
import com.example.huji_assistant.R;
import com.example.huji_assistant.ViewModelApp;

import java.util.ArrayList;

public class AddCourseFragment extends Fragment {

    private ViewModelApp viewModelApp;

    SearchView searchView;
    ListView listView;
    ArrayList list;
    ArrayAdapter adapter;
    public LocalDataBase dataBase = null;

    public AddCourseFragment(){
        super(R.layout.addcourse_fragment);
    }
    public AddCourseFragment.addCourseToListButtonClickListener addCourseToListButtonClickListener = null;
    public interface addCourseToListButtonClickListener{
        public void addCourseToListBtnClicked(String id);
    }
    private boolean isCourseIdValid = false;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewModelApp viewModelApp = new ViewModelProvider(requireActivity()).get(ViewModelApp.class);
        Button addCourseBtn = view.findViewById(R.id.addCourseToListBtn);
        TextView courseIdTextView = view.findViewById(R.id.insertCourseNumber);

        if (dataBase == null){
            dataBase = HujiAssistentApplication.getInstance().getDataBase();
        }
        ArrayList<Course> courseItems = dataBase.getMyCoursesList();

        addCourseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String courseIdToAdd = courseIdTextView.getText().toString();

                checkValidaity(courseIdToAdd);

                if (isCourseIdValid) {
                    String text = "קורס מספר: " + courseIdToAdd + " נוסף בהצלחה";
                    Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
                    dataBase.addCourseId(courseIdToAdd);
                    courseIdTextView.setText("");
                }
               // addCourseToListButtonClickListener.addCourseToListBtnClicked(courseIdToAdd);
            }
        });


        searchView = view.findViewById(R.id.searchViewNumber);
        listView = view.findViewById(R.id.listViewNumber);
        list = new ArrayList<>(courseItems);

        String[] aa = {"linearit", "infi"};

        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, aa);
        listView.setAdapter(adapter);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(list.contains(query)){
                    adapter.getFilter().filter(query);
                }else{
                   // Toast.makeText(MainActivity.this, "No Match found",Toast.LENGTH_LONG).show();
                }
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    private void checkValidaity(String courseToAddId){
        ArrayList<String> courses = dataBase.getCurrentStudent().getCourses();
        // todo check if the id is a real course id
        ArrayList<Course> coursesFromFireBase = dataBase.getCoursesFromFireBase();

        // go to firebase to check if course exists
        boolean isExists = checkIfExists(courseToAddId);
        System.out.println("is exists: " + isExists);

        if (!isExists){
            String text = "לא קיים קורס עם מספר זה";
            Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
        }
        else {
            if (courses.contains(courseToAddId)) {
                String text = "הקורס כבר קיים ברשימת הקורסים";
                Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
            } else {
                isCourseIdValid = true;
            }
        }
    }

    private boolean checkIfExists(String courseId){

        ArrayList<Course> coursesFromFireBase = dataBase.getCoursesFromFireBase();
        for (Course course : coursesFromFireBase) {
            if (course.getNumber().equals(courseId)){
                return true;
            }
        }
        return false;
    }
}
