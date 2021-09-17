package com.example.huji_assistant;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.fragment.app.FragmentContainerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    public LocalDataBase dataBase = null;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //FirebaseFirestore.getInstance()

        // db
        if (dataBase == null){
            dataBase = HujiAssistentApplication.getInstance().getDataBase();
        }
       // Fetcher fe = new Fetcher();
       // fe.execute();

       // HashMap<String, Chug> chugsMap = fe.getChugsMap();
      //  dataBase.chugs = chugsMap;



        // application singleton
        HujiAssistentApplication application = (HujiAssistentApplication) getApplication();

        // Gets fragments
        FragmentContainerView loginFragment = findViewById(R.id.loginfragment);
        FragmentContainerView topF = findViewById(R.id.topFragment);
        TopFragment f = new TopFragment();
        FirstFragment firstFragment = new FirstFragment();
        TextViewFragment secondFragment = new TextViewFragment();
        CoursesFragment coursesFragment = new CoursesFragment();
        InfoFragment infoFragment = new InfoFragment();
        MainScreen mainScreen = new MainScreen();
        getSupportFragmentManager().beginTransaction().replace(loginFragment.getId(), firstFragment)
                .commit();

        firstFragment.newUserBtnListener = new FirstFragment.newUserButtonClickListener() {
            @Override
            public void onNewUserBtnClicked() {

                getSupportFragmentManager().beginTransaction().setCustomAnimations(
                        R.anim.fade_in,  // enter
                        R.anim.slide_out,  // exit
                        R.anim.slide_in,   // popEnter
                        R.anim.fade_out  // popExit
                )
                        .replace(loginFragment.getId(), secondFragment).addToBackStack(null).commit();
            }
        };

        firstFragment.existingUserBtnListener = new FirstFragment.existingUserButtonClickListener() {
            @Override
            public void onExistingUserBtnClicked() {
                getSupportFragmentManager().beginTransaction().setCustomAnimations(
                        R.anim.fade_in,  // enter
                        R.anim.slide_out,  // exit
                        R.anim.slide_in,   // popEnter
                        R.anim.fade_out  // popExit
                ).replace(loginFragment.getId(), secondFragment).addToBackStack(null).commit();
            }
        };

        secondFragment.listener = new TextViewFragment.buttonClickListener() {
            @Override
            public void onButtonClicked() {
                getSupportFragmentManager().beginTransaction().setCustomAnimations(
                        R.anim.fade_in,  // enter
                        R.anim.slide_out,  // exit
                        R.anim.slide_in,   // popEnter
                        R.anim.fade_out  // popExit
                )
                        .replace(loginFragment.getId(), infoFragment).addToBackStack(null).commit();
            }
        };

        infoFragment.continueListener = new InfoFragment.continueButtonClickListener() {
            @Override
            public void continueBtnClicked() {
                getSupportFragmentManager().beginTransaction().setCustomAnimations(
                        R.anim.fade_in,  // enter
                        R.anim.slide_out,  // exit
                        R.anim.slide_in,   // popEnter
                        R.anim.fade_out  // popExit
                )
                        .replace(loginFragment.getId(), coursesFragment).addToBackStack(null).commit();
            }
        };
/** todo remove this listener
        infoFragment.itemSelectedDropDownFacultyListener = new InfoFragment.itemSelectedDropDownFaculty() {
            @Override
            public void onFacultyItemSelected() {
                View autoCompleteTextViewFacultyView = findViewById(R.id.autoCompleteTextViewFaculty);
            //    autoCompleteTextViewFacultyView.

            }
        };
*/
        coursesFragment.endRegistrationBtnListener = new CoursesFragment.endRegistrationButtonClickListener() {
            @Override
            public void onEndRegistrationBtnClicked() {
                // todo get all data from registration fragments and save the new user
                getSupportFragmentManager().beginTransaction().setCustomAnimations(
                        R.anim.fade_in,  // enter
                        R.anim.slide_out,  // exit
                        R.anim.slide_in,   // popEnter
                        R.anim.fade_out  // popExit
                )
                        .replace(loginFragment.getId(), mainScreen).disallowAddToBackStack().commit();
            }
        };
 }

}