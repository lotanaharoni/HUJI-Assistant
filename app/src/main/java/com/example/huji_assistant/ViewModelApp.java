package com.example.huji_assistant;

import android.service.autofill.FillResponse;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.huji_assistant.StudentInfo;

import java.util.List;

public class ViewModelApp extends ViewModel {

    public MutableLiveData<StudentInfo> studentInfoMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<Course> courseMutableLiveData = new MutableLiveData<>();
    // private final Firestore firestore;

    public ViewModelApp(FragmentActivity fragmentActivity){
        // firestore = Firestore.getInstance();
    }

    public ViewModelApp(){

    }

    public void setStudent(StudentInfo student){
        studentInfoMutableLiveData.setValue(student);
    }

    public void setCourse(Course course){
        courseMutableLiveData.setValue(course);
    }

    //public MutableLiveData<StudentInfo> get(Class<ViewModelApp> viewModelAppClass){
    public MutableLiveData<StudentInfo> getStudent(){
        if (studentInfoMutableLiveData == null)
        {
            studentInfoMutableLiveData = new MutableLiveData<>();
        }
        return studentInfoMutableLiveData;
    }

    public MutableLiveData<Course> getCourse(){
        if (courseMutableLiveData == null)
        {
            courseMutableLiveData = new MutableLiveData<>();
        }
        return courseMutableLiveData;
    }

}
