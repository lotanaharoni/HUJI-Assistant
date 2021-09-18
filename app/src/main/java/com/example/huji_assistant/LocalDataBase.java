package com.example.huji_assistant;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Pair;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    private final FirebaseAuth mAuth;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public LocalDataBase(Context context){
        this.context = context;
        this.sp = context.getSharedPreferences("local_db_calculation_items", Context.MODE_PRIVATE);
        this.mAuth = FirebaseAuth.getInstance();
        // initializeSp();
    }

    public HashMap<String, String> getRuach_faculty_values_names_map() {
        return ruach_faculty_values_names_map;
    }

    public FirebaseAuth getUsersAuthenticator() {
        return mAuth;
    }

    public void logoutUser() {
        mAuth.signOut();
    }
}
