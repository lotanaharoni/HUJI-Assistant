package com.example.huji_assistant;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LocalDataBase {

    private final ArrayList<StudentInfo> listOfStudents = new ArrayList<>();
    private final Context context;
    private final SharedPreferences sp;
    private final MutableLiveData<List<StudentInfo>> mutableLiveData = new MutableLiveData<>();
    public final LiveData<List<StudentInfo>> publicLiveData = mutableLiveData;
    private ArrayList<Course> listOfCourses = new ArrayList<>();
    private HashMap<String, String> faculty_values_names_map;
    public HashMap<String, String> ruach_faculty_values_names_map;
    private String[] faculty_values;
    public HashMap<String, Chug> chugs = new HashMap<>();
    private FirebaseUser currentFbUser;
    private StudentInfo currentStudent;
    private final HashMap<String, StudentInfo> students;
    private final DatabaseReference usersRef;
    private final MutableLiveData<StudentInfo> currentUserMutableLiveData = new MutableLiveData<>();
    public final LiveData<StudentInfo> currentUserLiveData = currentUserMutableLiveData;
    private final MutableLiveData<Boolean> firstLoadFlagMutableLiveData = new MutableLiveData<>();
    public final LiveData<Boolean> firstLoadFlagLiveData = firstLoadFlagMutableLiveData;
    private boolean firstUsersLoad = false;

    private final FirebaseAuth mAuth;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public LocalDataBase(Context context){
        this.context = context;
        this.sp = context.getSharedPreferences("local_db_calculation_items", Context.MODE_PRIVATE);
        this.mAuth = FirebaseAuth.getInstance();
        this.students = new HashMap<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        this.usersRef = database.getReference("Users");
        this.currentStudent = new StudentInfo();
        this.refreshDataUsers();

        firstLoadFlagMutableLiveData.postValue(false);
        // initializeSp();
    }

    public HashMap<String, String> getRuach_faculty_values_names_map() {
        return ruach_faculty_values_names_map;
    }

    public FirebaseAuth getUsersAuthenticator() {
        return mAuth;
    }

    public StudentInfo getCurrentUser() {
        return this.currentStudent;
    }

    public void saveUserToSp(String id, String email, String password) {
        SharedPreferences.Editor spEditor = sp.edit();
//        spEditor.putString("id", id);
        spEditor.apply();
    }

    public void refreshDataUsers() {
        readDataIdsInUse((students) -> {
        });
    }

    public void logoutUser() {
        mAuth.signOut();
        this.currentFbUser = null;
        this.currentStudent = new StudentInfo();
        SharedPreferences.Editor spEditor = sp.edit();
//        spEditor.remove(user_email);
        spEditor.apply();
    }

    public boolean emailUserExists(String email) {
        for (StudentInfo student : students.values()) {
            if (student.getEmail().equals(email)) return true;
        }
        return false;
    }

    public void addStudent(String studentId, String email, String password) {
        StudentInfo newStudent = new StudentInfo(studentId, email, password);
        this.usersRef.child(newStudent.getId()).setValue(newStudent);
    }

    public void updateStudent(String name, String email, String password, String facultyId, String chugId, String maslulId, String degree, String year, String id) {
        StudentInfo newUser = new StudentInfo(name, email, password, facultyId, chugId, maslulId, degree, year, id);
        this.usersRef.child(id).setValue(newUser).addOnSuccessListener(aVoid -> {
            if (newUser.getId().equals(currentStudent.getId())) {
                currentStudent = newUser;
            } else {
                Log.d("sameUserCheck", "not the current user");
            }
        });
    }

    public void setCurrentUser(FirebaseUser user) {
        if (user != null) {
            this.currentFbUser = user;
            if (students.containsKey(user.getUid())) {
                this.currentStudent = students.get(user.getUid());
                currentUserMutableLiveData.setValue(this.currentStudent);
            }
        }
    }

    private void readDataIdsInUse(FirebaseUsersUpdateCallback firebaseCallback) {
        ValueEventListener valueEventListenerUsers = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                students.clear();
                ArrayList<StudentInfo> unapproved = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds != null) {
                        StudentInfo user = ds.getValue(StudentInfo.class);
                        assert user != null;
                        students.put(user.getId(), user);
                    }
                }
                setCurrentUser(currentFbUser);
                firebaseCallback.onCallback(students);
                firstLoadFlagMutableLiveData.postValue(firstUsersLoad);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        // update data and relevant liveData
        usersRef.addValueEventListener(valueEventListenerUsers);

        // notifies first load
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                firstUsersLoad = true;
                firstLoadFlagMutableLiveData.postValue(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public StudentInfo getStudent(String studentId) {
        if (students.containsKey(studentId)) {
            return students.get(studentId);
        }
        return null;
    }
}
