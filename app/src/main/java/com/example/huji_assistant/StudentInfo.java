package com.example.huji_assistant;

import java.util.UUID;

public class StudentInfo {

    private String name;
    private String email;

    private String facultyId;
    private String chugId;
    private String maslulId;
    private String degree;
    private String year;
    private String beginYear;
    private String beginSemester;
    private String id;

    public StudentInfo(){

    }

    public StudentInfo(String email){
        this.id = UUID.randomUUID().toString();
        this.email = email;
    }

    public StudentInfo(String studentId, String email){
        this.id = studentId;
        this.email = email;
    }

    public StudentInfo(String facultyId_, String chugId_, String maslulId_, String degree_, String year_, String beginYear_, String beginSemester_){
        this.id = UUID.randomUUID().toString();
        this.facultyId = facultyId_;
        this.chugId = chugId_;
        this.maslulId = maslulId_;
        this.degree = degree_;
        this.year = year_;
        this.beginYear = beginYear_;
        this.beginSemester = beginSemester_;
    }

    public StudentInfo(String name, String email, String facultyId, String chugId, String maslulId, String degree, String year, String id) {
        this.name = name;
        this.email = email;
        this.facultyId = facultyId;
        this.chugId = chugId;
        this.maslulId = maslulId;
        this.degree = degree;
        this.year = year;
        this.id = id;
    }


    public StudentInfo(String name, String email, String year){
        this.name=name;
        this.email=email;
        this.year=year;
        this.id = UUID.randomUUID().toString();
    }

    public void setName(String name){
        this.name = name;
    }

    public void setYear(String year){
        this.year = year;
    }

    public void setDegreeName(String degreeName){
        this.degree = degreeName;
    }

    public String getName(){
        return name;
    }

    public String getFacultyId(){
        return facultyId;
    }

    public String getChugId(){
        return chugId;
    }

    public String getMaslulId(){
        return maslulId;
    }

    public String getDegree(){
        return degree;
    }

    public String getYear(){
        return year;
    }

    public String getBeginYear(){
        return beginYear;
    }

    public String getBeginSemester(){
        return beginSemester;
    }

    public String getId(){ return id;}

    public String getEmail() {
        return email;
    }
}
