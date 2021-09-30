package com.example.huji_assistant;

import java.util.ArrayList;
import java.util.UUID;

public class StudentInfo {

    private String personalName;
    private String familyName;
    private String email;
    private Faculty faculty;
    private Chug chug;
    private Maslul maslul;
    private String id;

    private String facultyId; // faculty name
    private String chugId;
    private String maslulId;
    private String degree;
    private String year;
    private String beginYear;
    private String beginSemester;
    private ArrayList<String> coursesMadeByStudent; // list of course id's
    private ArrayList<String> coursesPlannedByStudent;


    public StudentInfo(){

    }

    public StudentInfo(String email_, String personalName_, String familyName_){
        this.id = UUID.randomUUID().toString(); // todo maybe id for db
        this.personalName = personalName_;
        this.familyName = familyName_;
        this.email = email_;
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

    public StudentInfo(String personalName_, String familyName_, String email, String facultyId, String chugId, String maslulId, String degree, String year, String id) {
        this.personalName = personalName_;
        this.familyName = familyName_;
        this.email = email;
        this.facultyId = facultyId;
        this.chugId = chugId;
        this.maslulId = maslulId;
        this.degree = degree;
        this.year = year;
        this.id = id;
    }


   // public StudentInfo(String name, String email, String year){
    //    this.name=name;
      //  this.email=email;
      //  this.year=year;
      //  this.id = UUID.randomUUID().toString();
    //}

    public void setPersonalName(String name){
        this.personalName = name;
    }

    public void setFamilyName(String name){
        this.familyName = name;
    }

    public void setYear(String year){
        this.year = year;
    }

    public void setDegree(String degreeName){
        this.degree = degreeName;
    }

    public void setBeginYear(String year_){
        this.beginYear = year_;
    }

    public void setBeginSemester(String beginSemester_){
        this.beginSemester = beginSemester_;
    }



    public void setFacultyId(String facultyId_){
        this.facultyId = facultyId_;
    }

    public void setChugId(String chugId_){
        this.chugId = chugId_;
    }

    public void setMaslulId(String maslulId_){
        this.maslulId = maslulId_;
    }

    public String toStringP(){
        return " personal name: " + personalName + " family name: " + familyName + " email: " + email + " faculty id: " +
                facultyId + "chug id: " + chugId + " maslulId: " + maslulId + " degree: " + degree + " year: " + year +
                " begin year: " + beginYear + " begin semester: " + beginSemester;
    }



    public String getPersonalName(){
        return personalName;
    }

    public String getFamilyName(){
        return familyName;
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
