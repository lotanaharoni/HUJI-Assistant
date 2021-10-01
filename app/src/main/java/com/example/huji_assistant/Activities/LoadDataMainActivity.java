package com.example.huji_assistant.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.huji_assistant.HujiAssistentApplication;
import com.example.huji_assistant.LocalDataBase;
import com.example.huji_assistant.R;
import com.google.firebase.auth.FirebaseUser;

public class LoadDataMainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wait_screen);

        LocalDataBase db = HujiAssistentApplication.getInstance().getDataBase();

        FirebaseUser currentUser = db.getUsersAuthenticator().getCurrentUser();

//        FireStoreReader fireStoreReader = new FireStoreReader();

        // update UI when DB finish to load the initial data
        db.firstLoadFlagLiveData.observe(this, isLoad -> {
            if (isLoad) {
                updateUI(currentUser);
            }
        });
    }

    /**
     * if there is a logged in user -> navigate to the map screen, else navigate to the login screen
     *
     * @param firebaseUser - the currently logged in user, null if no user os logged in
     */
    private void updateUI(FirebaseUser firebaseUser) {

        if (firebaseUser != null) {
            LocalDataBase db = HujiAssistentApplication.getInstance().getDataBase();
            db.setCurrentUser(firebaseUser);
            db.currentUserLiveData.observe(this, user -> {
                if (user != null) {
                    startActivity(new Intent(this, MainScreenActivity.class));
                    finish();
                }
            });
        } else {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }
}
