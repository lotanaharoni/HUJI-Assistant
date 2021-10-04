package com.example.huji_assistant.Activities;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.huji_assistant.AttendancyModel;
import com.example.huji_assistant.R;
import com.example.huji_assistant.ShowAttendancyAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShowAttendancyActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<String> courses;
    private ArrayList<AttendancyModel> attendancyFromFireBase;

    public ShowAttendancyAdapter adapter;
    FirebaseFirestore firebaseInstancedb = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_attendancy);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        attendancyFromFireBase = new ArrayList<>();
        courses = new ArrayList<>();
        adapter = new ShowAttendancyAdapter(this, courses, 0, "", false);
        recyclerView.setAdapter(adapter);

        firebaseInstancedb.collection("attendance").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        courses.add(document.getId());
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        int stage = adapter.getStage();
        String savedCourseDocument = adapter.getSavedCourseDocument();

        if (stage == 0){
            super.onBackPressed();
            finish();
        }
        else if (stage == 1){
            adapter = new ShowAttendancyAdapter(this, courses, 0, "", true);
        }
        else if (stage == 2){
            adapter = new ShowAttendancyAdapter(this, courses, 1, savedCourseDocument, true);
        }
        recyclerView.setAdapter(adapter);
        courses = new ArrayList<>();
        firebaseInstancedb.collection("attendance").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        courses.add(document.getId());
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }
}
