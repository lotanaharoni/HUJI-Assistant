package com.example.huji_assistant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentContainerView;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout moreInfoDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        moreInfoDrawerLayout = findViewById(R.id.drawer_layout_more_info);
        NavigationView navigationView = findViewById(R.id.nav_view);
        moreInfoDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        navigationView.setNavigationItemSelectedListener(this);

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

        findViewById(R.id.buttonMoreInfoMapActivity).setOnClickListener(v -> {
            moreInfoDrawerLayout.openDrawer(GravityCompat.START);
        });

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
                goToUrl("https://docs.google.com/spreadsheets/d/1ZeeEIkyVsfxV2R7kS_1Jcq06Tu1zu20Y3XT1q9cybtk/edit#gid=0");
                break;
        }
        return true;
    }
    @Override
    public void onBackPressed() {
        if (moreInfoDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            moreInfoDrawerLayout.closeDrawer(GravityCompat.START);
            return;
        }
        else {
            super.onBackPressed();
        }
    }
}