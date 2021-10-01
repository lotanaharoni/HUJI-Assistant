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
        return "name: " + name + " number " + number + " type " + type + " points " + points +" semester "+semester
                +" year: " + year;
    }

    public Course(String name_, String number_ , Type type_, String points_, String semester_){
        name = name_;
        number = number_;
        type = type_.toString();
        this.points = points_;
        this.semester = semester_;
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

    public String getNumber(){
        return number;
    }

    public String getPoints(){
        return points;
    }

    public String getSemester(){
        return semester;
    }

    public String getYear(){
        return year;
    }

    public void setType(String type_){
        type=type_;
    }

    public String getType(){
        return type;
    }
}