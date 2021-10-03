package com.example.huji_assistant.Activities;

import android.Manifest;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

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

import com.example.huji_assistant.Course;
import com.example.huji_assistant.CoursesAdapter;
import com.example.huji_assistant.Fragments.AddCourseFragment;
import com.example.huji_assistant.Fragments.CourseInfoFragment;
import com.example.huji_assistant.Fragments.CoursesFragment;
import com.example.huji_assistant.Fragments.MainScreenFragment;
import com.example.huji_assistant.Fragments.MyCoursesFragment;
import com.example.huji_assistant.Fragments.ProfilePageFragment;
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
    private ProgressBar progressBar;
    FirebaseFirestoreSettings settings;
    private ActivityResultLauncher<Intent> cameraUploadActivityResultLauncher;
    String currentPhotoPath;
    private DrawerLayout moreInfoDrawerLayout;
   // private DrawerLayout settingsDrawerLayout;
    private ImageView logoutImageView;
   // ArrayList<Course> coursesOfStudentByCourse = new ArrayList<>();
    ListenerRegistration listener;
    FirebaseFirestore firebaseInstancedb = FirebaseFirestore.getInstance();

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
                    System.out.println("updated student: " + currentStudent.toStringP());
                    System.out.println("updated courses list: ");
                    currentStudent.printCourses();
                    getNewCoursesList();
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
        progressBar = findViewById(R.id.progressBar);
      //  progressBar.setVisibility(View.INVISIBLE);
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
        MyCoursesFragment myCoursesFragment = new MyCoursesFragment();
        CourseInfoFragment courseInfoFragment = new CourseInfoFragment();
        AddCourseFragment addCourseFragment = new AddCourseFragment();
        ProfilePageFragment profilePageFragment = new ProfilePageFragment();
        FloatingActionButton openCameraBtn = findViewById(R.id.open_camera_floating_button);
        root = FirebaseDatabase.getInstance().getReference("Image");
        reference = FirebaseStorage.getInstance().getReference();

        MainScreenFragment mainscreenfragment = new MainScreenFragment();

        getSupportFragmentManager().beginTransaction().replace(mainFragmentView.getId(), mainscreenfragment, "MAIN_FRAGMENT")
                .commit();

        findViewById(R.id.buttonMoreInfoMapActivity).setOnClickListener(v -> {
            moreInfoDrawerLayout.openDrawer(GravityCompat.START);
        });

        findViewById(R.id.settings).setOnClickListener(v -> {
            // todo handle
            System.out.println("settings clicked");
        });

        // todo for lotan
        findViewById(R.id.qrscan).setOnClickListener(v -> {
            // todo handle
            System.out.println("qrscan clicked");
        });

        // todo add code - lotan
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
                        .replace(mainFragmentView.getId(), profilePageFragment, "EDIT_INFO_FRAGMENT").addToBackStack(null).commit();
            }
        };

        mainscreenfragment.uploadPicturesButtonListenerBtn = new MainScreenFragment.UploadPictureButtonListener(){

            @Override
            public void onUploadPictureButtonClicked() {
                startActivity(new Intent(MainScreenActivity.this, CaptureImageActivity.class));
                finish();
//                getSupportFragmentManager().beginTransaction().setCustomAnimations(
//                        R.anim.fade_in,  // enter
//                        R.anim.slide_out,  // exit
//                        R.anim.slide_in,   // popEnter
//                        R.anim.fade_out  // popExit
//                )
//                        // .replace(mainFragmentView.getId(), coursesFragment, "COURSES_FRAGMENT").addToBackStack(null).commit();
//                        .replace(mainFragmentView.getId(), profilePageFragment, "EDIT_INFO_FRAGMENT").addToBackStack(null).commit();
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

    }

    private void goToUrl(String s) {
        Uri url = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW, url));
    }



    private void getNewCoursesList(){

        ArrayList<String> coursesOfStudentById = dataBase.getCurrentStudent().getCourses();
        ArrayList<Course> coursesOfStudentByCourse = new ArrayList<>();

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

                        dataBase.setCoursesFromFireBase(coursesFromFireBase);

                        for (String id : coursesOfStudentById){
                            for (Course course: coursesFromFireBase){
                                if (course.getNumber().equals(id)){
                                    course.setIsFinished(true);
                                    coursesOfStudentByCourse.add(course);
                                }
                            }
                        }
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
            } else {
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
}
