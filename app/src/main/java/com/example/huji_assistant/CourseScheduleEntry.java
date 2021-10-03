package com.example.huji_assistant;

public class CourseScheduleEntry {
    private final String type;
    private final String groupNumber;
    private final String day;
    private final String starting;
    private final String ending;
    private final String location;
    private final String group;
    private final String teacher;

    public CourseScheduleEntry(String type, String groupNumber, String day, String starting, String ending, String location, String group, String teacher) {
        this.type = type;
        this.groupNumber = groupNumber;
        this.day = day;
        this.starting = starting;
        this.ending = ending;
        this.location = location;
        this.group = group;
        this.teacher = teacher;
    }

    public String getType() {
        return type;
    }

    public String getGroupNumber() {
        return groupNumber;
    }

    public String getDay() {
        return day;
    }

    public String getStarting() {
        return starting;
    }

    public String getEnding() {
        return ending;
    }

    public String getLocation() {
        return location;
    }

    public String getGroup() {
        return group;
    }

    public String getTeacher() {
        return teacher;
    }

    @Override
    public String toString() {
        return "CourseScheduleEntry{" +
                "type='" + type + '\'' +
                ", groupNumber='" + groupNumber + '\'' +
                ", day='" + day + '\'' +
                ", starting='" + starting + '\'' +
                ", ending='" + ending + '\'' +
                ", location='" + location + '\'' +
                ", group='" + group + '\'' +
                ", teacher='" + teacher + '\'' +
                '}';
    }
}
