package com.example.huji_assistant;

import java.util.ArrayList;

public class Faculty {

    public String title="";
    public String facultyId="";
    public ArrayList<Chug> chugimInFaculty= new ArrayList<>();
    // todo change to private

    public Faculty(){

    }

    public Faculty(String title1, String facultyId1){
        title = title1;
        facultyId = facultyId1;
        chugimInFaculty = new ArrayList<>();
    }

    public String toStringP(){
        return "title: " + title + "facultyId: " + facultyId;
    }

    public void addChugToFaculty(Chug chug){
        chugimInFaculty.add(chug);
    }

    public String getFacultyId(){
        return facultyId;
    }
}
