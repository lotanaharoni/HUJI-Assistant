package com.example.huji_assistant;

public class KdamOrAfterCourse {
    private String number = "";
    private String name = "";
    private String points = "";
    private String semester = "";
    private String group = "";
    private boolean done = false;

    public KdamOrAfterCourse() {

    }

    public KdamOrAfterCourse(String number_, String name_, String points_, String semester_, String group_) {
        this.number = number_;
        this.name = name_;
        this.points = points_;
        this.semester = semester_;
        this.group = group_;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    @Override
    public String toString() {
        return "KdamOrAfterCourse{" +
                "number='" + number + '\'' +
                ", name='" + name + '\'' +
                ", points='" + points + '\'' +
                ", semester='" + semester + '\'' +
                ", group='" + group + '\'' +
                ", done=" + done +
                '}';
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
