package com.example.huji_assistant;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class KdamCoursesAdapter extends RecyclerView.Adapter<KdamCourseItemHolder>{

    private ArrayList<KdamOrAfterCourse> list;
    private Context mContext;

    public KdamCoursesAdapter(Context context){
        this.list = new ArrayList<>();
        this.mContext = context;
    }

    public void addKdamCoursesListToAdapter(ArrayList<KdamOrAfterCourse> newList){
        this.list.clear();
        this.list.addAll(newList);
        notifyDataSetChanged();
    }

    public void removeCourseFromAdapter(KdamOrAfterCourse course){
        //list.remove(course);
        this.list.remove(course);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public KdamCourseItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.kdamcourseitem, parent, false);
        return new KdamCourseItemHolder(view);
    }

    public com.example.huji_assistant.CoursesAdapter.DeleteClickListener deleteListener;
    public com.example.huji_assistant.CoursesAdapter.CancelClickListener cancelListener;
    public com.example.huji_assistant.CoursesAdapter.OnItemClickListener itemClickListener;
    public com.example.huji_assistant.CoursesAdapter.OnCheckBoxClickListener checkBoxClickListener;
        // public OnTextBoxClickListener textBoxClickListener;


        // Create an interface
        public interface DeleteClickListener{
            void onDeleteClick(View v, Course item);
        }

        // Create an interface
        public interface CancelClickListener{
            void onCancelClick(Course item);
        }

        public interface OnCheckBoxClickListener{
            void onCheckBoxClicked(View v, Course item);
        }

        public interface OnItemClickListener {
            //  public void onClick(View view, int position);
            public void onClick(Course item);
        }

        // public interface OnTextBoxClickListener {
        //  public void onClick(View view, int position);
        //     public void onTextBoxClick(Course item);
        // }

        public void setItemClickListener(com.example.huji_assistant.CoursesAdapter.OnItemClickListener listener){
            this.itemClickListener = listener;
        }

        public void setItemCheckBoxListener(com.example.huji_assistant.CoursesAdapter.OnCheckBoxClickListener listener){
            this.checkBoxClickListener = listener;
        }

        public void setDeleteListener(com.example.huji_assistant.CoursesAdapter.DeleteClickListener listener){
            this.deleteListener = listener;
        }

        // public void setTextBoxClickListener(OnTextBoxClickListener listener){
        ///     this.textBoxClickListener = listener;
        //  }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull KdamCourseItemHolder holder, int position) {
        KdamOrAfterCourse courseItem = this.list.get(position);
        holder.name.setText(courseItem.getName());
        holder.number.setText(courseItem.getNumber());
    }


    public int getItemCount() {
        return this.list.size();
    }

    public ArrayList<KdamOrAfterCourse> getItems(){
        return list;
    }

}
