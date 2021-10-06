package com.example.huji_assistant.Fragments;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.huji_assistant.Chug;
import com.example.huji_assistant.Course;
import com.example.huji_assistant.CourseItemHolder;
import com.example.huji_assistant.CoursesAdapter;
import com.example.huji_assistant.Faculty;
import com.example.huji_assistant.HujiAssistentApplication;
import com.example.huji_assistant.LocalDataBase;
import com.example.huji_assistant.Maslul;
import com.example.huji_assistant.R;
import com.example.huji_assistant.StudentInfo;
import com.example.huji_assistant.SwipeViewHolderItem;
import com.example.huji_assistant.ViewModelApp;
import com.example.huji_assistant.ViewModelAppMainScreen;
import com.example.huji_assistant.databinding.FragmentCoursesBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CoursesFragment extends Fragment {
    private ViewModelApp viewModelApp;
    private ViewModelAppMainScreen viewModelAppCourse;
    FragmentCoursesBinding binding;
    public CourseItemHolder holder = null;
    public CoursesAdapter adapter = null;
    private LocalDataBase db = null;
    public interface endRegistrationButtonClickListener{
        public void onEndRegistrationBtnClicked();
    }

    private String email;
    private String password;
    private String personalName;
    private String familyName;
    FirebaseFirestoreSettings settings;
    String facultyId;
    String chugId;
    String maslulId;
    String degreeType; // first degree, second degree...
    String year;
    String beginnigYearOfDegree;
    String beginSemesterOfDegree;
    String grade;
    TextView facultyTextView;
    StudentInfo currentStudent;
    TextView chugTextView;
    TextView maslulTextView;
    TextView yearTextView;
    TextView degreeTextView;
    RecyclerView recyclerViewCourses;
    LinearLayoutManager coordinatorLayout;
    AutoCompleteTextView dropdowntype;
    AutoCompleteTextView dropdownpoints;

    //FirebaseFirestore firebaseInstancedb = FirebaseFirestore.getInstance();
   // FirebaseFirestore firebaseInstancedb = HujiAssistentApplication.getInstance().getDataBase().getFirestoreDB();
    FirebaseFirestore firebaseInstancedb = FirebaseFirestore.getInstance();

    ArrayList<String> coursesOfStudent = new ArrayList<>(); // todo save in db to pass between activities

    public CoursesFragment.endRegistrationButtonClickListener endRegistrationBtnListener = null;

    public CoursesAdapter.OnItemClickListener onItemClickListener = null;
    public CoursesAdapter.OnCheckBoxClickListener onCheckBoxClickListener = null;
  //  public CoursesAdapter.OnTextBoxClickListener onTextBoxClickListener = null;

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
        dropdowntype = view.findViewById(R.id.autocompletechoosetypeRegisterScreen);
        dropdownpoints = view.findViewById(R.id.autocompletechoosenameRegisterScreen);

        if (holder == null) {
            holder = new CourseItemHolder(recyclerViewCourses);
        }

        ArrayList<Course> courseItems = new ArrayList<>(); // Saves the current courses list

        if (db == null) {
            db = HujiAssistentApplication.getInstance().getDataBase();
        }
        settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firebaseInstancedb.setFirestoreSettings(settings);

        viewModelApp.getStudent().observe(getViewLifecycleOwner(), item->{
             email = item.getEmail();
             password = item.getPassword();
             personalName = item.getPersonalName();
             familyName = item.getFamilyName();
             facultyId = item.getFacultyId();
             chugId = item.getChugId();
             maslulId = item.getMaslulId();
             degreeType = item.getDegree();
             year = item.getYear();
             beginnigYearOfDegree = item.getBeginYear();
             beginSemesterOfDegree = item.getBeginSemester();

             System.out.println("begin year: " + beginnigYearOfDegree);
             System.out.println("begin semester: " + beginSemesterOfDegree);

            Task<DocumentSnapshot> faculties1 = firebaseInstancedb.collection("faculties").document(facultyId)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                 @Override
                 public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                     DocumentSnapshot result = task.getResult();
                     assert result != null;
                     Faculty data = result.toObject(Faculty.class);
                     assert data != null;
                     String facultyName = data.getTitle();
                     String text = facultyName + " " + facultyId;
                     facultyTextView.setText(text);
                     db.setCurrentFaculty(data);
                 }
             });
            String COLLECTION = "coursesTestOnlyCs";
            Task<DocumentSnapshot> chugim1 = firebaseInstancedb.collection(COLLECTION).document(chugId)
                   // .collection("chugimInFaculty").document(chugId)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot result = task.getResult();
                            assert result != null;
                            Chug data = result.toObject(Chug.class);
                            assert data != null;
                            String chugName = data.title;
                            String text = chugName + " " + chugId;
                            chugTextView.setText(text);
                            db.setCurrentChug(data);
                        }
                    });
            System.out.println("rOO" + chugId + " " + maslulId );
            Task<DocumentSnapshot> maslulim1 = firebaseInstancedb.collection(COLLECTION).document(chugId)
                    //.collection("chugimInFaculty").document(chugId)
                    .collection("maslulimInChug").document(maslulId)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot result = task.getResult();
                            assert result != null;
                            Maslul data = result.toObject(Maslul.class);
                            assert data != null;
                            String maslulName = data.getTitle();
                            String text = maslulName + " " + maslulId;
                            maslulTextView.setText(text);
                            db.setCurrentMaslul(data);
                        }
                    });

             //facultyTextView.setText(facultyId);
           //  chugTextView.setText(chugId);
             //maslulTextView.setText(maslulId);
             degreeTextView.setText(degreeType);
             yearTextView.setText(year);


            String ROOT_COLLECTION = "coursesTestOnlyCs";
            Task<QuerySnapshot> document = firebaseInstancedb.collection(ROOT_COLLECTION).document(chugId)
                    .collection("maslulimInChug").document(maslulId).collection("coursesInMaslul")
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            List<DocumentSnapshot> documents = task.getResult().getDocuments();
                     //   }
                   // })

            // Get the list of courses from firebase
        //    Task<QuerySnapshot> document = firebaseInstancedb.collection("courses").document(chugId)
                  //  .collection("maslulim").document(maslulId).collection("coursesInMaslul")
                  //  .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                     //   @Override
                    //    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                          //  List<DocumentSnapshot> documents = task.getResult().getDocuments();

                            ArrayList<Course> coursesInMaslul = new ArrayList<>();

                            for (DocumentSnapshot document1 : documents){
                                // retrieve for each chug id it's name
                                Course course = document1.toObject(Course.class);
                                assert course != null;
                                String courseTitle = course.getName();
                                String courseNumber = course.getNumber();
                                String coursePoints = course.getPoints();
                                String courseType = course.getType();
                                System.out.println("print_of_course" + course.toStringP());
                                coursesInMaslul.add(course);
                            }

                            db.setCoursesRegistration(coursesInMaslul);

                            // show results in recycle view
                            // Create the adapter
                            adapter.addCoursesListToAdapter(coursesInMaslul);
                            recyclerViewCourses.setAdapter(adapter);

                            coordinatorLayout = new LinearLayoutManager(getContext(),
                                    RecyclerView.VERTICAL, false);

                            recyclerViewCourses.setLayoutManager(new LinearLayoutManager(getContext(),
                                    RecyclerView.VERTICAL, false));
                        }
                    });



             /**
             firebaseInstancedb.collection("courses").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                 @Override
                 public void onComplete(@NonNull Task<QuerySnapshot> task) {
                     List<DocumentSnapshot> documents = task.getResult().getDocuments();

                     for (DocumentSnapshot doc : documents) {
                       //  Chug chug = doc.toObject(Chug.class);
                       //  System.out.println(chug.getTitle());
                         String s = doc.getId().toString();
                         System.out.println(s);

                     }
                 }
             });
              */
/**
             firebaseInstancedb.collection("courses").whereArrayContains(chugId, chugId)
                     .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                 @Override
                 public void onComplete(@NonNull Task<QuerySnapshot> task) {

                 }
             });
 */
        });

        ArrayAdapter arrayAdapter = new ArrayAdapter(requireContext(), R.layout.dropdowntypeitem, getResources().getStringArray(R.array.courseType));
        arrayAdapter.getFilter().filter("");
        binding.autocompletechoosetypeRegisterScreen.setAdapter(arrayAdapter);

        dropdowntype.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedValue = (String)(parent.getItemAtPosition(position));
                if (selectedValue.equals("הכל")){
                    ArrayList<Course> list = db.getCoursesRegistration();
                    adapter.addCoursesListToAdapter(list);
                    //arrayAdapter.getFilter().filter("");
                    adapter.notifyDataSetChanged();
                    arrayAdapter.getFilter().filter("");
                    binding.autocompletechoosetypeRegisterScreen.setAdapter(arrayAdapter);

                }
                else {
                    System.out.println("selection1 " + selectedValue);
                    System.out.println("position1 " + position);
                    arrayAdapter.getFilter().filter("");
                    ArrayList<Course> newC = new ArrayList<>();
                    // todo check


                    ArrayList<Course> democ = db.getCoursesRegistration();
                    for (Course demo : democ) {
                        if (demo.getType().equals(selectedValue)) {
                            newC.add(demo);
                        }
                    }

                    for (Course c : newC ) {
                        if (c.getType().equals(selectedValue)) {
                            if (!dropdownpoints.getText().toString().equals("")) {
                                String pointsToFilter = dropdownpoints.getText().toString();
                                if (c.getType().equals(pointsToFilter)) {
                                    newC.add(c);
                                }
                            }
                            else {

                                newC.add(c);
                            }
                        }
                    }

                    // Create the adapter
                    //  CoursesAdapter adapter2 = new CoursesAdapter(getContext());
                    adapter.addCoursesListToAdapter(newC);
                    adapter.notifyDataSetChanged();
                    //  recyclerViewMyCourses.setAdapter(adapter);
                    System.out.println("reached");
                }

            }
        });

        ArrayAdapter arrayAdapter1 = new ArrayAdapter(requireContext(), R.layout.dropdowntypeitem, getResources().getStringArray(R.array.coursePoints));
        arrayAdapter1.getFilter().filter("");
        arrayAdapter.getFilter().filter("");
        binding.autocompletechoosenameRegisterScreen.setAdapter(arrayAdapter1);

        dropdownpoints.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedValue = (String)(parent.getItemAtPosition(position));
                String onlyNumber = selectedValue.replaceAll("נז", "");
                String onlyNumber2 = onlyNumber.replaceAll(" ", "");
                System.out.println("number3: " + onlyNumber2);
                arrayAdapter1.getFilter().filter("");
                arrayAdapter.getFilter().filter("");
                if (selectedValue.equals("הכל")){
                    ArrayList<Course> list = db.getCoursesRegistration();
                    adapter.addCoursesListToAdapter(list);
                    adapter.notifyDataSetChanged();
                   // arrayAdapter1.getFilter().filter("");
                   // arrayAdapter.getFilter().filter("");
                    arrayAdapter1.getFilter().filter("");
                    binding.autocompletechoosenameRegisterScreen.setAdapter(arrayAdapter1);

                }
                else {
                    System.out.println("selection1 " + selectedValue);
                    System.out.println("position1 " + position);
                    arrayAdapter.getFilter().filter("");
                    ArrayList<Course> newC = new ArrayList<>();
                    // todo check
                    ArrayList<Course> democ = db.getCoursesRegistration();
                    for (Course demo : democ) {
                        if (demo.getPoints().equals(onlyNumber2)) {
                            boolean b = dropdowntype.getText().toString().equals("");
                            System.out.println("c " + dropdowntype.getText().toString());
                            System.out.println("b " + b);
                            if ((!dropdowntype.getText().toString().equals("")) || (!dropdowntype.getText().toString().equals("הכל"))){
                                String typeToFilter = dropdowntype.getText().toString();
                                if (demo.getType().equals(typeToFilter)){
                                    newC.add(demo);
                                }
                            }
                            else {
                                newC.add(demo);
                            }
                        }
                    }

                    // Create the adapter
                    //  CoursesAdapter adapter2 = new CoursesAdapter(getContext());
                    adapter.addCoursesListToAdapter(newC);
                    adapter.notifyDataSetChanged();
                    //  recyclerViewMyCourses.setAdapter(adapter);
                    System.out.println("reached");
                }

            }
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

        adapter.setItemCheckBoxListener(new CoursesAdapter.OnCheckBoxClickListener() {
            @Override
            public void onCheckBoxClicked(View v, Course item) {
                if (onCheckBoxClickListener != null){
                   // viewModelAppCourse.set(item);
                    // todo show here pop up?
                    System.out.println("item: " + item.toStringP());


                   // Intent i = new Intent(getActivity(), PopUp.class);
                  //  startActivity(i);
                    //showPopup(v); //todo check
                   // grade="";
                  //  System.out.println("grade: " + grade);

                  //  if (!grade.equals("")) {
                   //     int gradeInt = Integer.parseInt(grade);
                   //     item.setGrade(gradeInt); // todo check
                  //  }
                    // todo validation on grade


                    if (item.getChecked()){
                        coursesOfStudent.add(item.getNumber());
                        String text = "קורס מספר: " + item.getNumber() + " נוסף לרשימת הקורסים";
                        item.setChecked(true);
                        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
                        System.out.println("added: " + item.getNumber());
                    }
                    else{
                        coursesOfStudent.remove(item.getNumber()); //todo check if contains?
                        item.setChecked(false);
                        String text = "קורס מספר: " + item.getNumber() + " הוסר מרשימת הקורסים";
                        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
                        System.out.println("removed: " + item.getNumber());
                    }
                    //todo sort
                    System.out.println("sort: ");
                    printCourses();
                    onCheckBoxClickListener.onCheckBoxClicked(v, item); // todo need?
                }
            }
        });

     //   adapter.setTextBoxClickListener(new CoursesAdapter.OnTextBoxClickListener() {
         //   @Override
         //   public void onTextBoxClick(Course item) {
            //    if (onTextBoxClickListener != null){
             //       onTextBoxClickListener.onTextBoxClick(item);
            //    }
          //  }
       // });



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
        viewModelApp.studentInfoMutableLiveData.observe(getViewLifecycleOwner(), new Observer<StudentInfo>() {
            @Override
            public void onChanged(StudentInfo studentInfo) {
                currentStudent = viewModelApp.studentInfoMutableLiveData.getValue();

            }
        });

        Button endRegistrationBtn = view.findViewById(R.id.buttonEndRegistration);
        endRegistrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (endRegistrationBtnListener != null){

                    FirebaseAuth auth = db.getUsersAuthenticator();
                    auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("RegisterActivity", "registerWithEmail:success");
                                        Toast.makeText(getActivity(), R.string.register_Successfully_message, Toast.LENGTH_LONG).show();                                            //todo: don't allow to continue
                                        FirebaseUser user = auth.getCurrentUser();
                                        // todo upload courses

                                        printCourses();
                                        System.out.println("size: " + coursesOfStudent.size());

                                        assert user != null;

                                        StudentInfo newStudent = new StudentInfo(user.getUid(), email, personalName, familyName,
                                                facultyId, chugId, maslulId, degreeType, year, beginnigYearOfDegree, beginSemesterOfDegree,
                                                coursesOfStudent);

                                        db.addStudent(user.getUid(), email, personalName, familyName,
                                                facultyId, chugId, maslulId, degreeType, year, beginnigYearOfDegree, beginSemesterOfDegree,
                                                coursesOfStudent);

                                        db.setCurrentUser(user);

                                        db.setCurrentStudent(newStudent); // todo check

                                      //  StudentInfo newStudent = new StudentInfo(email.getText().toString(),
                                       //         personalName.getText().toString(), familyName.getText().toString());
                                       // viewModelApp.setStudent(newStudent);


                                        endRegistrationBtnListener.onEndRegistrationBtnClicked();
                                    }else{
                                        Log.w("RegisterActivity", "registerWithEmail:failure", task.getException());
                                        Toast.makeText(getActivity(), R.string.register_failed_message, Toast.LENGTH_LONG).show();                                            //todo: don't allow to continue
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                    // TODO create an object studentInfo
                }
            }
        });



//create an adapter to describe how the items are displayed, adapters are used in several places in android.
//There are multiple variations of this, but this is the basic variant.
        //  ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
//set the spinners adapter to the previously created one.
        // dropdown.setAdapter(adapter);

    }

    public void showPopup(View anchorView) {

        System.out.println("reached top");
        View popupView = getLayoutInflater().inflate(R.layout.popup_layout, null);
        System.out.println("bottom");
        PopupWindow popupWindow = new PopupWindow(popupView,
                RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        System.out.println("bottom1");
        // TextView tv = (TextView) popupView.findViewById(R.id.tv);
        EditText gradeTextView = popupView.findViewById(R.id.editTextNumber);
        Button approveBtn = popupView.findViewById(R.id.approveBtn);
        System.out.println("bottom2");
        // If the PopupWindow should be focusable
        popupWindow.setFocusable(true);
        System.out.println("bottom3");
        // If you need the PopupWindow to dismiss when when touched outside
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        System.out.println("bottom4");
        int[] location = new int[2];

        // Get the View's(the one that was clicked in the Fragment) location
        anchorView.getLocationOnScreen(location);

        // Using location, the PopupWindow will be displayed right under anchorView
        popupWindow.showAtLocation(anchorView, Gravity.NO_GRAVITY,
                location[0], location[1] + anchorView.getHeight());
        System.out.println("bottom5");
        approveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("grade1: " + gradeTextView.getText().toString());
                grade = gradeTextView.getText().toString();
                popupWindow.dismiss();
            }
        });

    }


    private void printCourses(){
        for (int i = 0; i < coursesOfStudent.size(); i++){
            System.out.println(coursesOfStudent.get(i));
        }
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
