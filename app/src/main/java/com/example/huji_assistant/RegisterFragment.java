package com.example.huji_assistant;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterFragment extends Fragment {
    private LocalDataBase db;
    private ViewModelApp viewModelApp;
    public interface buttonClickListener{
        public void onButtonClicked();
    }
    public RegisterFragment(){
        super(R.layout.register_fragment);
    }
    public TextViewFragment.buttonClickListener listener = null;
    boolean isEmailValid = false;
    boolean isPasswordValid = false;
    TextView emailValidationView;
    TextView passwordValidationView;
    int PASSWORD_LENGTH = 8;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.db = HujiAssistentApplication.getInstance().getDataBase();
        viewModelApp = new ViewModelProvider(requireActivity()).get(ViewModelApp.class);
        if (view != null) {

        }
        EditText email = view.findViewById(R.id.email);
        EditText password = view.findViewById(R.id.password);

        emailValidationView = view.findViewById(R.id.emailValidation);
        passwordValidationView = view.findViewById(R.id.passwordValidation);

        // Set view model - singleton
        ViewModelApp vm = new ViewModelProvider(requireActivity()).get(ViewModelApp.class);

        // Gets current data from live data to show at the screen
        vm.studentInfoMutableLiveData.observe(getViewLifecycleOwner(), new Observer<StudentInfo>() {
            @Override
            public void onChanged(StudentInfo studentInfo) {
                // when observe occurs
            }
        });

        Button continueBtn = view.findViewById(R.id.continueBtn);
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailValidationView.setVisibility(View.GONE);
                passwordValidationView.setVisibility(View.GONE);
//                FirstFragment myFragment = (FirstFragment)getSupportFragmentManager().findFragmentByTag("FIRST_FRAGMENT");
//                if (myFragment != null && myFragment.isVisible()) {
//                }

                checkValidation(email.getText().toString(), password.getText().toString());
                if (isEmailValid && isPasswordValid){
                    if (listener != null && !db.emailUserExists(email.getText().toString())) {
                        FirebaseAuth auth = db.getUsersAuthenticator();
                        Toast.makeText(getActivity(), "register fragment", Toast.LENGTH_LONG).show();                                            //todo: don't allow to continue
                        auth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("RegisterActivity", "registerWithEmail:success");
                                            Toast.makeText(getActivity(), "registerWithEmail:success", Toast.LENGTH_LONG).show();                                            //todo: don't allow to continue
                                            FirebaseUser user = auth.getCurrentUser();
                                            db.addStudent(user.getUid(), email.getText().toString(), password.getText().toString());
                                            db.setCurrentUser(user);
                                            StudentInfo newStudent = new StudentInfo(email.getText().toString(), password.getText().toString());
                                            viewModelApp.setStudent(newStudent);
                                            listener.onButtonClicked();
                                        }else{
                                            Log.w("RegisterActivity", "registerWithEmail:failure", task.getException());
                                            Toast.makeText(getActivity(), "registerWithEmail:failure", Toast.LENGTH_LONG).show();                                            //todo: don't allow to continue
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });
                    }else{
                        Toast.makeText(getActivity(), "user is already register", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void setText(String text){
        View view = getView();
        if (view != null){
            TextView textView = view.findViewById(R.id.screennametextview);
            textView.setText(text);
        }
    }

    public void checkValidation(String email, String password){
        if (email.isEmpty()) {
//            emailValidationView.setText(getResources().getString(R.string.please_enter_email_msg));
//            emailValidationView.setVisibility(View.VISIBLE);
            //todo: maybe a Toast?
            Toast.makeText(getActivity(), getResources().getString(R.string.please_enter_email_msg), Toast.LENGTH_LONG).show();

            isEmailValid = false;
        }
        else if (email.length() > 254 ||
                !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(getActivity(), getResources().getString(R.string.email_not_valid_message), Toast.LENGTH_LONG).show();
            isEmailValid = false;
        }
        //  } else if (!Patterns.EMAIL_ADDRESS.matcher(email.matches("*"))) {
        //  email.setError(getResources().getString(R.string.error_invalid_email));
        //    isEmailValid = false;}
        else  {
            isEmailValid = true;
        }

        // Check for a valid password.
        if (password.isEmpty()) {
//            passwordValidationView.setText(getResources().getString(R.string.please_enter_password_msg));
//            passwordValidationView.setVisibility(View.VISIBLE);
            //todo: maybe a Toast?
            Toast.makeText(getActivity(), getResources().getString(R.string.please_enter_password_msg), Toast.LENGTH_LONG).show();
            isPasswordValid = false;
        } else if (password.length() < PASSWORD_LENGTH) {
//            passwordValidationView.setText(getResources().getString(R.string.please_enter_password_msg));
//            passwordValidationView.setVisibility(View.VISIBLE);
            //todo: maybe a Toast?
            Toast.makeText(getActivity(), getResources().getString(R.string.please_enter_password_msg), Toast.LENGTH_LONG).show();
            isPasswordValid = false;
        } else  {
            isPasswordValid = true;
        }

        if (isEmailValid && isPasswordValid) {
            // the values are valid
        }
    }
}
