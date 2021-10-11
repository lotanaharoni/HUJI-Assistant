package com.example.huji_assistant;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShowAttendancyAdapter extends RecyclerView.Adapter<ShowAttendancyAdapter.MyViewHolder> {

    private ArrayList<String> mListCourses;
    private Context context;
    private String savedCourseDocument;
    private int stage;
    private String savedDateDocument;
    private List<String> data;
    private FirebaseFirestore firebaseInstancedb = FirebaseFirestore.getInstance();

    public ShowAttendancyAdapter(Context context, ArrayList<String> mList, int stage, String savedCourseDocument){
        this.context = context;
        this.mListCourses = mList;
        this.stage = stage;
        this.savedCourseDocument = savedCourseDocument;
        this.savedDateDocument = "";
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.attendance_course_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        data = new ArrayList<>();
        holder.imageTitle.setText(mListCourses.get(position));
        if (stage == 2){
            holder.imageView.setImageResource(R.drawable.ic_baseline_person_24);
        }
        else {
            holder.imageView.setImageResource(R.drawable.ic_baseline_folder_24);
        }

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (stage == 0){
                    savedCourseDocument = holder.imageTitle.getText().toString();
                    firebaseInstancedb.collection("attendance").document(holder.imageTitle.getText().toString()).collection("2021").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    data.add(document.getId());
                                }
                                swap(data);
                            }
                        }
                    });
                    stage = 1;
                }
                else if (stage == 1){
                    savedDateDocument = holder.imageTitle.getText().toString();
                    firebaseInstancedb.collection("attendance").document(savedCourseDocument).collection("2021").document(holder.imageTitle.getText().toString()).collection("dummy").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Map<String, Object> map = document.getData();
                                    data.addAll(map.keySet());
                                }
                                swap(data);
                            }
                        }
                    });
                    stage = 2;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mListCourses.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView imageTitle;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.pdfImage);
            imageTitle = itemView.findViewById(R.id.nameTxt);
            imageTitle.setTypeface(null, Typeface.BOLD_ITALIC);
        }
    }

    public void swap(List<String> datas)
    {
        mListCourses.clear();
        mListCourses.addAll(datas);
        notifyDataSetChanged();
    }

    public int getStage(){
        return stage;
    }

    public String getSavedCourseDocument(){
        return this.savedCourseDocument;
    }

    public String getSavedDate(){
        return savedDateDocument;
    }
}
