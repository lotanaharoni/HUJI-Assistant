package com.example.huji_assistant;

import java.util.HashMap;

import com.example.huji_assistant.StudentInfo;

public interface FirebaseUsersUpdateCallback {
    void onCallback(HashMap<String, StudentInfo> users);
}
