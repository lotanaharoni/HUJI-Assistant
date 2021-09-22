package com.example.huji_assistant;

import java.util.ArrayList;
import java.util.UUID;

public class Course {

    private String name;
    private String id;
    private ArrayList<Course> prevCourses;
    private boolean isMandatory;
    private String nameOfDegree;
    private String points;

    public Course(){

    }

    public Course(String name_, String id_){
        name = name_;
        id = id_;
    }

    public String getName(){
        return name;
    }

    public String getId(){
        return id;
    }

    public String getPoints(){
        return points;
    }

}