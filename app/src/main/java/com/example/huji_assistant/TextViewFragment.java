package com.example.huji_assistant;

import android.os.Bundle;
import android.util.Log;
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

public class TextViewFragment extends Fragment {
    private LocalDataBase db;
    private ViewModelApp viewModelApp;
    public interface buttonClickListener{
        public void onButtonClicked();
    }
    public TextViewFragment(){
        super(R.layout.loginfragment);
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

                checkValidation(email.getText().toString(), password.getText().toString());
                if (isEmailValid && isPasswordValid){
                    if (listener != null) {
                        FirebaseAuth auth = db.getUsersAuthenticator();
                        auth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("LoginActivity", "signInWithEmail:success");
                                            Toast.makeText(getActivity(), "signInWithEmail:success", Toast.LENGTH_LONG).show();                                            //todo: don't allow to continue
//                                            FirebaseUser user = auth.getCurrentUser();
//                                            db.setCurrentUser(user);                                        } else {
                                            StudentInfo newStudent = new StudentInfo(email.getText().toString(), password.getText().toString());
                                            viewModelApp.set(newStudent);
                                            listener.onButtonClicked();
                                        }else{
                                            Log.w("LoginActivity", "signInWithEmail:failure", task.getException());
                                            Toast.makeText(getActivity(), "signInWithEmail:failure", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });
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
            emailValidationView.setText(getResources().getString(R.string.please_enter_email_msg));
            emailValidationView.setVisibility(View.VISIBLE);

            isEmailValid = false;}
        //  } else if (!Patterns.EMAIL_ADDRESS.matcher(email.matches("*"))) {
        //  email.setError(getResources().getString(R.string.error_invalid_email));
        //    isEmailValid = false;}
        else  {
            isEmailValid = true;
        }

        // Check for a valid password.
        if (password.isEmpty()) {
            passwordValidationView.setText(getResources().getString(R.string.please_enter_password_msg));
            passwordValidationView.setVisibility(View.VISIBLE);
            isPasswordValid = false;
        } else if (password.length() < PASSWORD_LENGTH) {
            isPasswordValid = false;
        } else  {
            isPasswordValid = true;
        }

        if (isEmailValid && isPasswordValid) {
            // the values are valid
        }
    }
}