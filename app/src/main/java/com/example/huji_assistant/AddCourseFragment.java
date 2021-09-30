package com.example.huji_assistant;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewModelApp viewModelApp = new ViewModelProvider(requireActivity()).get(ViewModelApp.class);

        if (dataBase == null){
            dataBase = HujiAssistentApplication.getInstance().getDataBase();
        }
        ArrayList<Course> courseItems = dataBase.getMyCoursesList();

        Course infiC = new Course("אינפי", "0", Course.Type.MandatoryChoose, "", "");
       // Course linearitC = new Course("לינארית", "1", Course.Type.Mandatory);
      //  Course cC = new Course("סי", "2", Course.Type.Choose);
       // Course dastC = new Course("דאסט", "4", Course.Type.Supplemental);
       // Course linearit2C = new Course("לינארית 2", "6", Course.Type.CornerStones);
        courseItems.add(infiC);
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
