package com.example.huji_assistant.Fragments;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.huji_assistant.Course;
import com.example.huji_assistant.CoursesAdapter;
import com.example.huji_assistant.HujiAssistentApplication;
import com.example.huji_assistant.LocalDataBase;
import com.example.huji_assistant.PlanCoursesAdapter;
import com.example.huji_assistant.R;
import com.example.huji_assistant.StudentInfo;
import com.example.huji_assistant.ViewModelAppMainScreen;
import com.example.huji_assistant.databinding.FragmentPlancoursesBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class PlanCoursesFragment extends Fragment {
    private ViewModelAppMainScreen viewModelAppMainScreen;
    public PlanCoursesAdapter adapter = null;
    public LocalDataBase dataBase = null;
    public PlanCoursesAdapter.OnCheckBoxClickListener onCheckBoxClickListener = null;
    public PlanCoursesAdapter.OnItemClickListener onItemClickListener = null;
    private ArrayList<Course> coursesToShow = null;
    private String filterYear = null;
    private String filterSemester = null;
    private String filterPoints = null;

    FirebaseFirestoreSettings settings;
    FragmentPlancoursesBinding binding;
    LinearLayoutManager coordinatorLayout;
    RecyclerView recyclerViewMyCourses;
    FirebaseFirestore firebaseInstancedb = FirebaseFirestore.getInstance();

    TextView studentNameTextView;
    TextView facultyTextView;
    TextView chugTextView;
    TextView maslulTextView;
    TextView degreeTextView;
    TextView yearTextView;
    Button approvePlannedBtn;
    AutoCompleteTextView autoCompleteChooseYear;
    AutoCompleteTextView autoCompleteChooseSemester;
    AutoCompleteTextView autoCompleteChoosePoints;
    CheckBox showOnlyChosePlanned;
    ArrayList<String> plannedCoursesOfStudent = new ArrayList<>(); // Saves the checked courses numbers

    public PlanCoursesFragment() {
        super(R.layout.fragment_plancourses);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPlancoursesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        facultyTextView = view.findViewById(R.id.nameOfFaculty);
        chugTextView = view.findViewById(R.id.nameOfChug);
        maslulTextView = view.findViewById(R.id.nameOfMaslul);
        degreeTextView = view.findViewById(R.id.nameOfDegree);
        yearTextView = view.findViewById(R.id.yearOfDegree);
        autoCompleteChooseYear = view.findViewById(R.id.autocompletechooseyear);
        autoCompleteChooseSemester = view.findViewById(R.id.autocompletechoosesemester);
        autoCompleteChoosePoints = view.findViewById(R.id.autocompletechoosepoints);
        recyclerViewMyCourses = view.findViewById(R.id.recycleViewMyCourses);
        showOnlyChosePlanned = view.findViewById(R.id.showPlanedOnlyCheckBox);
        studentNameTextView = view.findViewById(R.id.nameOfStudentTextView);
        viewModelAppMainScreen = new ViewModelProvider(requireActivity()).get(ViewModelAppMainScreen.class);
        adapter = new PlanCoursesAdapter(getContext());
        approvePlannedBtn = view.findViewById(R.id.approvePlannedCoursesBtn);

        if (dataBase == null) {
            dataBase = HujiAssistentApplication.getInstance().getDataBase();
        }

        if (coursesToShow == null) {
            coursesToShow = dataBase.getCoursesToComplete();
        }

        settings = new FirebaseFirestoreSettings.Builder().setPersistenceEnabled(true).build();
        firebaseInstancedb.setFirestoreSettings(settings);

        StudentInfo currentStudent = dataBase.getCurrentUser();

        // Update data on screen
        String name = studentNameTextView.getText() + " " + currentStudent.getPersonalName() + " " + currentStudent.getFamilyName();
        studentNameTextView.setText(name);

        String faculty = facultyTextView.getText() + " " + currentStudent.getFacultyId() + " " + dataBase.getCurrentFaculty().getTitle();
        facultyTextView.setText(faculty);

        String chug = chugTextView.getText() + " " + currentStudent.getChugId() + " " + dataBase.getCurrentChug().getTitle();
        chugTextView.setText(chug);

        String maslul = maslulTextView.getText() + " " + currentStudent.getMaslulId() + " " + dataBase.getCurrentMaslul().getTitle();
        maslulTextView.setText(maslul);

        String degree = currentStudent.getDegree();
        if (degree != null) {
            degree = degreeTextView.getText() + " " + currentStudent.getDegree();
            degreeTextView.setText(degree);
        }

        String year = currentStudent.getYear();
        if (year != null) {
            year = yearTextView.getText() + " " + currentStudent.getYear();
            yearTextView.setText(year);
        }

        // Year Choose
        ArrayAdapter arrayYearAdapter = new ArrayAdapter(requireContext(), R.layout.dropdownfacultyitem, getResources().getStringArray(R.array.yearArray));
        arrayYearAdapter.getFilter().filter("");
        binding.autocompletechooseyear.setAdapter(arrayYearAdapter);

        autoCompleteChooseYear.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                filterYear = (String) (parent.getItemAtPosition(position));

                ArrayList<Course> list = dataBase.getCoursesToPlan(filterYear, filterSemester, filterPoints);
                coursesToShow = list;
                adapter.addCoursesListToAdapter(dataBase.sortCoursesByYearAndType(list));
                adapter.notifyDataSetChanged();
                if (list.size() == 0)
                    Toast.makeText(getContext(), getResources().getString(R.string.no_courses), Toast.LENGTH_SHORT).show();

                if (showOnlyChosePlanned.isChecked()){ // check if onlyShowPlaned is clicked, and if so response accordingly
                    showOnlyPlanedLogic();
                }
            }
        });

        // Semester Choose
        ArrayAdapter arraySemesterAdapter = new ArrayAdapter(requireContext(), R.layout.dropdownfacultyitem, getResources().getStringArray(R.array.semesterArray));
        arraySemesterAdapter.getFilter().filter("");
        binding.autocompletechoosesemester.setAdapter(arraySemesterAdapter);

        autoCompleteChooseSemester.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                filterSemester = ((String) (parent.getItemAtPosition(position)));
                if (!(filterSemester.equals("קורס שנתי") || filterSemester.equals("הכל"))) {
                    if (filterSemester.equals("סמסטר א או ב")) {
                        filterSemester = "א' או ב'";
                    } else filterSemester = filterSemester.substring(6) + "'";
                }

                dataBase.getCurrentStudent().getCourses(); // courses the student finished in string array
                ArrayList<Course> list = dataBase.getCoursesToPlan(filterYear, filterSemester, filterPoints);

                coursesToShow = list;
                adapter.addCoursesListToAdapter(dataBase.sortCoursesByYearAndType(list));
                adapter.notifyDataSetChanged();
                if (list.size() == 0)
                    Toast.makeText(getContext(), getResources().getString(R.string.no_courses), Toast.LENGTH_SHORT).show();

                if (showOnlyChosePlanned.isChecked()){ // check if onlyShowPlaned is clicked, and if so response accordingly
                    showOnlyPlanedLogic();
                }
            }
        });


        // Points Choose
        ArrayAdapter arrayPointsAdapter = new ArrayAdapter(requireContext(), R.layout.dropdownfacultyitem, getResources().getStringArray(R.array.pointsArray));
        arrayPointsAdapter.getFilter().filter("");
        binding.autocompletechoosepoints.setAdapter(arrayPointsAdapter);

        autoCompleteChoosePoints.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                filterPoints = ((String) (parent.getItemAtPosition(position))).substring(0, 1);
                ArrayList<Course> list = dataBase.getCoursesToPlan(filterYear, filterSemester, filterPoints);

                adapter.addCoursesListToAdapter(dataBase.sortCoursesByYearAndType(list));
                adapter.notifyDataSetChanged();
                if (list.size() == 0)
                    Toast.makeText(getContext(), getResources().getString(R.string.no_courses), Toast.LENGTH_SHORT).show();

                if (showOnlyChosePlanned.isChecked()){ // check if onlyShowPlaned is clicked, and if so response accordingly
                    showOnlyPlanedLogic();
                }
            }
        });

        // Create the adapter
        adapter.addCoursesListToAdapter(dataBase.sortCoursesByYearAndType(coursesToShow)); // courses List To Show --------------------------
        adapter.notifyDataSetChanged();
        recyclerViewMyCourses.setAdapter(adapter);

        if (showOnlyChosePlanned.isChecked()){ // check if onlyShowPlaned is clicked, and if so response accordingly
            showOnlyPlanedLogic();
        }

        coordinatorLayout = new LinearLayoutManager(getContext(),
                RecyclerView.VERTICAL, false);

        recyclerViewMyCourses.setLayoutManager(new LinearLayoutManager(getContext(),
                RecyclerView.VERTICAL, false));

        // opening the Course info fragment when clicking on a course
        adapter.setItemClickListener(new PlanCoursesAdapter.OnItemClickListener() {
            @Override
            public void onClick(Course item) {
                if (onItemClickListener != null) {
                    viewModelAppMainScreen.set(item);
                    onItemClickListener.onClick(item);
                }
            }
        });

        // todo-liora
        //adding new course to planned courses by checkbox
        adapter.setItemCheckBoxListener(new PlanCoursesAdapter.OnCheckBoxClickListener() {
            @Override
            public void onCheckBoxClicked(View v, Course item) {
                // if (onCheckBoxClickListener != null) {
                //      onCheckBoxClickListener.onCheckBoxClicked(v, item);
                //  }
                System.out.println("rrrr " + item.getNumber());
              //  plannedCoursesOfStudent = dataBase.getCurrentStudent().getPlanned();
                plannedCoursesOfStudent = dataBase.getCoursesPlannedById();
                for (String s: plannedCoursesOfStudent){
                    System.out.println("jj " + s);
                }

                //if (onCheckBoxClickListener != null) { // todo need?
                    System.out.println("this item1 " + item.getNumber());
                    if (item.getPlannedChecked()) {
                        if (!plannedCoursesOfStudent.contains(item.getNumber())) {
                            System.out.println("this item2 " + item.getNumber());
                            plannedCoursesOfStudent.add(item.getNumber());
                            // Adds the new checked course to the list of planned courses in database
                            dataBase.setCoursesPlannedById(plannedCoursesOfStudent);
                         //   dataBase.getCurrentStudent().setPlanned(plannedCoursesOfStudent);

                            String text = getActivity().getResources().getString(R.string.course_number_txt) + " " +
                                    item.getNumber() + " " + getActivity().getResources().getString(R.string.added_to_the_list_of_courses);
                            item.setPlannedChecked(true);
                            Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();

                        } else {
                            String text = getActivity().getResources().getString(R.string.course_number_txt) + " "
                                    + item.getNumber() + " " + getActivity().getResources().getString(R.string.exists_in_the_list_of_courses);
                            item.setPlannedChecked(true);
                            Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        plannedCoursesOfStudent.remove(item.getNumber());
                        System.out.println("removed: " + item.getNumber());
                        // Removes the un-checked course from the list of planned courses in database
                        dataBase.setCoursesPlannedById(plannedCoursesOfStudent);
                      //  dataBase.getCurrentStudent().setPlanned(plannedCoursesOfStudent);
                        item.setPlannedChecked(false);
                        String text = getActivity().getResources().getString(R.string.course_number_txt) + " "
                                + item.getNumber() + " " + getActivity().getResources().getString(R.string.removed_from_the_list_of_courses);
                        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
                    }
                    // printPlannedCourses();
                  //  onCheckBoxClickListener.onCheckBoxClicked(v, item); // todo need?
              //  }
            }
        });


        approvePlannedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("save clicked, the planned courses are: ");

                ArrayList<String> courses = dataBase.getCoursesPlannedById();
                for (String s : courses){
                    System.out.println("kk" + s);
                }
              //  dataBase.getCurrentStudent().setPlanned(courses);

                // todo - upload saved list to firebase
                dataBase.updatePlannedCourses();
            }
        });


        showOnlyChosePlanned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            showOnlyPlanedLogic();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onResume() {
        super.onResume();

        ArrayAdapter arrayYearAdapter = new ArrayAdapter(requireContext(), R.layout.dropdownfacultyitem, getResources().getStringArray(R.array.yearArray));
        arrayYearAdapter.getFilter().filter("");
        binding.autocompletechooseyear.setAdapter(arrayYearAdapter);

        ArrayAdapter arraySemesterAdapter = new ArrayAdapter(requireContext(), R.layout.dropdownfacultyitem, getResources().getStringArray(R.array.semesterArray));
        arraySemesterAdapter.getFilter().filter("");
        binding.autocompletechoosesemester.setAdapter(arraySemesterAdapter);

        ArrayAdapter arrayPointsAdapter = new ArrayAdapter(requireContext(), R.layout.dropdownfacultyitem, getResources().getStringArray(R.array.pointsArray));
        arrayPointsAdapter.getFilter().filter("");
        binding.autocompletechoosepoints.setAdapter(arrayPointsAdapter);

        adapter.addCoursesListToAdapter(dataBase.sortCoursesByYearAndType(coursesToShow));
        adapter.notifyDataSetChanged();

        if (showOnlyChosePlanned.isChecked()){ // check if onlyShowPlaned is clicked, and if so response accordingly
            showOnlyPlanedLogic();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showOnlyPlanedLogic(){
        if (showOnlyChosePlanned.isChecked()) {
            ArrayList<Course> planedOnlyList;
            planedOnlyList = adapter.getItems().stream().filter(Course::isPlanned).collect(Collectors.toCollection(ArrayList::new));
            adapter.addCoursesListToAdapter(dataBase.sortCoursesByYearAndType(planedOnlyList));
            adapter.notifyDataSetChanged();
        } else {
            adapter.addCoursesListToAdapter(dataBase.sortCoursesByYearAndType(coursesToShow));
            adapter.notifyDataSetChanged();
        }
    }
}