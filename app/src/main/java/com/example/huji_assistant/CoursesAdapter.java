package com.example.huji_assistant;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CoursesAdapter extends RecyclerView.Adapter<CourseItemHolder> {
    private  ArrayList<Course> list;
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
    }

    private Context mContext;
    ArrayList<Course> filterList;
    CustomFilter filter;

    public Filter getFilter(){
        if (filter == null){
            filter = new CustomFilter();
        }
        return filter;
    }

    public CoursesAdapter(Context context){
        this.list = new ArrayList<>();
        this.filterList = list;
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
    public OnCheckBoxClickListener checkBoxClickListener;
   // public OnTextBoxClickListener textBoxClickListener;


    // Create an interface
    public interface DeleteClickListener{
        void onDeleteClick(Course item);
    }

    // Create an interface
    public interface CancelClickListener{
        void onCancelClick(Course item);
    }

    public interface OnCheckBoxClickListener{
        void onCheckBoxClicked(Course item);
    }

    public interface OnItemClickListener {
      //  public void onClick(View view, int position);
        public void onClick(Course item);
    }

   // public interface OnTextBoxClickListener {
        //  public void onClick(View view, int position);
   //     public void onTextBoxClick(Course item);
   // }

    public void setItemClickListener(OnItemClickListener listener){
        this.itemClickListener = listener;
    }

    public void setItemCheckBoxListener(OnCheckBoxClickListener listener){
        this.checkBoxClickListener = listener;
    }

   // public void setTextBoxClickListener(OnTextBoxClickListener listener){
   ///     this.textBoxClickListener = listener;
  //  }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull CourseItemHolder holder, int position) {
        Course courseItem = this.list.get(position);
        holder.name.setText(courseItem.getName());
        holder.number.setText(courseItem.getNumber());
        holder.type.setText(courseItem.getType());

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
            checkBoxClickListener.onCheckBoxClicked(courseItem);
        });

        holder.itemView.setOnClickListener(v -> {
            itemClickListener.onClick(courseItem);
        });

        String courseType = courseItem.getType();
        switch (courseType) {
            case "Mandatory":
                holder.type.setText("חובה");
                break;
            case "MandatoryChoose":
                holder.type.setText("חובת בחירה");
                break;
            case "Choose":
                holder.type.setText("בחירה");
                break;
            case "Supplemental":
                holder.type.setText("משלימים");
                break;
            case "CornerStones":
                holder.type.setText("אבני פינה");
                break;
            default:
                break;
        }



        if (courseItem.getType().equals(Course.Type.Mandatory.toString())){
          // holder.itemView.setBackgroundColor(R.color.colorAccent);
           holder.cardView.setCardBackgroundColor(this.mContext.getResources().getColor(R.color.lightblue1));
        }
        else if (courseItem.getType().equals(Course.Type.MandatoryChoose.toString())){
           // holder.itemView.setBackgroundColor(R.color.light_teal);
            holder.cardView.setCardBackgroundColor(this.mContext.getResources().getColor(R.color.lightblue2));
        }
        else if (courseItem.getType().equals(Course.Type.Choose.toString())){
         //   holder.itemView.setBackgroundColor(R.color.purple_200);
            holder.cardView.setCardBackgroundColor(this.mContext.getResources().getColor(R.color.lightblue3));
        }
        else if (courseItem.getType().equals(Course.Type.Supplemental.toString())){
           // holder.itemView.setBackgroundColor(R.color.purple_500);
            holder.cardView.setCardBackgroundColor(this.mContext.getResources().getColor(R.color.lightblue4));
        }
        else if (courseItem.getType().equals(Course.Type.CornerStones.toString())){
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
