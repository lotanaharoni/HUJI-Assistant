package com.example.huji_assistant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CoursesAdapter extends RecyclerView.Adapter<CourseItemHolder> {

    private final ArrayList<Course> list;
    private Context mContext;


    public CoursesAdapter(Context context){
        this.list = new ArrayList<>();
        this.mContext = context;
    }

    public void addCoursesListToAdapter(ArrayList<Course> newList){
        this.list.clear();
        this.list.addAll(newList);
        notifyDataSetChanged();
    }

    public void removeCourseFromAdapter(Course course){
        //list.remove(course);
        this.list.remove(course);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CourseItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.courseitem, parent, false);
        return new CourseItemHolder(view);
    }

    public DeleteClickListener deleteListener;
    public CancelClickListener cancelListener;
    public OnItemClickListener itemClickListener;

    // Create an interface
    public interface DeleteClickListener{
        void onDeleteClick(Course item);
    }

    // Create an interface
    public interface CancelClickListener{
        void onCancelClick(Course item);
    }

    public interface OnItemClickListener {
      //  public void onClick(View view, int position);
        public void onClick(Course item);
    }

    public void setItemClickListener(OnItemClickListener listener){
        this.itemClickListener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull CourseItemHolder holder, int position) {
        Course courseItem = this.list.get(position);
        holder.name.setText(courseItem.getName());
        holder.number.setText(courseItem.getId());

        // todo check if works click on item
        holder.itemView.setOnClickListener(v -> {
            // todo open item's fragment
            // todo make listener
            itemClickListener.onClick(courseItem);
        });

    }

    public int getItemCount() {
        return this.list.size();
    }

    public ArrayList<Course> getItems(){
        return list;
    }
}
