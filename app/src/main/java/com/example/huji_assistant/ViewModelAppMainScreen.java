package com.example.huji_assistant;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ViewModelAppMainScreen extends ViewModel {

    public MutableLiveData<Course> courseMutableLiveData = new MutableLiveData<>();

    public ViewModelAppMainScreen(FragmentActivity fragmentActivity){
        // firestore = Firestore.getInstance();
    }

    public ViewModelAppMainScreen(){

    }

    public void set(Course course){
        courseMutableLiveData.setValue(course);
    }

    //public MutableLiveData<StudentInfo> get(Class<ViewModelApp> viewModelAppClass){
    public MutableLiveData<Course> get(){
        if (courseMutableLiveData == null)
        {
            courseMutableLiveData = new MutableLiveData<>();
        }
        return courseMutableLiveData;
    }
}
