package com.example.huji_assistant;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import androidx.annotation.Nullable;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
//    private final DatabaseReference usersRef;
    private final MutableLiveData<StudentInfo> currentUserMutableLiveData = new MutableLiveData<>();
    public final LiveData<StudentInfo> currentUserLiveData = currentUserMutableLiveData;
    private final MutableLiveData<Boolean> firstLoadFlagMutableLiveData = new MutableLiveData<>();
    public final LiveData<Boolean> firstLoadFlagLiveData = firstLoadFlagMutableLiveData;
    private boolean firstUsersLoad = false;
    FirebaseFirestore db;
    CollectionReference studentsCollection;

    // Courses db
    private final MutableLiveData<List<Course>> mutableLiveDataMyCourses = new MutableLiveData<>();
    public final LiveData<List<Course>> publicLiveDataMyCourses = mutableLiveDataMyCourses;


    private final FirebaseAuth mAuth;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public LocalDataBase(Context context){
        this.context = context;
        this.sp = context.getSharedPreferences("local_db_calculation_items", Context.MODE_PRIVATE);
        this.mAuth = FirebaseAuth.getInstance();
        this.students = new HashMap<>();
        this.db = FirebaseFirestore.getInstance();
        this.studentsCollection = db.collection("students");
        this.currentStudent = new StudentInfo();
        this.readDataIdsInUse4();
        firstLoadFlagMutableLiveData.postValue(false);


        mutableLiveDataMyCourses.setValue(new ArrayList<>(listOfCourses));
        sortCourseItems();
//        this.refreshDataUsers();
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        this.usersRef = database.getReference("Users");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<Course> getMyCoursesList(){
        sortCourseItems();
        return new ArrayList<>(listOfCourses);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void sendBroadcastDbChanged(){
        Intent broadcast = new Intent("changed_db");
        broadcast.putExtra("new_list", getMyCoursesList());
        context.sendBroadcast(broadcast);
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

    public void addStudent(String studentId, String email){
        StudentInfo newStudent = new StudentInfo(studentId, email);
        Map<String, StudentInfo> newUser = new HashMap<>();
        newUser.put(newStudent.getId(), newStudent);
        this.studentsCollection.document(newStudent.getId()).set(newStudent);
    }
    // todo fix
    public void updateStudent(String name, String email, String facultyId, String chugId, String maslulId, String degree, String year, String id) {
       // StudentInfo newUser = new StudentInfo(name, email, facultyId, chugId, maslulId, degree, year, id);
        // todo fix
        StudentInfo newUser = new StudentInfo(name,name, email, facultyId, chugId, maslulId, degree, year, id);
        Map<String, StudentInfo> updatedUser = new HashMap<>();
        updatedUser.put(newUser.getId(), newUser);
        this.studentsCollection.document(newUser.getId()).set(updatedUser).addOnSuccessListener(aVoid -> {
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

    public void readDataIdsInUse4(){
        db.collection("students")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        students.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            StudentInfo user = doc.toObject(StudentInfo.class);
                            students.put(user.getId(), user);
                        }
                        setCurrentUser(currentFbUser);
                        firstLoadFlagMutableLiveData.postValue(true);
                        firstUsersLoad = true;
//                        firstLoadFlagMutableLiveData.postValue(true);
                    }
                });
    }

    public StudentInfo getStudent(String studentId) {
        if (students.containsKey(studentId)) {
            return students.get(studentId);
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void sortCourseItems(){
        this.listOfCourses.sort(new Comparator<Course>() {
            @Override
            public int compare(Course o1, Course o2) {
                if (o1.getType().equals(Course.Type.Mandatory.toString()) && o2.getType().equals(Course.Type.Mandatory.toString())) {
                    String num1 = o1.getPoints();
                    String num2 = o2.getPoints();
                    return num2.compareTo(num1);
                }
                else if (o1.getType().equals(Course.Type.MandatoryChoose.toString()) && o2.getType().equals(Course.Type.Mandatory.toString())){
                    String num1 = o1.getPoints();
                    String num2 = o2.getPoints();
                    return num1.compareTo(num2);
                } // todo add conditions
                return 0;
            }
        });
    }

    //    private void readDataIdsInUse(FirebaseUsersUpdateCallback firebaseCallback) {
//        ValueEventListener valueEventListenerUsers = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                students.clear();
//                ArrayList<StudentInfo> unapproved = new ArrayList<>();
//                for (DataSnapshot ds : snapshot.getChildren()) {
//                    if (ds != null) {
//                        StudentInfo user = ds.getValue(StudentInfo.class);
//                        assert user != null;
//                        students.put(user.getId(), user);
//                    }
//                }
//                setCurrentUser(currentFbUser);
//                firebaseCallback.onCallback(students);
//                firstLoadFlagMutableLiveData.postValue(firstUsersLoad);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//            }
//        };
//        // update data and relevant liveData
//        usersRef.addValueEventListener(valueEventListenerUsers);
//
//        // notifies first load
//        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                firstUsersLoad = true;
//                firstLoadFlagMutableLiveData.postValue(true);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//            }
//        });
//    }

    //    public void addStudentFirebase(String studentId, String email, String password) {
//        StudentInfo newStudent = new StudentInfo(studentId, email, password);
//        this.usersRef.child(newStudent.getId()).setValue(newStudent);
//    }

    //    public void updateStudentFirebase(String name, String email, String password, String facultyId, String chugId, String maslulId, String degree, String year, String id) {
//        StudentInfo newUser = new StudentInfo(name, email, password, facultyId, chugId, maslulId, degree, year, id);
//        this.usersRef.child(id).setValue(newUser).addOnSuccessListener(aVoid -> {
//            if (newUser.getId().equals(currentStudent.getId())) {
//                currentStudent = newUser;
//            } else {
//                Log.d("sameUserCheck", "not the current user");
//            }
//        });
//    }

    //    public void refreshDataUsers() {
//        readDataIdsInUse((students) -> {
//        });
//    }
}
