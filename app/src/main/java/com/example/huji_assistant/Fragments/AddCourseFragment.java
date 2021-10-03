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
                String text = "קורס מספר: " + courseIdToAdd + " נוסף בהצלחה";
                Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
                dataBase.addCourseId(courseIdToAdd);
               // addCourseToListButtonClickListener.addCourseToListBtnClicked(courseIdToAdd);
            }
        });

       // Course infiC = new Course("אינפי", "0", Course.Type.MandatoryChoose, "", "");
       // Course linearitC = new Course("לינארית", "1", Course.Type.Mandatory);
      //  Course cC = new Course("סי", "2", Course.Type.Choose);
       // Course dastC = new Course("דאסט", "4", Course.Type.Supplemental);
       // Course linearit2C = new Course("לינארית 2", "6", Course.Type.CornerStones);
        //courseItems.add(infiC);
       // courseItems.add(linearitC);
      //  courseItems.add(cC);
      //  courseItems.add(dastC);
      //  courseItems.add(linearit2C);

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
}
