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
import com.google.firebase.firestore.FirebaseFirestoreSettings;
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
    private ArrayList<Course> coursesFromFireBase = new ArrayList<>();
    private FirebaseUser currentFbUser;
    private StudentInfo currentStudent;
    private Faculty currentFaculty;
    private Chug currentChug;
    private Maslul currentMaslul;
    private int currentPointsSum = 0;
    private int currentMandatoryChoosePoints = 0;
    private int currentMandatoryPoints = 0;
    private int currentCornerStonePoints = 0;
    private int currentChoosePoints = 0;
    private int currentSuppPoints = 0;
    private ArrayList<Course> coursesRegistration;
    private final HashMap<String, StudentInfo> students;
//    private final DatabaseReference usersRef;
    private final MutableLiveData<StudentInfo> currentUserMutableLiveData = new MutableLiveData<>();
    public final LiveData<StudentInfo> currentUserLiveData = currentUserMutableLiveData;
    private final MutableLiveData<Boolean> firstLoadFlagMutableLiveData = new MutableLiveData<>();
    public final LiveData<Boolean> firstLoadFlagLiveData = firstLoadFlagMutableLiveData;
    private boolean firstUsersLoad = false;
    FirebaseFirestoreSettings settings;
    private ArrayList<Course> coursesOfCurrentStudent = new ArrayList<>();
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
        settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);


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

    public void addCourseId(String courseId){
        if (courseId != null){
            ArrayList<String> courses = this.currentStudent.getCourses();
            courses.add(courseId);
            this.currentStudent.setCourses(courses);

           // Course toAdd = null;

            // Adds the course to the list of courses
            for (Course course : this.coursesFromFireBase){
                if (course.getNumber().equals(courseId)){
                   //toAdd = course;
                    this.coursesOfCurrentStudent.add(course);
                }
            }

          //  SharedPreferences.Editor editor = sp.edit();
           // if (toAdd != null) {
           //     editor.putString("course", toAdd.getNumber());
           // }
           // editor.apply();

            this.studentsCollection.document(this.currentStudent.getId()).set(this.currentStudent).addOnSuccessListener(aVoid -> {
                System.out.println("upload finished");
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void sendBroadcastDbChanged(){
        Intent broadcast = new Intent("changed_db");
        broadcast.putExtra("new_list", getMyCoursesList());
        context.sendBroadcast(broadcast);
    }

    public void saveLocale(String lang){
        SharedPreferences.Editor editor = context.getSharedPreferences("settings", context.MODE_PRIVATE).edit();
        editor.putString("My_lang", lang);
        editor.apply();
    }

    public int getLanguageIndex(){
        SharedPreferences prefs = context.getSharedPreferences("settings", context.MODE_PRIVATE);
        String lang = prefs.getString("My_lang", "");
        if (lang.equals("he")){
            return 1;
        }
        else {
            return 0;
        }
    }

    public String loadLocale(){
        SharedPreferences prefs = context.getSharedPreferences("settings", context.MODE_PRIVATE);
         return prefs.getString("My_lang", "");
    }

    public HashMap<String, String> getRuach_faculty_values_names_map() {
        return ruach_faculty_values_names_map;
    }

    public FirebaseAuth getUsersAuthenticator() {
        return mAuth;
    }

    public void setCurrentFaculty(Faculty faculty){
        this.currentFaculty = faculty;
    }
    public Faculty getCurrentFaculty(){
        return this.currentFaculty;
    }

    public void setCurrentChug(Chug chug){
        this.currentChug = chug;
    }
    public Chug getCurrentChug(){
        return this.currentChug;
    }

    public void setCoursesFromFireBase(ArrayList<Course> coursesToSet){
        this.coursesFromFireBase = new ArrayList<>(coursesToSet);
    }

    public void setCurrentPointsSum(int sum){
        this.currentPointsSum = sum;
    }

    public void setCurrentMandatoryPoints(int currentMandatoryPoints_){
        this.currentMandatoryPoints = currentMandatoryPoints_;
    }

    public void setCurrentMandatoryChoosePoints(int currentMandatoryChoosePoints_){
        this.currentMandatoryChoosePoints = currentMandatoryChoosePoints_;
    }

    public void setCurrentCornerStonesPoints(int currentCornerStonePoints_){
        this.currentCornerStonePoints = currentCornerStonePoints_;
    }

    public int getCurrentCornerStonesPoints(){
        return this.currentCornerStonePoints;
    }

    public int getCurrentMandatoryPoints(){
        return this.currentMandatoryPoints;
    }

    public int getCurrentMandatoryChoosePoints(){
        return this.currentMandatoryChoosePoints;
    }

    public int getCurrentPointsSum(){
        return this.currentPointsSum;
    }

    public void setCoursesRegistration(ArrayList<Course>list)
    {
        this.coursesRegistration = new ArrayList<>(list);
    }

    public int getCurrentChoosePoints(){
        return this.currentChoosePoints;
    }

    public int getCurrentCornerStonePoints(){
        return this.currentCornerStonePoints;
    }

    public int getCurrentSuppPoints(){
        return this.currentSuppPoints;
    }

    public ArrayList<Course> getCoursesRegistration(){
        return new ArrayList<>(this.coursesRegistration);
    }

    public ArrayList<Course> getCoursesFromFireBase(){
        return new ArrayList<>(this.coursesFromFireBase);
    }

    public void setCurrentMaslul(Maslul maslul){
        this.currentMaslul = maslul;
    }
    public Maslul getCurrentMaslul(){
        return this.currentMaslul;
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

    public void setCurrentChoosePoints(int currentChoosePoints_){
        this.currentChoosePoints = currentChoosePoints_;
    }

    public  void setCurrentSuppPoints(int currentSuppPoints_){
        this.currentSuppPoints = currentSuppPoints_;
    }

    public void addStudent(String studentId, String email, String personalName,
                           String familyName, String facultyId, String chugId, String maslulId,
                           String degreeType, String year, String beginYear, String beginSemester,
                           ArrayList<String> courses){

        StudentInfo newStudent = new StudentInfo(studentId, email, personalName, familyName,
                facultyId, chugId, maslulId, degreeType, year, beginYear, beginSemester,
                courses);

        Map<String, StudentInfo> newUser = new HashMap<>();
        setCurrentStudent(newStudent); // todo check
        newUser.put(newStudent.getId(), newStudent);
        this.studentsCollection.document(newStudent.getId()).set(newStudent);
        // todo when loading main screen doesnt have time to set new student
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

    public void updateYear(String year){
        this.currentStudent.setYear(year);
        this.studentsCollection.document(this.currentStudent.getId()).set(this.currentStudent).addOnSuccessListener(aVoid -> {
            System.out.println("upload finished");
        });
    }

    public void updateDegree(String degree){
        this.currentStudent.setDegree(degree);
        this.studentsCollection.document(this.currentStudent.getId()).set(this.currentStudent).addOnSuccessListener(aVoid -> {
            System.out.println("upload finished");
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void removeCourseFromCurrentList(String itemId){

        ArrayList<String> coursesIdList = new ArrayList<>(this.currentStudent.getCourses());
        coursesIdList.remove(itemId); // todo check if exists?
        this.currentStudent.setCourses(coursesIdList);

        Course toDelete = null;
        for (Course item : this.coursesOfCurrentStudent){
            if (item.getNumber().equals(itemId)){
                toDelete = item;
                break;
            }
        }
        if (toDelete != null){
            this.coursesOfCurrentStudent.remove(toDelete);
        }

        SharedPreferences.Editor editor = sp.edit();
        if (toDelete != null) {
            editor.remove(toDelete.getNumber()); // remove the key
        }
        editor.apply();

        //mutableLiveData.setValue(new ArrayList<Course>(this.coursesOfCurrentStudent));
//        sendBroadcastDbChanged();
        // update firebase
      //  this.currentStudent.setCourses(coursesIdList);
        this.studentsCollection.document(this.currentStudent.getId()).set(this.currentStudent).addOnSuccessListener(aVoid -> {
           System.out.println("upload finished");
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

    public void setCurrentStudent(StudentInfo studentInfo){
        this.currentStudent = studentInfo;
    }

    public void setCoursesOfCurrentStudent(ArrayList<Course> courses){
        this.coursesOfCurrentStudent = courses;
    }

    public ArrayList<Course> getCoursesOfCurrentStudent(){
        return new ArrayList<Course>(this.coursesOfCurrentStudent);
    }

    public void AddCourse(){

    }

    public void deleteCourse(){

    }

    public StudentInfo getCurrentStudent(){
        return this.currentStudent;
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

    public FirebaseFirestore getFirestoreDB(){
        return db;
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
