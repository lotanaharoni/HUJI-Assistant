package com.example.huji_assistant;

public class KdamOrAfterCourse {
    private final String number;
    private final String name;
    private final String points;
    private final String semester;
    private final String group;

    public KdamOrAfterCourse(String number, String name, String points, String semester, String group) {
        this.number = number;
        this.name = name;
        this.points = points;
        this.semester = semester;
        this.group = semester;
    }

    public String getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public String getPoints() {
        return points;
    }

    public String getSemester() {
        return semester;
    }

    public String getGroup() {
        return group;
    }
}
