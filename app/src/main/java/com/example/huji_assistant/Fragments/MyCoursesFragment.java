package com.example.huji_assistant.Fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
//<<<<<<< HEAD:app/src/main/java/com/example/huji_assistant/MyCoursesFragment.java
import android.widget.Spinner;
import android.widget.TextView;

//>>>>>>> bf2892270b275fb497ce423d915c8af6ed29de96:app/src/main/java/com/example/huji_assistant/Fragments/MyCoursesFragment.java

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.huji_assistant.Chug;
import com.example.huji_assistant.Course;
import com.example.huji_assistant.CourseItemHolder;
import com.example.huji_assistant.CoursesAdapter;
import com.example.huji_assistant.Faculty;
import com.example.huji_assistant.HujiAssistentApplication;
import com.example.huji_assistant.LocalDataBase;
import com.example.huji_assistant.R;
import com.example.huji_assistant.StudentInfo;
import com.example.huji_assistant.ViewModelAppMainScreen;
import com.example.huji_assistant.databinding.FragmentMycoursesBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

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
    ArrayList<String> coursesId = new ArrayList<>();
    public CoursesAdapter.OnItemClickListener onItemClickListener = null;
    FirebaseFirestore firebaseInstancedb = FirebaseFirestore.getInstance();
    TextView studentNameTextView;
    TextView facultyTextView;
    TextView chugTextView;
    TextView maslulTextView;
    TextView degreeTextView;
    TextView yearTextView;


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

        if (dataBase == null){
            dataBase = HujiAssistentApplication.getInstance().getDataBase();
        }

        StudentInfo currentStudent = dataBase.getCurrentUser();
        studentNameTextView = view.findViewById(R.id.nameOfStudentTextView);
        facultyTextView = view.findViewById(R.id.nameOfFaculty);
        chugTextView = view.findViewById(R.id.nameOfChug);
        maslulTextView = view.findViewById(R.id.nameOfMaslul);
        degreeTextView = view.findViewById(R.id.nameOfDegree);
        yearTextView = view.findViewById(R.id.yearOfDegree);

        String name = studentNameTextView.getText() +" " + currentStudent.getPersonalName() + " " + currentStudent.getFamilyName();
        studentNameTextView.setText(name);
        String faculty = facultyTextView.getText() + " " + currentStudent.getFacultyId();
        facultyTextView.setText(faculty); //todo change not id
        String chug = chugTextView.getText() + " " + currentStudent.getChugId();
        chugTextView.setText(chug);
        String maslul = maslulTextView.getText() + " " + currentStudent.getMaslulId();
        maslulTextView.setText(maslul);
        String degree = degreeTextView.getText() + " " + currentStudent.getDegree();
        degreeTextView.setText(degree);
        String year = yearTextView.getText() + " " + currentStudent.getYear();
        yearTextView.setText(year);

        ArrayList<String> coursesOfStudentById = currentStudent.getCourses();
        ArrayList<Course> coursesFromFireBase = new ArrayList<>();
        ArrayList<Course> coursesForAdapter = new ArrayList<>();

      //  String courseNumber = "64304";
        // Gets all courses from firestore
        /**
        // todo show courses of student - using firebase
        Task<QuerySnapshot> courses1 = firebaseInstancedb.collection("courses").document(currentStudent.getChugId())
                .collection("maslulimInChug").document(currentStudent.getMaslulId())
                .collection("coursesInMaslul")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        ArrayList<Course> coursesFromFireBase = new ArrayList<>();

                        for (DocumentSnapshot document1 : documents){
                            // retrieve for each chug id it's name
                            String docId  = document1.getId().toString();
                            Course course = document1.toObject(Course.class);
                            // chugId = chug.getId();
                            System.out.println("course "+ course.toStringP());
                            coursesFromFireBase.add(course);
                        }

                        for (String id : coursesOfStudentById){
                            for (Course course: coursesFromFireBase){
                                if (course.getNumber().equals(id)){
                                    coursesForAdapter.add(course);
                                }
                            }
                        }
                        // todo save the list of courses of current student to db
                        // show changes on course list in adapter
                        dataBase.setCoursesOfCurrentStudent(coursesForAdapter);
                      //  adapter.addCoursesListToAdapter(coursesForAdapter);
                      //  adapter.notifyDataSetChanged();
                      //  recyclerViewMyCourses.setAdapter(adapter);
                    }
                });
         */

        //todo observe from db on change in courses



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



        if (holder == null) {
            holder = new CourseItemHolder(recyclerViewMyCourses);
        }

       // ImageView logoutImageView = view.findViewById(R.id.logoutImageView);
      //  logoutImageView.setEnabled(false);

       // ArrayList<Course>
       // ArrayList<Course> courseItems = new ArrayList<>(); // Saves the current courses list
        StudentInfo currentUser = dataBase.getCurrentUser();


        // todo maybe observe instead
        Task<DocumentSnapshot> document =  firebaseInstancedb.collection("students").document(currentUser.getId())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                         @Override
                         public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                             DocumentSnapshot result = task.getResult();
                             assert result != null;
                             StudentInfo data = result.toObject(StudentInfo.class);
                             assert data != null;
                             coursesId = new ArrayList<>(data.getCourses());
                             System.out.println("in+++: ");
                             printCourses();
                         }
                     });

        ArrayList<String> coursesIds = dataBase.getCurrentUser().getCourses();
        // get the courses from fire store

        System.out.println("out: ++++");
        printCourses();
        ArrayList<Course> courseItems = dataBase.getCoursesOfCurrentStudent();
        // todo add demo courses
       // Course infiC = new Course("infi", "0", Course.Type.MandatoryChoose,"","");
      //  Course linearitC = new Course("linearit", "1", Course.Type.Mandatory);
      //  Course cC = new Course("c", "2", Course.Type.Choose);
     //   Course dastC = new Course("dast", "4", Course.Type.Supplemental);
      //  Course linearit2C = new Course("linearit 2", "6", Course.Type.CornerStones);
       // courseItems.add(infiC);
   //     courseItems.add(linearitC);
     //   courseItems.add(cC);
      //  courseItems.add(dastC);
      //  courseItems.add(linearit2C);



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
    private void printCourses(){
        for (int i = 0; i < coursesId.size(); i++){
            System.out.println(coursesId.get(i));
        }
    }
}
