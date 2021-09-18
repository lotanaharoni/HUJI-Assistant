package com.example.huji_assistant;

import java.util.UUID;

public class StudentInfo {

    private String name;
    private String email;
    private String password;

    private String facultyId;
    private String chugId;
    private String maslulId;
    private String degree;
    private String year;
    private UUID id;

    public StudentInfo(String email, String password){
        email=email;
        password=password;
        id = UUID.randomUUID();
    }

    public StudentInfo(String facultyId_, String chugId_, String maslulId_, String degree_, String year_){
        this.facultyId = facultyId_;
        this.chugId = chugId_;
        this.maslulId = maslulId_;
        this.degree = degree_;
        this.year = year_;
    }


    public StudentInfo(String name, String email, String password, String year){
        name=name;
        email=email;
        password=password;
        year=year;
        //  id = UUID.randomUUID();
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
}
