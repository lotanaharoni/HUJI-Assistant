package com.example.huji_assistant.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.example.huji_assistant.Chug;
import com.example.huji_assistant.Faculty;
import com.example.huji_assistant.Fragments.EditProfileFragment;

import com.example.huji_assistant.Fragments.GradeFragment;

import com.example.huji_assistant.Fragments.FirstFragment;

import com.example.huji_assistant.Fragments.PlanCoursesFragment;
import com.example.huji_assistant.Fragments.PlannedCoursesFragment;
import com.example.huji_assistant.Fragments.SettingsFragment;
import com.example.huji_assistant.Fragments.TextViewFragment;
import com.example.huji_assistant.Maslul;
import com.example.huji_assistant.Model;
import com.example.huji_assistant.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;

import com.example.huji_assistant.Course;
import com.example.huji_assistant.CoursesAdapter;
import com.example.huji_assistant.Fragments.AddCourseFragment;
import com.example.huji_assistant.Fragments.CourseInfoFragment;
import com.example.huji_assistant.Fragments.CoursesFragment;
import com.example.huji_assistant.Fragments.MainScreenFragment;
import com.example.huji_assistant.Fragments.MyCoursesFragment;
import com.example.huji_assistant.HujiAssistentApplication;
import com.example.huji_assistant.LocalDataBase;
import com.example.huji_assistant.StudentInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.Locale;
import android.content.res.Configuration;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainScreenActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public LocalDataBase dataBase = null;
    public static final int CAMERA_PERM_CODE = 101;
    private static final int CAMERA_TYPE = 1;
    private DatabaseReference root;
    private StorageReference reference;
    FragmentManager fragmentManager;
    MainScreenFragment mainscreenfragment;
    MainScreenFragment mainscreenfragment2;
    EditProfileFragment editProfileFragment;
    EditProfileFragment editProfileFragment2;
    MyCoursesFragment myCoursesFragment;
    MyCoursesFragment myCoursesFragment2;
    SettingsFragment settingsFragment;
    SettingsFragment settingsFragment2;
    PlanCoursesFragment planCoursesFragment;
    PlanCoursesFragment planCoursesFragment2;
    AddCourseFragment addCourseFragment;
    AddCourseFragment addCourseFragment2;
    PlannedCoursesFragment plannedCoursesFragment;
    ProgressBar progressBar;
    FirebaseFirestoreSettings settings;
    private ActivityResultLauncher<Intent> cameraUploadActivityResultLauncher;
    String currentPhotoPath;
    private DrawerLayout moreInfoDrawerLayout;
    private ImageView logoutImageView;
    private TextView changeLanguageTextView;
   // ArrayList<Course> coursesOfStudentByCourse = new ArrayList<>();
    ListenerRegistration listener;
    FirebaseFirestore firebaseInstancedb = FirebaseFirestore.getInstance();
  //  FirebaseFirestore firebaseInstancedb = HujiAssistentApplication.getInstance().getDataBase().getFirestoreDB();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainscreen2);

        // db
        if (dataBase == null) {
            dataBase = HujiAssistentApplication.getInstance().getDataBase();
        }

        settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firebaseInstancedb.setFirestoreSettings(settings);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        // application singleton
        HujiAssistentApplication application = (HujiAssistentApplication) getApplication();

        System.out.println("current student: " + dataBase.getCurrentUser().toStringP());

        // firebase listener for changes in current student
        try {
            final DocumentReference docRef = firebaseInstancedb.collection("students").document(dataBase.getCurrentUser().getId());
            docRef.addSnapshotListener((value, error) -> {
                if (error != null) {
                    Log.w("ERROR", "Listen failure", error);
                    return;
                }
                if ((value != null) && (value.exists())) {
                    StudentInfo currentStudent = value.toObject(StudentInfo.class);
                    assert currentStudent != null;
                    // todo set as current student
                    dataBase.setCurrentStudent(currentStudent);
                    dataBase.setGradesOfStudentMap(currentStudent.getCoursesGrades()); // todo check
                    // todo the data from firebase should be saved in local
                    System.out.println("updated student: " + currentStudent.toStringP());
                    System.out.println("updated courses list: ");
                    currentStudent.printCourses();
                    getNewCoursesList();
                    getFacultyInfo();
                    getChugInfo();
                    getMaslulInfo();
                }
            });
        }
        catch(Exception e){
            System.out.println("error loading data from firebase");
        }

       // ArrayList<String> coursesOfStudentById = dataBase.getCurrentStudent().getCourses();
       // ArrayList<Course> coursesOfStudentByCourse = new ArrayList<>();
/**
        // Gets the courses of the student from firebase and updates in local data base
        Task<QuerySnapshot> courses1 = firebaseInstancedb.collection("courses").document(dataBase.getCurrentUser().getChugId())
                .collection("maslulimInChug").document(dataBase.getCurrentUser().getMaslulId())
                .collection("coursesInMaslul")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        ArrayList<Course> coursesFromFireBase = new ArrayList<>();
                        for (DocumentSnapshot document1 : documents){
                            Course course = document1.toObject(Course.class);
                            System.out.println("course2 "+ course.toStringP());
                            coursesFromFireBase.add(course);
                        }

                        for (String id : coursesOfStudentById){
                            for (Course course: coursesFromFireBase){
                                if (course.getNumber().equals(id)){
                                    coursesOfStudentByCourse.add(course);
                                }
                            }
                        }
                        // todo save the list of courses of current student to db
                        // show changes on course list in adapter
                        dataBase.setCoursesOfCurrentStudent(coursesOfStudentByCourse);
                    }
                });*/

        // todo check if is updated in fragments

        logoutImageView = findViewById(R.id.logoutImageView);
        progressBar = findViewById(R.id.progressBar1);
        progressBar.setVisibility(View.INVISIBLE); // todo revert
        changeLanguageTextView = findViewById(R.id.change_language_textView);

        //   logoutImageView.setVisibility(View.VISIBLE);
     //   logoutImageView.setEnabled(true);
        moreInfoDrawerLayout = findViewById(R.id.drawer_layout_more_info);
        NavigationView navigationView = findViewById(R.id.nav_view);
        moreInfoDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        navigationView.setNavigationItemSelectedListener(this);

       // settingsDrawerLayout = findViewById(R.id.drawer_layout_settings);
      //  NavigationView settingsNavView = findViewById(R.id.nav_view_settings);
     //   settingsDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
      //  settingsNavView.setNavigationItemSelectedListener(this);


        FragmentContainerView mainFragmentView = findViewById(R.id.mainfragment);
        CoursesFragment coursesFragment = new CoursesFragment();
        myCoursesFragment = new MyCoursesFragment();
        myCoursesFragment2 = new MyCoursesFragment();
        CourseInfoFragment courseInfoFragment = new CourseInfoFragment();

        AddCourseFragment addCourseFragment = new AddCourseFragment();
        PlannedCoursesFragment plannedCoursesFragment = new PlannedCoursesFragment();
       // ProfilePageFragment profilePageFragment = new ProfilePageFragment();
        EditProfileFragment editProfileFragment = new EditProfileFragment();
        PlanCoursesFragment planCoursesFragment = new PlanCoursesFragment();
        SettingsFragment settingsFragment = new SettingsFragment();
        GradeFragment gradeFragment = new GradeFragment();
        CourseInfoFragment courseInfoFragment1 = new CourseInfoFragment();

       // addCourseFragment = new AddCourseFragment();
        addCourseFragment2 = new AddCourseFragment();
        // ProfilePageFragment profilePageFragment = new ProfilePageFragment();
      //  editProfileFragment = new EditProfileFragment();
        editProfileFragment2 = new EditProfileFragment();
       // planCoursesFragment = new PlanCoursesFragment();
        planCoursesFragment2 = new PlanCoursesFragment();
      //  settingsFragment = new SettingsFragment();
        settingsFragment2 = new SettingsFragment();

        FloatingActionButton openCameraBtn = findViewById(R.id.open_camera_floating_button);
        root = FirebaseDatabase.getInstance().getReference("Image");
        reference = FirebaseStorage.getInstance().getReference();

        mainscreenfragment = new MainScreenFragment();
        mainscreenfragment2 = new MainScreenFragment();

        getSupportFragmentManager().beginTransaction().replace(mainFragmentView.getId(), mainscreenfragment, "MAIN_FRAGMENT")
                .commit();

        findViewById(R.id.buttonMoreInfoMapActivity).setOnClickListener(v -> {
            moreInfoDrawerLayout.openDrawer(GravityCompat.START);
        });

        findViewById(R.id.settings).setOnClickListener(v -> {
            // todo handle
            SettingsFragment myFragment = (SettingsFragment)getSupportFragmentManager().findFragmentByTag("SETTINGS_FRAGMENT");
            if (myFragment != null && myFragment.isVisible()) {
                // add your code here
                return;
            }

            getSupportFragmentManager().beginTransaction().setCustomAnimations(
                    R.anim.fade_in,  // enter
                            R.anim.slide_out,  // exit
                            R.anim.slide_in,   // popEnter
                            R.anim.fade_out  // popExit
            )
                    .replace(mainFragmentView.getId(), settingsFragment, "SETTINGS_FRAGMENT")
                    .addToBackStack(null)
                    .commit();

           // getSupportFragmentManager().beginTransaction().setCustomAnimations(
            //        R.anim.fade_in,  // enter
            //        R.anim.slide_out,  // exit
            //        R.anim.slide_in,   // popEnter
            //        R.anim.fade_out  // popExit
           // )
               //     .replace(mainFragmentView.getId(), settingsFragment, "SETTINGS_FRAGMENT").addToBackStack(null).commit();

            System.out.println("settings clicked");
        });

        findViewById(R.id.qrscan).setOnClickListener(v -> {
            startActivity(new Intent(this, ScanQrActivity.class));
            finish();
//            System.out.println("qrscan clicked");
        });

        openCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("camera clicked");
                askCameraPermissions();
            }
        });



        cameraUploadActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();
                            File f = new File(currentPhotoPath);

                            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            Uri contentUri = Uri.fromFile(f);
                            mediaScanIntent.setData(contentUri);
                            sendBroadcast(mediaScanIntent);

                            uploadToFirebase(contentUri);
                        }
                    }
                });

        mainscreenfragment.coursesPlanButtonListenerBtn = new MainScreenFragment.coursesPlanButtonListenerBtn() {
            @Override
            public void onPlanCoursesButtonClicked() {
                getSupportFragmentManager().beginTransaction().setCustomAnimations(
                        R.anim.fade_in,  // enter
                        R.anim.slide_out,  // exit
                        R.anim.slide_in,   // popEnter
                        R.anim.fade_out  // popExit
                )
                        // .replace(mainFragmentView.getId(), coursesFragment, "COURSES_FRAGMENT").addToBackStack(null).commit();
                        .replace(mainFragmentView.getId(), planCoursesFragment, "PLAN_COURSES_FRAGMENT").addToBackStack(null).commit();
            }
        };

        mainscreenfragment.onSearchClickListener = new MainScreenFragment.onSearchClickListener() {
            @Override
            public void onSearchClick() {
                getSupportFragmentManager().beginTransaction().setCustomAnimations(
                        R.anim.fade_in,  // enter
                        R.anim.slide_out,  // exit
                        R.anim.slide_in,   // popEnter
                        R.anim.fade_out  // popExit
                )
                        // .replace(mainFragmentView.getId(), coursesFragment, "COURSES_FRAGMENT").addToBackStack(null).commit();
                        .replace(mainFragmentView.getId(), courseInfoFragment1, "INFO_COURSE_FRAGMENT").addToBackStack(null).commit();
            }
        };

        mainscreenfragment.coursesPlanScreenListenerBtn = new MainScreenFragment.coursesPlanScreenListenerBtn() {
            @Override
            public void onPlanCoursesScreenClicked() {
                getSupportFragmentManager().beginTransaction().setCustomAnimations(
                        R.anim.fade_in,  // enter
                        R.anim.slide_out,  // exit
                        R.anim.slide_in,   // popEnter
                        R.anim.fade_out  // popExit
                )
                        // .replace(mainFragmentView.getId(), coursesFragment, "COURSES_FRAGMENT").addToBackStack(null).commit();
                        .replace(mainFragmentView.getId(), plannedCoursesFragment, "PLAN_SCREEN_FRAGMENT").addToBackStack(null).commit();
            }
        };

        mainscreenfragment.editInfoButtonListener = new MainScreenFragment.editInfoButtonListener(){

            @Override
            public void onEditInfoButtonClicked() {
                getSupportFragmentManager().beginTransaction().setCustomAnimations(
                        R.anim.fade_in,  // enter
                        R.anim.slide_out,  // exit
                        R.anim.slide_in,   // popEnter
                        R.anim.fade_out  // popExit
                )
                        // .replace(mainFragmentView.getId(), coursesFragment, "COURSES_FRAGMENT").addToBackStack(null).commit();
                        .replace(mainFragmentView.getId(), editProfileFragment, "EDIT_INFO_FRAGMENT").addToBackStack(null).commit();
            }
        };

        mainscreenfragment.showAttendanceButtonListener = new MainScreenFragment.showAttendanceButtonListener(){

            @Override
            public void onShowAttendanceButtonClicked() {
                startActivity(new Intent(MainScreenActivity.this, ShowAttendanceActivity.class));
                finish();
            }
        };

        mainscreenfragment.uploadPicturesButtonListenerBtn = new MainScreenFragment.UploadPictureButtonListener(){
            @Override
            public void onUploadPictureButtonClicked() {
                startActivity(new Intent(MainScreenActivity.this, CaptureImageActivity.class));
                finish();
            }
        };

        // todo remove
        myCoursesFragment.addGradeListener= new CoursesAdapter.AddGradeListener() {
            @Override
            public void onAddGradeClick(Course item, String grade) {
                getSupportFragmentManager().beginTransaction().setCustomAnimations(
                        R.anim.fade_in,  // enter
                        R.anim.slide_out,  // exit
                        R.anim.slide_in,   // popEnter
                        R.anim.fade_out  // popExit
                )
                        .replace(mainFragmentView.getId(), gradeFragment,"GRADE_FRAGMENT").addToBackStack(null).commit();
            }
        };


        myCoursesFragment.addCourseListener = new MyCoursesFragment.addCourseButtonClickListener() {
            @Override
            public void addCourseBtnClicked() {
                getSupportFragmentManager().beginTransaction().setCustomAnimations(
                        R.anim.fade_in,  // enter
                        R.anim.slide_out,  // exit
                        R.anim.slide_in,   // popEnter
                        R.anim.fade_out  // popExit
                )
                        .replace(mainFragmentView.getId(), addCourseFragment, "ADD_COURSES_FRAGMENT").addToBackStack(null).commit();
            }
        };

        myCoursesFragment2.addCourseListener = new MyCoursesFragment.addCourseButtonClickListener() {
            @Override
            public void addCourseBtnClicked() {
                getSupportFragmentManager().beginTransaction().setCustomAnimations(
                        R.anim.fade_in,  // enter
                        R.anim.slide_out,  // exit
                        R.anim.slide_in,   // popEnter
                        R.anim.fade_out  // popExit
                )
                        .replace(mainFragmentView.getId(), addCourseFragment, "ADD_COURSES_FRAGMENT").addToBackStack(null).commit();
            }
        };
/**
        addCourseFragment.addCourseToListButtonClickListener = new AddCourseFragment.addCourseToListButtonClickListener() {
            @Override
            public void addCourseToListBtnClicked(String id) {
                //todo here check
               // System.out.println("course id to add: " + id);
                // todo go back to previous fragment

                /**
                getSupportFragmentManager().beginTransaction().setCustomAnimations(
                        R.anim.fade_in,  // enter
                        R.anim.slide_out,  // exit
                        R.anim.slide_in,   // popEnter
                        R.anim.fade_out  // popExit
                )
                        // .replace(mainFragmentView.getId(), coursesFragment, "COURSES_FRAGMENT").addToBackStack(null).commit();
                        .replace(mainFragmentView.getId(), myCoursesFragment, "MY_COURSES_FRAGMENT").addToBackStack(null).commit();
            }
            }
        };*/

        mainscreenfragment.myCoursesButtonListenerBtn = new MainScreenFragment.myCoursesButtonListener() {
            @Override
            public void onMyCoursesButtonClicked() {
                getSupportFragmentManager().beginTransaction().setCustomAnimations(
                        R.anim.fade_in,  // enter
                        R.anim.slide_out,  // exit
                        R.anim.slide_in,   // popEnter
                        R.anim.fade_out  // popExit
                )
                       // .replace(mainFragmentView.getId(), coursesFragment, "COURSES_FRAGMENT").addToBackStack(null).commit();
                  .replace(mainFragmentView.getId(), myCoursesFragment, "MY_COURSES_FRAGMENT").addToBackStack(null).commit();
            }
        };
/**
        myCoursesFragment.onCheckBoxClickListener = new CoursesAdapter.OnCheckBoxClickListener() {
            @Override
            public void onCheckBoxClicked(View v, Course item) {
                System.out.println("----------------item checked: " + item.toStringP());
                // todo show here pop up?
                if (item.getChecked()) {
                    grade = "";
                    //  Intent i = new Intent();
                    MainActivity.PopUpWindowActivity popUpWindow = new MainScreenActivity().PopUpWindowActivity();
                    popUpWindow.showPopup(v, item); //todo check
                    System.out.println("grade of course: " + item.getGrade());
                }
            }
        };*/

        myCoursesFragment.deleteClickListener = new CoursesAdapter.DeleteClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDeleteClick(View v, Course item) {
                System.out.println("delete button in main activity");
                /**
                DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE: {
                           // dataBase.logoutUser();
                           //// startActivity(new Intent(MainScreenActivity.this, MainActivity.class));
                            //finish();
                            dataBase.removeCourseFromCurrentList(item.getNumber());
                            break;
                        }
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(MainScreenActivity.this);
                builder.setMessage("האם אתה בטוח שברצונך למחוק את הקורס? הפעולה אינה ניתנת לביטול")
                        .setPositiveButton(R.string.positive_answer, dialogClickListener)
                        .setNegativeButton(R.string.negative_answer, dialogClickListener).show();
                 */


               // dataBase.removeCourseFromCurrentList(item.getNumber());
            }
        };

        myCoursesFragment2.deleteClickListener = new CoursesAdapter.DeleteClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDeleteClick(View v, Course item) {
                System.out.println("delete button in main activity");
            }
        };

        settingsFragment.sendEmailBtnListener = new SettingsFragment.sendEmailBtnListener() {
            @Override
            public void onSendEmailBtnClicked(String email) {
                sendEmail(email);
            }
        };

        myCoursesFragment.onItemClickListener = new CoursesAdapter.OnItemClickListener() {
            @Override
            public void onClick(Course item) {
                getSupportFragmentManager().beginTransaction().setCustomAnimations(
                        R.anim.fade_in,  // enter
                        R.anim.slide_out,  // exit
                        R.anim.slide_in,   // popEnter
                        R.anim.fade_out  // popExit
                )
                        .replace(mainFragmentView.getId(), courseInfoFragment, "SELECT_COURSE_ITEM_FRAGMENT").addToBackStack(null).commit();
            }
        };

        settingsFragment.changeLanguageBtnListener = new SettingsFragment.changeLanguageBtnListener() {
            @Override
            public void onClickChangeLanguage() {
                showChangeLanguageDialog();
            }
        };

        myCoursesFragment2.onItemClickListener = new CoursesAdapter.OnItemClickListener() {
            @Override
            public void onClick(Course item) {
                getSupportFragmentManager().beginTransaction().setCustomAnimations(
                        R.anim.fade_in,  // enter
                        R.anim.slide_out,  // exit
                        R.anim.slide_in,   // popEnter
                        R.anim.fade_out  // popExit
                )
                        .replace(mainFragmentView.getId(), courseInfoFragment, "SELECT_COURSE_ITEM_FRAGMENT").addToBackStack(null).commit();
            }
        };
     // end of on create

        logoutImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE: {
                            dataBase.logoutUser();
                            startActivity(new Intent(MainScreenActivity.this, MainActivity.class));
                            finish();
                            break;
                        }
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(MainScreenActivity.this);
                builder.setMessage(R.string.logout).setPositiveButton(R.string.positive_answer, dialogClickListener)
                        .setNegativeButton(R.string.negative_answer, dialogClickListener).show();
            }
        });

        // todo maybe reset
        //changeLanguageTextView.setOnClickListener(new View.OnClickListener() {
        //    @Override
          //  public void onClick(View v) {
          //      showChangeLanguageDialog();
         //   }
       // });

    }

    private void showChangeLanguageDialog() {
        final String[] listItems = {"English", "עברית"};
        AlertDialog.Builder mbuilder = new AlertDialog.Builder(MainScreenActivity.this);
        mbuilder.setTitle("Choose Languae...");
        int languageIndex = dataBase.getLanguageIndex();
        mbuilder.setSingleChoiceItems(listItems, languageIndex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0){
                    setLocale("en");
                }
                else if (which == 1){
                    setLocale("he");
                }

                dialog.dismiss();
            }
        });

        AlertDialog mDialog = mbuilder.create();
        mDialog.show();
    }

    private void goToUrl(String s) {
        Uri url = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW, url));
    }

    @SuppressLint("IntentReset")
    private void sendEmail(String email){
        String [] emailList = email.split(",");

        StudentInfo currentUser = dataBase.getCurrentStudent();
        String[] CC = {""};
        String link = "link"; // todo set link
        String emailSubject = "הזמנה לאפליקציה" + " " + " Huji assistent";
        String emailFrom = currentUser.getPersonalName() + " " + currentUser.getFamilyName();
        String emailText = "שלום! החבר שלך" + " " + emailFrom + " " + " רוצה להזמין אותך להצטרף לאפליקציה"
                + " " + "Huji assistent!" + " " +  "קישור לאפליקציה:" + " " + link;

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("message/rfc822");

        emailIntent.putExtra(Intent.EXTRA_EMAIL, emailList);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, emailText);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "There is no email client installed.", Toast.LENGTH_LONG).show();
        }
    }

    private void getChugInfo(){
        if (dataBase.getCurrentChug() == null){
            try{
                String COLLECTION = "coursesTestOnlyCs";
                Task<DocumentSnapshot> chugim1 = firebaseInstancedb.collection(COLLECTION).document(dataBase.getCurrentStudent().getChugId())
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot result = task.getResult();
                                assert result != null;
                                Chug data = result.toObject(Chug.class);
                                assert data != null;
                                String chugName = data.getTitle();
                                System.out.println("got chug: " + chugName);
                                dataBase.setCurrentChug(data);
                            }
                        });
            }
            catch (Exception e){
                System.out.println("couldn't get chug");
            }
        }
    }

    private void getFacultyInfo(){
        if (dataBase.getCurrentFaculty() == null){
            try{
                String COLLECTION = "faculties";
                Task<DocumentSnapshot> faculties1 = firebaseInstancedb.collection(COLLECTION).document(dataBase.getCurrentStudent().getFacultyId())
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot result = task.getResult();
                                assert result != null;
                                Faculty data = result.toObject(Faculty.class);
                                assert data != null;
                                String facultyName = data.getTitle();
                                System.out.println("got faculty: " + facultyName);
                                dataBase.setCurrentFaculty(data);
                            }
                        });
            }
            catch (Exception e){
                System.out.println("couldn't get faculty");
            }
        }
    }

    private void getMaslulInfo(){
        // If the sign up process was from other phone or the data was erased
        if (dataBase.getCurrentMaslul() == null){

            try{
                String COLLECTION = "coursesTestOnlyCs";
                Task<DocumentSnapshot> maslulim1 = firebaseInstancedb.collection(COLLECTION).document(dataBase.getCurrentStudent().getChugId())
                        .collection("maslulimInChug").document(dataBase.getCurrentStudent().getMaslulId())
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot result = task.getResult();
                                assert result != null;
                                Maslul data = result.toObject(Maslul.class);
                                assert data != null;
                                String maslulName = data.getTitle();
                                System.out.println("got maslul: " + maslulName);
                                String totalPoints = data.getTotalPoints();
                                dataBase.setCurrentMaslul(data);
                            }
                        });
            }
            catch (Exception e){
                System.out.println("couldn't get maslul");
            }
        }
    }

    private void getNewCoursesList(){

        ArrayList<String> coursesOfStudentById = dataBase.getCurrentStudent().getCourses();
        ArrayList<Course> coursesOfStudentByCourse = new ArrayList<>();
        String ROOT_COLLECTION = "coursesTestOnlyCs";

        // Gets the courses of the student from firebase and updates in local data base
        Task<QuerySnapshot> courses1 = firebaseInstancedb.collection(ROOT_COLLECTION).document(dataBase.getCurrentUser().getChugId())
                .collection("maslulimInChug").document(dataBase.getCurrentUser().getMaslulId())
                .collection("coursesInMaslul")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        ArrayList<Course> coursesFromFireBase = new ArrayList<>();
                        for (DocumentSnapshot document1 : documents){
                            Course course = document1.toObject(Course.class);
                            System.out.println("course2 "+ course.toStringP());
                            coursesFromFireBase.add(course);
                        }

                        int currentPointsSum = 0; // points in degree
                        int currentMandatoryPoints = 0; // hova points
                        int currentCornerStonePoints = 0; // avnei pina
                        int currentMandatoryChoosePoints = 0; // hova choose
                        int currentChoosePoints = 0; // choose
                        int currentSuppPoints = 0; // mashlimim

                        // Save the list of all courses in the student's maslul
                        dataBase.setCoursesFromFireBase(coursesFromFireBase);

                        for (String id : coursesOfStudentById){
                            for (Course course: coursesFromFireBase){
                                if (course.getNumber().equals(id)){
                                    course.setIsFinished(true);
                                    currentPointsSum += Integer.parseInt(course.getPoints());
                                    String type = course.getType();
                                    switch (type){
                                        case "לימודי חובה":
                                            currentMandatoryPoints += Integer.parseInt(course.getPoints());
                                            break;
                                        case "לימודי חובת בחירה":
                                            currentMandatoryChoosePoints += Integer.parseInt(course.getPoints());
                                            break;
                                        case "קורסי בחירה":
                                            currentChoosePoints += Integer.parseInt(course.getPoints());
                                            break;
                                        case "משלימים":
                                            currentSuppPoints += Integer.parseInt(course.getPoints());
                                            break;
                                        case "אבני פינה":
                                            currentCornerStonePoints += Integer.parseInt(course.getPoints());
                                            break;
                                    }
                                    coursesOfStudentByCourse.add(course);
                                }
                            }
                        }
                        // Save the current points sum of the current student
                        dataBase.setCurrentPointsSum(currentPointsSum);
                        dataBase.setCurrentMandatoryPoints(currentMandatoryPoints);
                        dataBase.setCurrentMandatoryChoosePoints(currentMandatoryChoosePoints);
                        dataBase.setCurrentChoosePoints(currentChoosePoints);
                        dataBase.setCurrentSuppPoints(currentSuppPoints);
                        dataBase.setCurrentCornerStonesPoints(currentCornerStonePoints);
                        // todo save the list of courses of current student to db
                        // show changes on course list in adapter
                        dataBase.setCoursesOfCurrentStudent(coursesOfStudentByCourse);
                    }
                });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.aguda:
                goToUrl("https://www.aguda.org.il/");
                break;
            case R.id.kolzchut:
                goToUrl("https://www.kolzchut.org.il/he/%D7%A1%D7%98%D7%95%D7%93%D7%A0%D7%98%D7%99%D7%9D");
                break;
            case R.id.link_to_drive:
                goToUrl("https://drive.google.com/drive/u/3/folders/0B4NFaiXelmmkelRNeFpRMHlVX2M?resourcekey=0-5b6GE4omL97bd0wCXvzv4w");
                break;
            case R.id.rishumnet:
                goToUrl("https://rishum-net.huji.ac.il/site/student/login.asp");
                break;
            case R.id.recommendations:
                goToUrl("https://drive.google.com/drive/u/3/folders/1BtcBIpl_zTht1KWKR7Qjl_RoavkmfQG0");
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (moreInfoDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            moreInfoDrawerLayout.closeDrawer(GravityCompat.START);
            return;
        } else {
            MainScreenFragment myFragment = (MainScreenFragment) getSupportFragmentManager().findFragmentByTag("MAIN_FRAGMENT");
            SettingsFragment settingsFragment = (SettingsFragment) getSupportFragmentManager().findFragmentByTag("SETTINGS_FRAGMENT");
            SettingsFragment settingsFragment2 = (SettingsFragment) getSupportFragmentManager().findFragmentByTag("SETTINGS_FRAGMENT2");
            EditProfileFragment editProfileFragment = (EditProfileFragment) getSupportFragmentManager().findFragmentByTag("EDIT_INFO_FRAGMENT");
            EditProfileFragment editProfileFragment2 = (EditProfileFragment) getSupportFragmentManager().findFragmentByTag("EDIT_INFO_FRAGMENT2");
            MyCoursesFragment myCoursesFragment = (MyCoursesFragment) getSupportFragmentManager().findFragmentByTag("MY_COURSES_FRAGMENT");
            MyCoursesFragment myCoursesFragment2 = (MyCoursesFragment) getSupportFragmentManager().findFragmentByTag("MY_COURSES_FRAGMENT2");
            PlanCoursesFragment planCoursesFragment = (PlanCoursesFragment) getSupportFragmentManager().findFragmentByTag("PLAN_COURSES_FRAGMENT");
            PlanCoursesFragment planCoursesFragment2 = (PlanCoursesFragment) getSupportFragmentManager().findFragmentByTag("PLAN_COURSES_FRAGMENT2");
            AddCourseFragment addCourseFragment = (AddCourseFragment) getSupportFragmentManager().findFragmentByTag("ADD_COURSES_FRAGMENT");
            AddCourseFragment addCourseFragment2 = (AddCourseFragment) getSupportFragmentManager().findFragmentByTag("ADD_COURSES_FRAGMENT2");


            if (myFragment != null && myFragment.isVisible()) {
                DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE: {
                            finishAffinity();
                            System.exit(0);
                            break;
                        }
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.close_app).setPositiveButton(R.string.positive_answer, dialogClickListener)
                        .setNegativeButton(R.string.negative_answer, dialogClickListener).show();
            } else if ((settingsFragment != null && settingsFragment.isVisible())){
                changeSettingsFragment(settingsFragment, mainscreenfragment, "MAIN_FRAGMENT");}
            else if ((settingsFragment2 != null && settingsFragment2.isVisible())){
            changeSettingsFragment(settingsFragment2, mainscreenfragment, "MAIN_FRAGMENT");}
            //
            else if ((editProfileFragment != null && editProfileFragment.isVisible())){
                changeEditProfileFragment(editProfileFragment, mainscreenfragment, "MAIN_FRAGMENT");}
            else if ((editProfileFragment2 != null && editProfileFragment2.isVisible())){
                changeEditProfileFragment(editProfileFragment2, mainscreenfragment, "MAIN_FRAGMENT");}
            //
            else if ((myCoursesFragment != null && myCoursesFragment.isVisible())){
                changeMyCoursesFragment(myCoursesFragment, mainscreenfragment, "MAIN_FRAGMENT");}
            else if ((myCoursesFragment2 != null && myCoursesFragment2.isVisible())){
                changeMyCoursesFragment(myCoursesFragment2, mainscreenfragment, "MAIN_FRAGMENT");}
            //
            else if ((planCoursesFragment != null && planCoursesFragment.isVisible())){
                changePlanCoursesFragment(planCoursesFragment, mainscreenfragment, "MAIN_FRAGMENT");}
            else if ((planCoursesFragment2 != null && planCoursesFragment2.isVisible())){
                changePlanCoursesFragment(planCoursesFragment2, mainscreenfragment, "MAIN_FRAGMENT");}
            //
            else if ((addCourseFragment != null && addCourseFragment.isVisible())){
                changeAddCoursesFragment(addCourseFragment, myCoursesFragment, "MY_COURSES_FRAGMENT");}
            else if ((addCourseFragment2 != null && addCourseFragment2.isVisible())){
                changeAddCoursesFragment(addCourseFragment2, myCoursesFragment, "MY_COURSES_FRAGMENT");}
            //
            else {
                super.onBackPressed();
            }
        }
    }

    private void askCameraPermissions() {
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        }else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {

                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "com.example.android.fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    cameraUploadActivityResultLauncher.launch(takePictureIntent);
                }
            }
        }
    }

    private void uploadToFirebase(Uri uri) {
        String name = new SimpleDateFormat("dd-MM-yyyy-HH:mm:ss").format(new Date()) +
                "_" + dataBase.getCurrentStudent().getPersonalName() + "_" +
                dataBase.getCurrentStudent().getFamilyName() + ".jpg";
        StorageReference fileRef = reference.child("Camera_images/" + name);
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Model model = new Model(uri.toString(), name, CAMERA_TYPE);
                        String modelId = root.push().getKey();
                        assert modelId != null;
                        root.child(modelId).setValue(model);
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(MainScreenActivity.this, R.string.upload_Successfully_message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                progressBar.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(MainScreenActivity.this, R.string.upload_failed_message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private File createImageFile() throws IOException {
        String imageFileName = new SimpleDateFormat("dd-MM-yyyy").format(new Date());;
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
        dataBase.saveLocale(lang);

        fragmentManager = getSupportFragmentManager();

        MyCoursesFragment myCoursesFragment4 = (MyCoursesFragment) getSupportFragmentManager().findFragmentByTag("MY_COURSES_FRAGMENT");
        MyCoursesFragment myCoursesFragment3 = (MyCoursesFragment) getSupportFragmentManager().findFragmentByTag("MY_COURSES_FRAGMENT2");

        // myCourses
        if (myCoursesFragment3 != null && myCoursesFragment3.isVisible()) {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(
                    R.anim.fade_in,  // enter
                    R.anim.slide_out,  // exit
                    R.anim.slide_in,   // popEnter
                    R.anim.fade_out  // popExit
            )
                    .replace(myCoursesFragment3.getId(), myCoursesFragment, "MY_COURSES_FRAGMENT").addToBackStack(null).commit();
        }

        if (myCoursesFragment4 != null && myCoursesFragment4.isVisible()) {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(
                    R.anim.fade_in,  // enter
                    R.anim.slide_out,  // exit
                    R.anim.slide_in,   // popEnter
                    R.anim.fade_out  // popExit
            )
                    .replace(myCoursesFragment4.getId(), myCoursesFragment2, "MY_COURSES_FRAGMENT2").addToBackStack(null).commit();
        }

        // editInfo
        EditProfileFragment editProfileFragment4 = (EditProfileFragment) getSupportFragmentManager().findFragmentByTag("EDIT_INFO_FRAGMENT");
        EditProfileFragment editProfileFragment3 = (EditProfileFragment) getSupportFragmentManager().findFragmentByTag("EDIT_INFO_FRAGMENT2");

        if (editProfileFragment3 != null && editProfileFragment3.isVisible()) {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(
                    R.anim.fade_in,  // enter
                    R.anim.slide_out,  // exit
                    R.anim.slide_in,   // popEnter
                    R.anim.fade_out  // popExit
            )
                    .replace(editProfileFragment3.getId(), editProfileFragment, "EDIT_INFO_FRAGMENT").addToBackStack(null).commit();
        }

        if (editProfileFragment4 != null && editProfileFragment4.isVisible()) {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(
                    R.anim.fade_in,  // enter
                    R.anim.slide_out,  // exit
                    R.anim.slide_in,   // popEnter
                    R.anim.fade_out  // popExit
            )
                    .replace(editProfileFragment4.getId(), editProfileFragment2, "EDIT_INFO_FRAGMENT2").addToBackStack(null).commit();
        }

        // settingsFragment
        SettingsFragment settingsFragment4 = (SettingsFragment) getSupportFragmentManager().findFragmentByTag("SETTINGS_FRAGMENT");
        SettingsFragment settingsFragment3 = (SettingsFragment) getSupportFragmentManager().findFragmentByTag("SETTINGS_FRAGMENT2");

        if (settingsFragment3 != null && settingsFragment3.isVisible()) {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(
                    R.anim.fade_in,  // enter
                    R.anim.slide_out,  // exit
                    R.anim.slide_in,   // popEnter
                    R.anim.fade_out  // popExit
            )
                    .replace(settingsFragment3.getId(), settingsFragment, "SETTINGS_FRAGMENT").addToBackStack(null).commit();
        }

        if (settingsFragment4 != null && settingsFragment4.isVisible()) {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(
                    R.anim.fade_in,  // enter
                    R.anim.slide_out,  // exit
                    R.anim.slide_in,   // popEnter
                    R.anim.fade_out  // popExit
            )
                    .replace(settingsFragment4.getId(), settingsFragment2, "SETTINGS_FRAGMENT2").addToBackStack(null).commit();
        }

        // planCoursesFragment
        PlanCoursesFragment planCoursesFragment4 = (PlanCoursesFragment) getSupportFragmentManager().findFragmentByTag("PLAN_COURSES_FRAGMENT");
        PlanCoursesFragment planCoursesFragment3 = (PlanCoursesFragment) getSupportFragmentManager().findFragmentByTag("PLAN_COURSES_FRAGMENT2");

        if (planCoursesFragment3 != null && planCoursesFragment3.isVisible()) {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(
                    R.anim.fade_in,  // enter
                    R.anim.slide_out,  // exit
                    R.anim.slide_in,   // popEnter
                    R.anim.fade_out  // popExit
            )
                    .replace(planCoursesFragment3.getId(), planCoursesFragment, "PLAN_COURSES_FRAGMENT").addToBackStack(null).commit();
        }

        if (planCoursesFragment4 != null && planCoursesFragment4.isVisible()) {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(
                    R.anim.fade_in,  // enter
                    R.anim.slide_out,  // exit
                    R.anim.slide_in,   // popEnter
                    R.anim.fade_out  // popExit
            )
                    .replace(planCoursesFragment4.getId(), planCoursesFragment2, "PLAN_COURSES_FRAGMENT2").addToBackStack(null).commit();
        }

        // mainFragment
        MainScreenFragment mainScreenFragment3 = (MainScreenFragment) getSupportFragmentManager().findFragmentByTag("MAIN_FRAGMENT");

        if (mainScreenFragment3 != null && mainScreenFragment3.isVisible()) {
            Intent refresh = new Intent(this, MainScreenActivity.class);
            startActivity(refresh);
            finish();
        }

        // addCourseFragment
        AddCourseFragment addCourseFragment4 = (AddCourseFragment) getSupportFragmentManager().findFragmentByTag("ADD_COURSES_FRAGMENT");
        AddCourseFragment addCourseFragment3 = (AddCourseFragment) getSupportFragmentManager().findFragmentByTag("ADD_COURSES_FRAGMENT2");

        if (addCourseFragment3 != null && addCourseFragment3.isVisible()) {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(
                    R.anim.fade_in,  // enter
                    R.anim.slide_out,  // exit
                    R.anim.slide_in,   // popEnter
                    R.anim.fade_out  // popExit
            )
                    .replace(addCourseFragment3.getId(), addCourseFragment, "ADD_COURSES_FRAGMENT").addToBackStack(null).commit();
        }

        if (addCourseFragment4 != null && addCourseFragment4.isVisible()) {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(
                    R.anim.fade_in,  // enter
                    R.anim.slide_out,  // exit
                    R.anim.slide_in,   // popEnter
                    R.anim.fade_out  // popExit
            )
                    .replace(addCourseFragment4.getId(), addCourseFragment2, "ADD_COURSES_FRAGMENT2").addToBackStack(null).commit();
        }
    }


    private void changeSettingsFragment(SettingsFragment fragmentFrom, MainScreenFragment fragmentTo, String tag){
        if (fragmentFrom != null && fragmentFrom.isVisible()) {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(
                    R.anim.fade_in,  // enter
                    R.anim.slide_out,  // exit
                    R.anim.slide_in,   // popEnter
                    R.anim.fade_out  // popExits
            )
                    .replace(fragmentFrom.getId(), fragmentTo, tag).addToBackStack(null).commit();
        }
    }

    private void changeEditProfileFragment(EditProfileFragment fragmentFrom, MainScreenFragment fragmentTo, String tag){
        if (fragmentFrom != null && fragmentFrom.isVisible()) {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(
                    R.anim.fade_in,  // enter
                    R.anim.slide_out,  // exit
                    R.anim.slide_in,   // popEnter
                    R.anim.fade_out  // popExits
            )
                    .replace(fragmentFrom.getId(), fragmentTo, tag).addToBackStack(null).commit();
        }
    }

    private void changeMyCoursesFragment(MyCoursesFragment fragmentFrom, MainScreenFragment fragmentTo, String tag){
        if (fragmentFrom != null && fragmentFrom.isVisible()) {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(
                    R.anim.fade_in,  // enter
                    R.anim.slide_out,  // exit
                    R.anim.slide_in,   // popEnter
                    R.anim.fade_out  // popExits
            )
                    .replace(fragmentFrom.getId(), fragmentTo, tag).addToBackStack(null).commit();
        }
    }

    private void changePlanCoursesFragment(PlanCoursesFragment fragmentFrom, MainScreenFragment fragmentTo, String tag){
        if (fragmentFrom != null && fragmentFrom.isVisible()) {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(
                    R.anim.fade_in,  // enter
                    R.anim.slide_out,  // exit
                    R.anim.slide_in,   // popEnter
                    R.anim.fade_out  // popExits
            )
                    .replace(fragmentFrom.getId(), fragmentTo, tag).addToBackStack(null).commit();
        }
    }

    private void changeAddCoursesFragment(AddCourseFragment fragmentFrom, MyCoursesFragment fragmentTo, String tag){
        if (fragmentFrom != null && fragmentFrom.isVisible()) {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(
                    R.anim.fade_in,  // enter
                    R.anim.slide_out,  // exit
                    R.anim.slide_in,   // popEnter
                    R.anim.fade_out  // popExits
            )
                    .replace(fragmentFrom.getId(), fragmentTo, tag).addToBackStack(null).commit();
        }
    }
}
