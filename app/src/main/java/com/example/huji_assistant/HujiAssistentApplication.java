package com.example.huji_assistant;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.firebase.FirebaseApp;

import java.util.UUID;

public class HujiAssistentApplication extends Application {

    private UUID id;
    private LocalDataBase dataBase;
    private SharedPreferences sp;
    private static HujiAssistentApplication instance = null;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        instance = this;
        dataBase = new LocalDataBase(this); // pass the current context to allow broadcasts
        sp = this.getSharedPreferences("local_work_sp", Context.MODE_PRIVATE);
        // singleton of work manager
        // androidx.work.WorkManager workManager = androidx.work.WorkManager.getInstance(this);
    }

    public LocalDataBase getDataBase(){
        return dataBase;
    }
    public static HujiAssistentApplication getInstance(){
        return instance;
    }

    UUID getWorkerId(){
        return id;
    }

    public SharedPreferences getWorkSp() {
        return sp;
    }
}
