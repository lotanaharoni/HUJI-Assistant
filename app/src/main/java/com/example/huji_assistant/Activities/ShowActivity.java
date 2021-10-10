package com.example.huji_assistant.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.huji_assistant.Model;
import com.example.huji_assistant.R;
import com.example.huji_assistant.ShowAttendancyAdapter;
import com.example.huji_assistant.ShowImagesAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ShowActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Model> list;

    private ShowImagesAdapter adapter;
    private DatabaseReference root;
    private static final int PDF_TYPE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        root = FirebaseDatabase.getInstance().getReference("Image");


        list = new ArrayList<>();
        adapter = new ShowImagesAdapter(this, list, 0, "");
        recyclerView.setAdapter(adapter);

        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    String course = dataSnapshot.getKey();
                    assert course != null;
                    list.add(new Model("", course, 0));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        int stage = adapter.getStage();
        String savedCourse = adapter.getSavedCourse();

        if (stage == 0){
            startActivity(new Intent(ShowActivity.this, CaptureImageActivity.class));
            finish();
        }
        else if (stage == 1){
            adapter = new ShowImagesAdapter(this, list, 0, "");
            recyclerView.setAdapter(adapter);
            list = new ArrayList<>();


            root = FirebaseDatabase.getInstance().getReference("Image");
            root.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        String course = dataSnapshot.getKey();
                        assert course != null;
                        list.add(new Model("", course, 0));
                    }
                    adapter.swapImages(list);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        else if (stage == 2){
//            courses = new ArrayList<>();
            adapter = new ShowImagesAdapter(this, list, 1, savedCourse);
            recyclerView.setAdapter(adapter);
            list = new ArrayList<>();

            //
            String path = "Image" + "/" + savedCourse;
            root = FirebaseDatabase.getInstance().getReference(path);

            root.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String course = dataSnapshot.getKey();
                        list.add(new Model("", course, 0));
                    }
                    adapter.swapImages(list);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}
