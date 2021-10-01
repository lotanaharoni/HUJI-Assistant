package com.example.huji_assistant.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.huji_assistant.HujiAssistentApplication;
import com.example.huji_assistant.LocalDataBase;
import com.example.huji_assistant.R;
import com.example.huji_assistant.StudentInfo;

public class ProfilePageFragment extends Fragment {

    private TextView userFirstNameEditText;
    private TextView userLastNameEditText;
    private EditText emailEditText;
    private ImageView btnEditProfile;
    private ImageView btnCancelEdit;
    private boolean isEdit = false;
    private String emailBeforeEdit;

    public ProfilePageFragment(){
        super(R.layout.activity_profile_page);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LocalDataBase dataBase = HujiAssistentApplication.getInstance().getDataBase();
        StudentInfo currentUser = dataBase.getCurrentUser();

        // find views
        userFirstNameEditText = view.findViewById(R.id.profile_user_first_name);
        userLastNameEditText = view.findViewById(R.id.profile_user_last_name);
        emailEditText = view.findViewById(R.id.usersEmailMyProfile);
        btnCancelEdit = view.findViewById(R.id.btnCancelEditProfile);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);

        // initialize screen appearance
        setViewsByState(false);
        setViewsContentByUser(currentUser);

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
        userFirstNameEditText.setText(user.getPersonalName()); //todo: first name
        userLastNameEditText.setText(user.getFamilyName()); //todo: last name
        emailEditText.setText(user.getEmail());
    }

    /**
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
    }*/

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
/**
    @Override
    public void onBackPressed() {
        if (isEdit) {
            cancelEditing();
        }
    }*/




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
