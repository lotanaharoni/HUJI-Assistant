package com.example.huji_assistant.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import androidx.annotation.NonNull;
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
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainScreenActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public LocalDataBase dataBase = null;
    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    private static final int CAMERA_TYPE = 1;
    private DatabaseReference root;
    private StorageReference reference;
    private ActivityResultLauncher<Intent> cameraUploadActivityResultLauncher;
    String currentPhotoPath;
    private DrawerLayout moreInfoDrawerLayout;
   // private DrawerLayout settingsDrawerLayout;
    private ImageView logoutImageView;
    ListenerRegistration listener;
    FirebaseFirestore firebaseInstancedb = FirebaseFirestore.getInstance();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainscreen2);

        // db
        if (dataBase == null) {
            dataBase = HujiAssistentApplication.getInstance().getDataBase();
        }

        // application singleton
        HujiAssistentApplication application = (HujiAssistentApplication) getApplication();

        System.out.println("current student: " + dataBase.getCurrentUser().toStringP());

        // firebase listener for changes in current student
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
            }
        });
        // todo check if is updated in fragments

        logoutImageView = findViewById(R.id.logoutImageView);
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

                            uploadToFirebase(contentUri, CAMERA_REQUEST_CODE, f.getName());
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

        myCoursesFragment.addCourseListener = new MyCoursesFragment.addCourseButtonClickListener() {
            @Override
            public void addCourseBtnClicked() {
                getSupportFragmentManager().beginTransaction().setCustomAnimations(
                        R.anim.fade_in,  // enter
                        R.anim.slide_out,  // exit
                        R.anim.slide_in,   // popEnter
                        R.anim.fade_out  // popExit
                )
                        // .replace(mainFragmentView.getId(), coursesFragment, "COURSES_FRAGMENT").addToBackStack(null).commit();
                        .replace(mainFragmentView.getId(), addCourseFragment, "ADD_COURSES_FRAGMENT").addToBackStack(null).commit();
            }
        };

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
            dispatchTakePictureIntent();
        }
    }

    private void dispatchTakePictureIntent() {
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

    private void uploadToFirebase(Uri uri, int source, String name) {
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
                        Toast.makeText(MainScreenActivity.this, R.string.upload_Successfully_message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainScreenActivity.this, R.string.upload_failed_message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private File createImageFile() throws IOException {
//     Create an image file name
        String imageFileName = "";
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageFileName = "JPEG_" + timeStamp + "_";
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