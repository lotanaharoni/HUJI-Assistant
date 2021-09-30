package com.example.huji_assistant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfilePageActivity extends AppCompatActivity {

    private TextView userFirstNameEditText;
    private TextView userLastNameEditText;
    private EditText emailEditText;
    private ImageView btnEditProfile;
    private ImageView btnCancelEdit;
    private boolean isEdit = false;
    private String emailBeforeEdit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        LocalDataBase dataBase = HujiAssistentApplication.getInstance().getDataBase();
        StudentInfo currentUser = dataBase.getCurrentUser();

        // find views
        userFirstNameEditText = findViewById(R.id.profile_user_first_name);
        userLastNameEditText = findViewById(R.id.profile_user_last_name);
        emailEditText = findViewById(R.id.usersEmailMyProfile);
        btnCancelEdit = findViewById(R.id.btnCancelEditProfile);
        btnEditProfile = findViewById(R.id.btnEditProfile);


        // initialize screen appearance
        setViewsByState(false);
        setViewsContentByUser(currentUser);

        // todo: last name
        btnEditProfile.setOnClickListener(v -> {
            if (isEdit) {
                btnCancelEdit.setVisibility(View.GONE);
//                dataBase.updateStudent(userFirstNameEditText.getText().toString(),
//                        emailEditText.getText().toString(), currentUser.getFacultyId(),
//                        currentUser.getChugId(), currentUser.getMaslulId(),
//                        currentUser.getDegree(), currentUser.getYear(), currentUser.getId());
            } else {
                emailBeforeEdit = emailEditText.getText().toString();
            }
            isEdit = !isEdit;
            setViewsByState(isEdit);
        });

        // cancel edit button callback
        btnCancelEdit.setOnClickListener(v -> cancelEditing());
    }

    /**
     * sets the views content as the given user fields
     */
    private void setViewsContentByUser(StudentInfo user) {
        userFirstNameEditText.setText(user.getName()); //todo: first name
        userLastNameEditText.setText(user.getName()); //todo: last name
        emailEditText.setText(user.getEmail());
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("is_edit", isEdit);
        outState.putString("user_first_name", userFirstNameEditText.getText().toString());
        outState.putString("user_last_name", userLastNameEditText.getText().toString());
        outState.putString("email", emailEditText.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isEdit = savedInstanceState.getBoolean("is_edit");
        userFirstNameEditText.setText(savedInstanceState.getString("user_first_name"));
        userLastNameEditText.setText(savedInstanceState.getString("user_last_name"));
        emailEditText.setText(savedInstanceState.getString("email"));
        setViewsByState(isEdit);
    }

    private void cancelEditing() {
        emailEditText.setText(emailBeforeEdit);
        if (isEdit) {
            btnEditProfile.callOnClick();
        }
    }

    /**
     * sets the views state according to the given editState
     */
    private void setViewsByState(boolean isEditState) {
        if (isEditState) {
            btnCancelEdit.setVisibility(View.VISIBLE);
        } else {
            btnCancelEdit.setVisibility(View.GONE);
        }
        emailEditText.setEnabled(isEditState);
        int edit_ic = isEditState ? R.drawable.ic_save_profile : R.drawable.ic_edit_profile;
        btnEditProfile.setImageResource(edit_ic);
    }

    @Override
    public void onBackPressed() {
        if (isEdit){
            cancelEditing();
        }
        else{
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

                android.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Close the app?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            } else {
                super.onBackPressed();
                finish();
            }
        }
    }
}