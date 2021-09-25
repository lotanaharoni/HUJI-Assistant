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
    private String type;
    public enum Type{
        Mandatory, MandatoryChoose, Choose, Supplemental, CornerStones
    }

    public Course(){

    }

    public Course(String name_, String id_ , Type type_){
        name = name_;
        id = id_;
        type = type_.toString();
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

    public void setType(String type_){
        type=type_;
    }

    public String getType(){
        return type;
    }


}