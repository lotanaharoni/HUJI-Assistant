package com.example.huji_assistant.Activities;

import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.huji_assistant.Model;
import com.example.huji_assistant.PDFCustomerAdapter;
import com.example.huji_assistant.PDFDoc;
import com.example.huji_assistant.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PDFMainActivity extends AppCompatActivity {

    ArrayList<PDFDoc> pdfDocs;
    private static final int PDF_TYPE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_pdf);
        final ListView lv= (ListView) findViewById(R.id.lv);

        DatabaseReference root = FirebaseDatabase.getInstance().getReference("Image");
        pdfDocs = new ArrayList<>();

        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                PDFDoc pdfDoc;
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Model model = dataSnapshot.getValue(Model.class);
                    assert model != null;
                    if (model.getType() == PDF_TYPE){
                        pdfDoc=new PDFDoc();
                        pdfDoc.setName(model.getName());
                        pdfDoc.setPath(model.getImageUrl());
                        pdfDocs.add(pdfDoc);
                    }
                }
                lv.setAdapter(new PDFCustomerAdapter(PDFMainActivity.this,getPDFs()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private ArrayList<PDFDoc> getPDFs() {
        return pdfDocs;
    }
}
