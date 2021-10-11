package com.example.huji_assistant.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.huji_assistant.PDFCustomerAdapter2;
import com.example.huji_assistant.PDFDoc;
import com.example.huji_assistant.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShowPDFActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<PDFDoc> list;

    private PDFCustomerAdapter2 adapter;
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
        root = FirebaseDatabase.getInstance().getReference("Documents");


        list = new ArrayList<>();
        adapter = new PDFCustomerAdapter2(this, list, 0, "");
        recyclerView.setAdapter(adapter);

        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    String course = dataSnapshot.getKey();
                    assert course != null;
                    PDFDoc pdfDoc = new PDFDoc();
                    pdfDoc.setName(course);
                    list.add(pdfDoc);
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
            super.onBackPressed();
//            startActivity(new Intent(ShowPDFActivity.this, MainScreenActivity.class));
//            finish();
        }
        else if (stage == 1){
            adapter = new PDFCustomerAdapter2(this, list, 0, "");
            recyclerView.setAdapter(adapter);
            list = new ArrayList<>();


            root = FirebaseDatabase.getInstance().getReference("Documents");
            root.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        String course = dataSnapshot.getKey();
                        assert course != null;
                        PDFDoc pdfDoc = new PDFDoc();
                        pdfDoc.setName(course);
                        list.add(pdfDoc);
//                        list.add(new Model("", course, 0));
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
            adapter = new PDFCustomerAdapter2(this, list, 1, savedCourse);
            recyclerView.setAdapter(adapter);
            list = new ArrayList<>();

            //
            String path = "Documents" + "/" + savedCourse;
            root = FirebaseDatabase.getInstance().getReference(path);

            root.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String course = dataSnapshot.getKey();
                        PDFDoc pdfDoc = new PDFDoc();
                        pdfDoc.setName(course);
                        list.add(pdfDoc);
//                        list.add(new Model("", course, 0));
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
