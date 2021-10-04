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
    private ArrayList<AttendancyModel> mList;
    private Context context;
    private ArrayList<String> years;
    private String savedCourseDocument = "";
    private int stage = 0;
    private String savedDateDocument = "";
    private boolean pressedBack;
    List<String> data;
    FirebaseFirestore firebaseInstancedb = FirebaseFirestore.getInstance();

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public ShowAttendancyAdapter(Context context, ArrayList<String> mList, int stage, String savedCourseDocument, boolean pressedBack){
        this.context = context;
        this.mListCourses = mList;
        this.stage = stage;
        this.savedCourseDocument = savedCourseDocument;
        this.pressedBack = pressedBack;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.attendancy_course_item, parent, false);

        final MyViewHolder viewHolder = new MyViewHolder(v);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (stage == 0 && pressedBack){
            data = new ArrayList<>();
            firebaseInstancedb.collection("attendance").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
            pressedBack = false;
        }
        if (stage == 1 && pressedBack){
            data = new ArrayList<>();
            savedCourseDocument = holder.imageTitle.getText().toString();
            firebaseInstancedb.collection("attendance").document(savedCourseDocument).collection("2021").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
            pressedBack = false;
        }
        years = new ArrayList<>();
        data = new ArrayList<>();
        holder.imageTitle.setText(mListCourses.get(position));
        if (stage == 2){
            holder.imageView.setImageResource(R.drawable.ic_baseline_person_24);
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
//                    holder.imageView.setImageResource(R.drawable.ic_baseline_person_24);
                    firebaseInstancedb.collection("attendance").document(savedCourseDocument).collection("2021").document(holder.imageTitle.getText().toString()).collection("dummy").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
//                                holder.studentImageView.setVisibility(View.INVISIBLE);
//                                holder.imageView.setVisibility(View.INVISIBLE);
//                                int id = context.getResources().getIdentifier("com.example.huji_assistant:drawable/university.png", null, null);

                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Map<String, Object> map = document.getData();
                                    //                                        System.out.println( key );
                                    data.addAll(map.keySet());

//                                    StudentInfo student = document.toObject(StudentInfo.class);
//                                    data.add(map.);
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
}
