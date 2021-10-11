package com.example.huji_assistant;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.huji_assistant.Activities.PDFActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PDFCustomerAdapter2 extends RecyclerView.Adapter<PDFCustomerAdapter2.MyViewHolder> {

    private ArrayList<PDFDoc> pdfDocs;
    private Context context;
    private int stage;
    private String savedCourse;
    private String savedDate;
    private DatabaseReference root;
    private List<PDFDoc> dataImages;
    private String savedId;
    private static final int PDF_TYPE = 2;


    public PDFCustomerAdapter2(Context context, ArrayList<PDFDoc> pdfDocs, int stage, String savedCourse){
        this.context = context;
        this.pdfDocs = pdfDocs;
        this.stage = stage;
        this.savedCourse = savedCourse;
        this.savedDate = "";
        this.savedId = "";
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.model_pdf, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        dataImages = new ArrayList<>();

        if (stage == 2){
            holder.imageView.setImageResource(R.drawable.ic_pdf_icon);
//            Glide.with(context).load(mModels.get(position).getImageUrl()).into(holder.imageView);
            holder.imageTitle.setText(pdfDocs.get(position).getName());
        }
        else {
            holder.imageView.setImageResource(R.drawable.ic_baseline_folder_256);
            holder.imageTitle.setText(pdfDocs.get(position).getName());
        }

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (stage == 0) {
                    savedCourse = holder.imageTitle.getText().toString();
                    String path = "Documents" + "/" + savedCourse;
                    root = FirebaseDatabase.getInstance().getReference(path);

                    root.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                String course = dataSnapshot.getKey();
                                PDFDoc pdfDoc = new PDFDoc();
                                pdfDoc.setName(course);
                                dataImages.add(pdfDoc);
//                                dataImages.add(new Model("", course, 0));
                            }
                            swapImages(dataImages);
                            stage = 1;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else if (stage == 1) {
                    savedDate = holder.imageTitle.getText().toString();
                    String path = "Documents" + "/" + savedCourse + "/" + savedDate;
                    root = FirebaseDatabase.getInstance().getReference(path);

                    root.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                String id2 = dataSnapshot.getKey();
                                savedId = dataSnapshot.getKey();
                                holder.id = dataSnapshot.getKey();
                                Model model = dataSnapshot.getValue(Model.class);
                                assert model != null;
                                String course = model.getName();
                                PDFDoc pdfDoc = new PDFDoc();
                                pdfDoc.setName(course);
                                pdfDoc.setId(dataSnapshot.getKey());
                                dataImages.add(pdfDoc);
//                                PDFDoc pdfDoc = dataSnapshot.getValue(PDFDoc.class);
//                                Model model = dataSnapshot.getValue(Model.class);
//                                assert model != null;
//                                dataImages.add(pdfDoc);
//                                if (model.getType() == PDF_TYPE){
//                                    dataImages.add(model);
//                                }
                            }
                            swapImages(dataImages);
                            stage = 2;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else if (stage == 2){
                    String name = holder.imageTitle.getText().toString();
                    String id2 = holder.id;

                    //
//                    String path = "Documents" + "/" + savedCourse + "/" + savedDate + "/" + name;
                    String path = "Documents" + "/" + savedCourse + "/" + savedDate;

                    root = FirebaseDatabase.getInstance().getReference(path);

                    root.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                                String s = dataSnapshot.getKey();
//                                Object s2 = dataSnapshot.getValue();
                                if (dataSnapshot.getKey().equals(pdfDocs.get(position).getId())){
                                    Model model = dataSnapshot.getValue(Model.class);
                                    assert model != null;
                                    if (model.getType() == PDF_TYPE){
                                        PDFDoc pdfDoc = new PDFDoc();
                                        pdfDoc.setName(model.getName());
                                        pdfDoc.setPath(model.getImageUrl());
//                                        dataImages.add(pdfDoc);
                                        openPDFView(pdfDoc.getPath());
                                        break;
                                    }
                                }
//                                PDFDoc pdfDoc = new PDFDoc();
//                                pdfDoc.setName(dataSnapshot.getValue());
//                                PDFDoc pdfDoc = dataSnapshot.getValue(PDFDoc.class);

//                                String course = dataSnapshot.getKey();
//                                PDFDoc pdfDoc = new PDFDoc();
//                                pdfDoc.setName(course);
//                                dataImages.add(pdfDoc);
//                                PDFDoc pdfDoc = dataSnapshot.getValue(PDFDoc.class);
//                                Model model = dataSnapshot.getValue(Model.class);
//                                assert model != null;
//                                dataImages.add(pdfDoc);
//                                if (model.getType() == PDF_TYPE){
//                                    dataImages.add(model);
//                                }
                            }
//                            stage = 3;
//                            swapImages(dataImages);
//                            final PDFDoc pdfDoc2 = (PDFDoc) getItem(position);
//                            final PDFDoc pdfDoc2 = (PDFDoc) dataImages.get(position);
//                            openPDFView(pdfDoc2.getPath());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    //
//                    final PDFDoc pdfDoc= (PDFDoc) pdfDocs.get(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return pdfDocs.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView imageTitle;
        String id;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.pdfImage);
            imageTitle = itemView.findViewById(R.id.nameTxt);
            id = "";
            imageTitle.setTypeface(null, Typeface.BOLD_ITALIC);

        }
    }

    public void swapImages(List<PDFDoc> datas)
    {
        pdfDocs.clear();
        pdfDocs.addAll(datas);
        notifyDataSetChanged();
    }

    public int getStage(){
        return stage;
    }

    public Object getItem(int i) {
        return pdfDocs.get(i);
    }



    public String getSavedCourse(){
        return savedCourse;
    }

    private void openPDFView(String path)
    {
        Intent i=new Intent(context, PDFActivity.class);
        i.putExtra("PATH",path);
        context.startActivity(i);
    }
}
