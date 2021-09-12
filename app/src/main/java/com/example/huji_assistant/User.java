package com.example.huji_assistant;

import java.util.HashMap;

public class User {
    private String name;
    private String password;
    private String email;
    private int yearOfStudy;
    private int studyPlan;
    private int credits;
    private HashMap<Integer, Integer> coursesAndGrades;

    public User(String name, String password, String email, int yearOfStudy, int studyPlan) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.yearOfStudy = yearOfStudy;
        this.studyPlan = studyPlan;
        this.credits = 0;
        this.coursesAndGrades = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public int getYearOfStudy() {
        return yearOfStudy;
    }

    public int getStudyPlan() {
        return studyPlan;
    }

    public int getCredits() {
        return credits;
    }

    public HashMap<Integer, Integer> getCoursesAndGrades() {
        return coursesAndGrades;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setYearOfStudy(int yearOfStudy) {
        this.yearOfStudy = yearOfStudy;
    }

    public void setStudyPlan(int studyPlan) {
        this.studyPlan = studyPlan;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public void setCoursesAndGrades(HashMap<Integer, Integer> coursesAndGrades) {
        this.coursesAndGrades = coursesAndGrades;
    }
}
