package com.example.huji_assistant;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tooltip.Tooltip;

import java.util.ArrayList;
import java.util.List;

public class CoursesAdapter extends RecyclerView.Adapter<CourseItemHolder> implements Filterable {
    private  ArrayList<Course> list;
    private ArrayList<Course> filteredList = new ArrayList<>();
    private ArrayList<Course> listFull;

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                System.out.println("char: " + charString);
                ArrayList<Course> filteredList = new ArrayList<>();
                if (charSequence == null || charSequence.length() == 0){
                    filteredList.addAll(listFull);
                }

               // if (charString.isEmpty()) {
               //     filteredList = list;
                else {
                   // ArrayList<Course> filteredList = new ArrayList<>();
                    for (Course item : list) {
                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (item.getName().toLowerCase().contains(charString.toLowerCase()) || item.getNumber().contains(charSequence)) {
                            filteredList.add(item);
                        }
                    }
                   // list = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                list = (ArrayList<Course>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
/**
    class CustomFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint != null && constraint.length() > 0) {

                constraint = constraint.toString().toUpperCase();
                ArrayList<String> filters = new ArrayList<>();

                for (int i = 0; i < filterList.size(); i++) {
                    if (filterList.get(i).getName().toUpperCase().contains(constraint)) {
                        filters.add(filterList.get(i).getName());
                    }
                }

                results.count = filters.size();
                results.values = filters;
            } else {
                results.count = filterList.size();
                results.values = filterList;
            }
            return results;
        }

        protected void publishResults(CharSequence constraint, FilterResults results) {
            list = (ArrayList<Course>) results.values;
            notifyDataSetChanged();
        }
    }*/

    private Context mContext;
    ArrayList<Course> filterList;
   // CustomFilter filter;

   // public Filter getFilter(){
     //   if (filter == null){
     //       filter = new CustomFilter();
     //   }
     //   return filter;
   // }

    public CoursesAdapter(Context context){
        this.list = new ArrayList<>();
        this.filterList = list;
       // this.filterList = new ArrayList<>(list); // todo keep?
        this.mContext = context;
      //  this.listFull = new ArrayList<>();
    }

    public void addCoursesListToAdapter(ArrayList<Course> newList){
        this.list.clear();
        this.listFull = new ArrayList<>(newList);
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
    public OnCheckBoxClickListener checkBoxClickListener;
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

    public void setItemClickListener(OnItemClickListener listener){
        this.itemClickListener = listener;
    }

    public void setItemCheckBoxListener(OnCheckBoxClickListener listener){
        this.checkBoxClickListener = listener;
    }

    public void setDeleteListener(DeleteClickListener listener){
        this.deleteListener = listener;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull CourseItemHolder holder, int position) {
        Course courseItem = this.list.get(position);
        holder.name.setText(courseItem.getName());
        holder.number.setText(courseItem.getNumber());
        holder.type.setText(courseItem.getType());
        holder.grade.setVisibility(View.INVISIBLE);
        holder.deleteButton.setVisibility(View.INVISIBLE);
        String text = courseItem.getPoints() + " נ''ז ";
        holder.points.setText(text);


        //holder.name.setTooltipText(courseItem.getName());


        /**
        Tooltip tooltip = new Tooltip.Builder(holder.itemView)
                .setText(courseItem.getName())
                .show();*/





        // Show the grade only for my fragment courses
        if (courseItem.getGrade() != -1) {
            holder.grade.setVisibility(View.VISIBLE);
          //  holder.grade.setText(courseItem.getGrade()); // todo check
            holder.grade.setText("");
        }

        // todo check - when added courses write is finished == true
        if (courseItem.getIsFinished()){
            holder.checkBox.setVisibility(View.INVISIBLE);
            holder.deleteButton.setVisibility(View.VISIBLE);
        }

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("delete button clicked");
                deleteListener.onDeleteClick(v, courseItem);
            }
        });

        // todo - enable edit grade?

        //todo remove
       // holder.textView.setOnClickListener(v -> {
       //     System.out.println("text box clicked");
       // });

        holder.checkBox.setOnClickListener(v -> {
            System.out.println("check box clicked");
            if (holder.checkBox.isChecked()){
                courseItem.setChecked(true);
            }
            else{
                courseItem.setChecked(false);
            }
            checkBoxClickListener.onCheckBoxClicked(v, courseItem);
        });

        if (courseItem.getChecked()){
            holder.checkBox.setChecked(true);
        }
        else{
            holder.checkBox.setChecked(false);
        }

        holder.itemView.setOnClickListener(v -> {
            itemClickListener.onClick(courseItem);
        });


        if (courseItem.getType().equals("לימודי חובה")){
          // holder.itemView.setBackgroundColor(R.color.colorAccent);
           holder.cardView.setCardBackgroundColor(this.mContext.getResources().getColor(R.color.lightblue1));
        }
        else if (courseItem.getType().equals("לימודי חובת בחירה")){
           // holder.itemView.setBackgroundColor(R.color.light_teal);
            holder.cardView.setCardBackgroundColor(this.mContext.getResources().getColor(R.color.lightblue2));
        }
        else if (courseItem.getType().equals("קורסי בחירה")){
         //   holder.itemView.setBackgroundColor(R.color.purple_200);
            holder.cardView.setCardBackgroundColor(this.mContext.getResources().getColor(R.color.lightblue3));
        }
        else if (courseItem.getType().equals("משלימים")){
           // holder.itemView.setBackgroundColor(R.color.purple_500);
            holder.cardView.setCardBackgroundColor(this.mContext.getResources().getColor(R.color.lightblue4));
        }
        else if (courseItem.getType().equals("אבני פינה")){
        //    holder.itemView.setBackgroundColor(R.color.purple_700);
            holder.cardView.setCardBackgroundColor(this.mContext.getResources().getColor(R.color.lightblue5));
        }
        else{

        }

    }

    public int getItemCount() {
        return this.list.size();
    }

    public ArrayList<Course> getItems(){
        return list;
    }
}
