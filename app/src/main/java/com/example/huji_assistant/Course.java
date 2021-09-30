package com.example.huji_assistant;

import java.util.ArrayList;
import java.util.UUID;

public class Course {

    private String name = "";
    private String number = "";
    private String points = "";
    private String semester = "";
    private String type = "";
    private String year = "";
    private boolean isFinished = false; // is the course done

    private ArrayList<Course> prevCourses;
    private boolean isMandatory;
    private String nameOfDegree;
    private boolean isChecked = false;

    public enum Type{
        Mandatory, MandatoryChoose, Choose, Supplemental, CornerStones
    }

    public Course(){

    }

    public String toStringP(){
        return "name: " + name + " number " + number + " type " + type + " points " + points;
    }

    public Course(String name_, String id_ , Type type_){
        name = name_;
        number = id_;
        type = type_.toString();
    }

    public void setChecked(boolean val){
        this.isChecked = val;
    }

    public boolean getChecked(){
        return this.isChecked;
    }

    public String getName(){
        return name;
    }

    public String getId(){
        return number;
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