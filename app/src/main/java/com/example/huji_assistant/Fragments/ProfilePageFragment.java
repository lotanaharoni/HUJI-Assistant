package com.example.huji_assistant.Fragments;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.huji_assistant.HujiAssistentApplication;
import com.example.huji_assistant.LocalDataBase;
import com.example.huji_assistant.R;
import com.example.huji_assistant.StudentInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfilePageFragment extends Fragment {

    private TextView helloMessage;
//    private TextView userFirstNameEditText;
//    private TextView userLastNameEditText;
    private EditText emailEditText, userNewPassword, userRepeatPassword, oldPassword;
    private ImageView btnEditProfile;
    private ImageView btnCancelEdit;
    private boolean isEdit = false;
    int PASSWORD_LENGTH = 8;

    public ProfilePageFragment(){
        super(R.layout.activity_profile_page);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LocalDataBase dataBase = HujiAssistentApplication.getInstance().getDataBase();
        StudentInfo currentUser = dataBase.getCurrentUser();

        // find views
        helloMessage = view.findViewById(R.id.helloMessage);
//        userFirstNameEditText = view.findViewById(R.id.profile_user_first_name);
//        userLastNameEditText = view.findViewById(R.id.profile_user_last_name);
        emailEditText = view.findViewById(R.id.usersEmailMyProfile);
        btnCancelEdit = view.findViewById(R.id.btnCancelEditProfile);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        userNewPassword = view.findViewById(R.id.userNewPassword1MyProfile);
        userRepeatPassword = view.findViewById(R.id.userNewPassword2MyProfile);
        oldPassword = view.findViewById(R.id.userOldPasswordMyProfile);
        emailEditText.setEnabled(false);

        // initialize screen appearance
        setViewsByState(false);
        setViewsContentByUser(currentUser);

        btnEditProfile.setOnClickListener(v -> {
            if (isEdit && checkValidation()) {
                btnCancelEdit.setVisibility(View.GONE);
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                AuthCredential credential = EmailAuthProvider
                        .getCredential(emailEditText.getText().toString(), oldPassword.getText().toString());

                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    user.updatePassword(userNewPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getActivity(), R.string.password_updated_Successfully_message, Toast.LENGTH_LONG).show();
                                                userNewPassword.setText("");
                                                userRepeatPassword.setText("");
                                                oldPassword.setText("");
                                            } else {
                                                Log.d(TAG, "Error password not updated");
                                                Toast.makeText(getActivity(), R.string.password_failed_to_update_message, Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                } else {
                                    Log.d(TAG, "Error auth failed");
                                    Toast.makeText(getActivity(), R.string.incorrect_old_password, Toast.LENGTH_LONG).show();
                                }
                            }
                        });
//                dataBase.updateStudent(userFirstNameEditText.getText().toString(),
//                        emailEditText.getText().toString(), currentUser.getFacultyId(),
//                        currentUser.getChugId(), currentUser.getMaslulId(),
//                        currentUser.getDegree(), currentUser.getYear(), currentUser.getId());
            } else {
//                emailBeforeEdit = emailEditText.getText().toString();
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
//        userFirstNameEditText.setText(user.getPersonalName()); //todo: first name
//        userLastNameEditText.setText(user.getFamilyName()); //todo: last name
        String message = "Hello" + " " + user.getPersonalName() + " " + user.getFamilyName();
        helloMessage.setText(message);
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
        //        emailEditText.setText(emailBeforeEdit);
        userNewPassword.setText("");
        userRepeatPassword.setText("");
        oldPassword.setText("");
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
        userNewPassword.setEnabled(isEditState);
        userRepeatPassword.setEnabled(isEditState);
        oldPassword.setEnabled(isEditState);
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

    private boolean checkValidation(){
        String oldPass = oldPassword.getText().toString();
        String newPass = userNewPassword.getText().toString();
        String repeatPass = userRepeatPassword.getText().toString();
        if (oldPass.isEmpty()){
            Toast.makeText(getActivity(), R.string.old_password_is_empty, Toast.LENGTH_LONG).show();
            return false;
        }
        if (newPass.isEmpty()){
            Toast.makeText(getActivity(), R.string.new_password_is_empty, Toast.LENGTH_LONG).show();
            return false;
        }
        if (repeatPass.isEmpty()){
            Toast.makeText(getActivity(), R.string.repeat_password_is_empty, Toast.LENGTH_LONG).show();
            return false;
        }
        if (!newPass.equals(repeatPass)){
            Toast.makeText(getActivity(), R.string.new_pass_is_not_equal_to_repeat_pass, Toast.LENGTH_LONG).show();
            return false;
        }
        if (newPass.length() < PASSWORD_LENGTH){
            Toast.makeText(getActivity(), R.string.please_enter_password_msg, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
