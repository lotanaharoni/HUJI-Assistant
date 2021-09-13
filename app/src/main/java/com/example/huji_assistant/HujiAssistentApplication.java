package com.example.huji_assistant;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.UUID;

public class HujiAssistentApplication extends Application {

    private UUID id = null;
    private LocalDataBase dataBase;
    private SharedPreferences sp;
    private static HujiAssistentApplication instance = null;

    public LocalDataBase getDataBase(){
        return dataBase;
    }
    public static HujiAssistentApplication getInstance(){
        return instance;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate() {
        super.onCreate();

        sp = this.getSharedPreferences("local_work_sp", Context.MODE_PRIVATE);
        instance = this;
        dataBase = new LocalDataBase(this); // pass the current context to allow broadcasts

        // singleton of work manager
        // androidx.work.WorkManager workManager = androidx.work.WorkManager.getInstance(this);
    }

    UUID getWorkerId(){
        return id;
    }

    public SharedPreferences getWorkSp() {
        return sp;
    }
}
