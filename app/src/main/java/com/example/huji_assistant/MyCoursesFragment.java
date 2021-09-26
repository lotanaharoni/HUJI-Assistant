package com.example.huji_assistant;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.huji_assistant.databinding.FragmentCoursesBinding;
import com.example.huji_assistant.databinding.FragmentMycoursesBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MyCoursesFragment extends Fragment {

    private ViewModelAppMainScreen viewModelAppMainScreen;
    FragmentMycoursesBinding binding;

    public CourseItemHolder holder = null;
    LinearLayoutManager coordinatorLayout;
    public CoursesAdapter adapter = null;
    public LocalDataBase dataBase = null;
    RecyclerView recyclerViewMyCourses;
    SearchView searchView;
    public CoursesAdapter.OnItemClickListener onItemClickListener = null;

    //public interface endRegistrationButtonClickListener{
     //   public void onEndRegistrationBtnClicked();
   // }

    public interface addCourseButtonClickListener{
        public void addCourseBtnClicked();
    }

    public MyCoursesFragment(){
        super(R.layout.fragment_mycourses);
    }
    public MyCoursesFragment.addCourseButtonClickListener addCourseListener = null;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMycoursesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModelAppMainScreen = new ViewModelProvider(requireActivity()).get(ViewModelAppMainScreen.class);
        recyclerViewMyCourses = view.findViewById(R.id.recycleViewMyCourses);
        adapter = new CoursesAdapter(getContext());
        FloatingActionButton addCourseBtn = view.findViewById(R.id.addCourseBtn);
        androidx.appcompat.widget.SearchView searchView = view.findViewById(R.id.search1);
        //searchView = (SearchView) /viewById




        addCourseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCourseListener.addCourseBtnClicked();
            }
        });

        ArrayAdapter arrayAdapter = new ArrayAdapter(requireContext(), R.layout.dropdownfacultyitem, getResources().getStringArray(R.array.courseType));
        arrayAdapter.getFilter().filter("");
        binding.autocompletechoosetype.setAdapter(arrayAdapter);

        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                arrayAdapter.getFilter().filter(newText);
                return false;
            }
        });


        if (dataBase == null){
            dataBase = HujiAssistentApplication.getInstance().getDataBase();
        }
/**
        dataBase.publicLiveDataMyCourses.observe(getViewLifecycleOwner(), new Observer<List<Course>>() {
            @Override
            public void onChanged(List<Course> items) {
                if(items.size() == 0){
                    adapter.addCoursesListToAdapter(new ArrayList<>());
                }
                else{
                    adapter.addCoursesListToAdapter(new ArrayList<>(items));
                }
            }
        });*/

        ArrayList<Course> courseItems = dataBase.getMyCoursesList();

        if (holder == null) {
            holder = new CourseItemHolder(recyclerViewMyCourses);
        }

       // ImageView logoutImageView = view.findViewById(R.id.logoutImageView);
      //  logoutImageView.setEnabled(false);

       // ArrayList<Course>
        courseItems = new ArrayList<>(); // Saves the current courses list

        // todo add demo courses
        Course infiC = new Course("infi", "0", Course.Type.MandatoryChoose);
        Course linearitC = new Course("linearit", "1", Course.Type.Mandatory);
        Course cC = new Course("c", "2", Course.Type.Choose);
        Course dastC = new Course("dast", "4", Course.Type.Supplemental);
        Course linearit2C = new Course("linearit 2", "6", Course.Type.CornerStones);
        courseItems.add(infiC);
        courseItems.add(linearitC);
        courseItems.add(cC);
        courseItems.add(dastC);
        courseItems.add(linearit2C);

        // Create the adapter
        adapter.addCoursesListToAdapter(courseItems);
        adapter.notifyDataSetChanged();
        recyclerViewMyCourses.setAdapter(adapter);

        coordinatorLayout = new LinearLayoutManager(getContext(),
                RecyclerView.VERTICAL, false);

        recyclerViewMyCourses.setLayoutManager(new LinearLayoutManager(getContext(),
                RecyclerView.VERTICAL, false));

        adapter.setItemClickListener(new CoursesAdapter.OnItemClickListener() {
            @Override
            public void onClick(Course item) {
                if (onItemClickListener != null){
                    viewModelAppMainScreen.set(item);
                    onItemClickListener.onClick(item);
                }
                //  adapter.notifyDataSetChanged();
            }
        });

    }
}
