package com.example.huji_assistant;

import java.util.UUID;

public class StudentInfo {

    private String name;
    private String email;
    private String password;
    private String degreeName;
    private String year;
    private UUID id;

    public StudentInfo(String email, String password){
        email=email;
        password=password;
        id = UUID.randomUUID();
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
        this.degreeName = degreeName;
    }

    public String getName(){
        return name;
    }
}
