package com.example.huji_assistant;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.Observer;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

public class MainScreenActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public LocalDataBase dataBase = null;
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
}
