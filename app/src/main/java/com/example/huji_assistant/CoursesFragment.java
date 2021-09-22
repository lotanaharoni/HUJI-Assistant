package com.example.huji_assistant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.huji_assistant.databinding.FragmentCoursesBinding;
import com.example.huji_assistant.databinding.FragmentInfoBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.UUID;

public class CoursesFragment extends Fragment {
    private ViewModelApp viewModelApp;
    private ViewModelAppMainScreen viewModelAppCourse;
    FragmentCoursesBinding binding;
    public CourseItemHolder holder = null;
    public CoursesAdapter adapter = null;
    public LocalDataBase dataBase = null;
    public interface endRegistrationButtonClickListener{
        public void onEndRegistrationBtnClicked();
    }

    String facultyId;
    String chugId;
    String maslulId;
    String degreeType; // first degree, second degree...
    String year;
    String beginnigYearOfDegree;
    String beginSemesterOfDegree;
    TextView facultyTextView;
    TextView chugTextView;
    TextView maslulTextView;
    TextView yearTextView;
    TextView degreeTextView;
    RecyclerView recyclerViewCourses;
    LinearLayoutManager coordinatorLayout;
    public CoursesFragment.endRegistrationButtonClickListener endRegistrationBtnListener = null;

    public CoursesAdapter.OnItemClickListener onItemClickListener = null;

    public CoursesFragment(){
        super(R.layout.fragment_courses);
    }
    Spinner dropdown;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCoursesBinding.inflate(inflater, container, false);

        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModelApp = new ViewModelProvider(requireActivity()).get(ViewModelApp.class);
        viewModelAppCourse = new ViewModelProvider(requireActivity()).get(ViewModelAppMainScreen.class);
        facultyTextView = view.findViewById(R.id.faculty);
        chugTextView = view.findViewById(R.id.chugName);
        maslulTextView = view.findViewById(R.id.maslulName);
        yearTextView = view.findViewById(R.id.year);
        degreeTextView = view.findViewById(R.id.degreeType);
        recyclerViewCourses = view.findViewById(R.id.recycleViewCourses);
        adapter = new CoursesAdapter(getContext());

        if (holder == null) {
            holder = new CourseItemHolder(recyclerViewCourses);
        }

        ArrayList<Course> courseItems = new ArrayList<>(); // Saves the current courses list

        // todo add demo courses
        Course infiC = new Course("אינפי", "0");
        Course linearitC = new Course("לינארית", "1");
        courseItems.add(infiC);
        courseItems.add(linearitC);

        // Create the adapter
        adapter.addCoursesListToAdapter(courseItems);
        recyclerViewCourses.setAdapter(adapter);

          coordinatorLayout = new LinearLayoutManager(getContext(),
                RecyclerView.VERTICAL, false);

        recyclerViewCourses.setLayoutManager(new LinearLayoutManager(getContext(),
                RecyclerView.VERTICAL, false));

        viewModelApp.getStudent().observe(getViewLifecycleOwner(), item->{
             facultyId = item.getFacultyId();
             chugId = item.getChugId();
             maslulId = item.getMaslulId();
             degreeType = item.getDegree();
             year = item.getYear();
             beginnigYearOfDegree = item.getBeginYear();
             beginSemesterOfDegree = item.getBeginSemester();

             System.out.println("begin year: " + beginnigYearOfDegree);
             System.out.println("begin semester: " + beginSemesterOfDegree);

             facultyTextView.setText(facultyId);
             chugTextView.setText(chugId);
             maslulTextView.setText(maslulId);
             degreeTextView.setText(degreeType);
             yearTextView.setText(year);
        });

        adapter.setItemClickListener(new CoursesAdapter.OnItemClickListener() {
            @Override
            public void onClick(Course item) {
                if (onItemClickListener != null){
                    viewModelAppCourse.set(item);
                    onItemClickListener.onClick(item);
                }

              //  adapter.notifyDataSetChanged();
            }
        });

        //beginnigYearOfDegree = "2022"; // todo change






        /**
        viewModelApp.studentInfoMutableLiveData.observe(getViewLifecycleOwner(), new Observer<StudentInfo>() {
            @Override
            public void onChanged(StudentInfo studentInfo) {
                StudentInfo currentStudent = viewModelApp.studentInfoMutableLiveData.getValue();
                String name = currentStudent.getName();
             //   TextView nameTextView = view.findViewById(R.id.studentNameTextView);
              //  nameTextView.setText(name);
            }
        });*/

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
    private void enableSwipeToDeleteAndUndo() {
        SwipeViewHolderItem swipeToDelete = new SwipeViewHolderItem(getContext()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                //super.onSwiped(viewHolder, direction);
                final int position = viewHolder.getAdapterPosition();
                final Course courseItem = adapter.getItems().get(position);
                //todo check if this works should delete from local db and then from adapter
                adapter.removeCourseFromAdapter(courseItem);

            }
        };
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDelete);
       // ItemTouchHelper.attachToRecyclerView(recyclerViewCourses);
    }
}
