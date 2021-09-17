package com.example.huji_assistant;

import java.util.ArrayList;

public class Maslul {

    public String maslulId="";
    public String title="";
    public String chugParentId="";
    public ArrayList<String> degrees = new ArrayList<>();
    // todo change to private

    public Maslul(){

    }

    public Maslul(String title1, String maslulId1, String chugParentId1){
        title = title1;
        maslulId = maslulId1;
        chugParentId = chugParentId1;
        degrees = new ArrayList<>();
    }

    public String toStringP(){
        return "title: " + title + "maslulId: " + maslulId + "chugParentId: " + chugParentId;
    }

    public String getTitle(){
        return title;
    }
}
