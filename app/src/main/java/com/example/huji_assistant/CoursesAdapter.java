package com.example.huji_assistant;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.huji_assistant.Activities.MainScreenActivity;
import com.tooltip.Tooltip;

import java.util.ArrayList;
import java.util.List;

public class CoursesAdapter extends RecyclerView.Adapter<CourseItemHolder> implements Filterable {
    private  ArrayList<Course> list;
    private ArrayList<Course> filteredList = new ArrayList<>();
    private ArrayList<Course> listFull;
    public LocalDataBase dataBase = HujiAssistentApplication.getInstance().getDataBase();

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

    private Context mContext;
    ArrayList<Course> filterList;

    public CoursesAdapter(Context context){
        this.list = new ArrayList<>();
      //  System.out.println("adapter rrrr");
       // this.filterList = list;
        this.filterList = new ArrayList<>(list); // todo keep?
        this.mContext = context;
      //  this.listFull = new ArrayList<>();
    }

    public void addCoursesListToAdapter(ArrayList<Course> newList){
      //  if (this.list == null){
            this.list = new ArrayList<>();
      //  }
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
  //  public PopupDialogFragment dialogFragment = new PopupDialogFragment();
    public DeleteClickListener deleteListener;
    public AddGradeListener addGradeListener;
    public CancelClickListener cancelListener;
    public OnItemClickListener itemClickListener;
    public OnCheckBoxClickListener checkBoxClickListener;
    public static OnPopUpApproveListener onPopUpApproveListener;
   // public OnTextBoxClickListener textBoxClickListener;


    // Create an interface
    public interface DeleteClickListener{
        void onDeleteClick(View v, Course item);
    }

    public interface OnPopUpApproveListener{
        void OnPopUpClick(String item, String grade);
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

    public interface AddGradeListener {
        //  public void onClick(View view, int position);
        public void onAddGradeClick(Course item, String grade);
    }

    public void setItemClickListener(OnItemClickListener listener){
        this.itemClickListener = listener;
    }

    public void setOnPopUpListener(OnPopUpApproveListener onPopUpListener){
        this.onPopUpApproveListener = onPopUpListener;
    }

    public void setItemCheckBoxListener(OnCheckBoxClickListener listener){
        this.checkBoxClickListener = listener;
    }

    public void setDeleteListener(DeleteClickListener listener){
        this.deleteListener = listener;
    }

    public void setGradeListener(AddGradeListener listener){
        this.addGradeListener = listener;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull CourseItemHolder holder, int position) {
        Course courseItem = this.list.get(position);
        holder.name.setText(courseItem.getName());
        holder.number.setText(courseItem.getNumber());
        holder.type.setText(courseItem.getType());
        holder.gradeAddBtn.setText(dataBase.getGradesOfStudent().get(courseItem.getNumber()));
       // holder.gradeAddBtn.setText(courseItem.getGradeFromDb());
        holder.grade.setVisibility(View.INVISIBLE);
        holder.deleteButton.setVisibility(View.INVISIBLE);
        holder.gradeAddBtn.setVisibility(View.INVISIBLE);
        holder.gradeAddBtn.setEnabled(true);
        String text = courseItem.getPoints() + " נ''ז ";
        holder.points.setText(text);

        // todo retrieve
        if (dataBase.getGradesOfStudent().containsKey(courseItem.getNumber())) {
            holder.gradeAddBtn.setText(dataBase.getGradesOfStudent().get(courseItem.getNumber()));
        }
        else{ // The grade doesn't exist in the map of grades
            holder.gradeAddBtn.setText("");
        }

        // Show the grade only for my fragment courses
      //  if (courseItem.getGrade() != -1) {
         //   holder.grade.setVisibility(View.VISIBLE);
          //  holder.grade.setText(courseItem.getGrade()); // todo check
         //   holder.grade.setText("");
      //  }

        // todo check - when added courses write is finished == true
        if (courseItem.getIsFinished()){
            holder.checkBox.setVisibility(View.INVISIBLE);
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.gradeAddBtn.setVisibility(View.VISIBLE);
            holder.gradeDescription.setVisibility(View.VISIBLE);
         //   holder.saveTextiew.setVisibility(View.VISIBLE);
        }

       // holder.gradeAddBtn.setOnClickListener(new View.OnClickListener() {
        //    @Override
         //   public void onClick(View v) {
                //showPopup(v);
              //  addGradeListener.onAddGradeClick(v, courseItem);
           // }
     //   });

        // This method runs when the text in grade edit text is changed
        holder.gradeAddBtn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String grade = holder.gradeAddBtn.getText().toString(); // Saves the grade
                // todo validity check >=0 && <=100 checkValidity(grade);
                // if (isValidGrade){

              //  if (!grade.isEmpty()) {//todo check
                addGradeListener.onAddGradeClick(courseItem, grade); // todo check
              //  }
            }

            @Override
            public void afterTextChanged(Editable s) {
               // String grade = holder.gradeAddBtn.getText().toString(); // Saves the grade
                //addGradeListener.onAddGradeClick(courseItem, grade); // todo check
            }
        });

        holder.gradeAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("grade clicked");
                showPopup(courseItem, v);
                holder.gradeAddBtn.setEnabled(true);
            }
        });

        holder.gradeAddBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                System.out.println("long clicked");
                holder.gradeAddBtn.setEnabled(true);
                return false;
            }
        });

        holder.gradeAddBtn.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View v, MotionEvent event) {
                Tooltip tooltip = new Tooltip.Builder(holder.itemView)
                        .setText(R.string.grade_tooltip)
                        .show();
                return false;
            }
        });

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
              //  holder.gradeAddBtn.setVisibility(View.VISIBLE); //todo delete
            }
            else{
                courseItem.setChecked(false);
            }
            checkBoxClickListener.onCheckBoxClicked(v, courseItem);
        });

        if (courseItem.getChecked()){
            holder.checkBox.setChecked(true);
            // todo can enter grade only if selected this course
            // and it exists in the list of courses
            holder.gradeAddBtn.setVisibility(View.VISIBLE);
            holder.gradeAddBtn.setEnabled(false);
        }
        else{
            holder.checkBox.setChecked(false);
        }

        holder.itemView.setOnClickListener(v -> {
            itemClickListener.onClick(courseItem);
        });

        if (courseItem.getType().equals("לימודי חובה")){
           holder.cardView.setCardBackgroundColor(this.mContext.getResources().getColor(R.color.lightblue1));
        }
        else if (courseItem.getType().equals("לימודי חובת בחירה")){
            holder.cardView.setCardBackgroundColor(this.mContext.getResources().getColor(R.color.lightblue2));
        }
        else if (courseItem.getType().equals("קורסי בחירה")){
            holder.cardView.setCardBackgroundColor(this.mContext.getResources().getColor(R.color.lightblue3));
        }
        else if (courseItem.getType().equals("משלימים")){
            holder.cardView.setCardBackgroundColor(this.mContext.getResources().getColor(R.color.lightblue4));
        }
        else if (courseItem.getType().equals("אבני פינה")){
            holder.cardView.setCardBackgroundColor(this.mContext.getResources().getColor(R.color.lightblue5));
        }
        else{

        }
    }
    public static class PopupDialogFragment extends DialogFragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.popup_layout, container,
                    false);

            return rootView;
        }

     //   public PopupDialogFragment.OnPopUpApproveListener onPopUpApproveListener;
      //  public void setOnPopUpListener(PopupDialogFragment.OnPopUpApproveListener onPopUpListener){
      //      this.onPopUpApproveListener = onPopUpListener;
      //  }
       // public interface OnPopUpApproveListener{
       //     void OnPopUpClick(String item, String grade);
       // }

        private String item_number;
        private String item_grade;
        public PopupDialogFragment(Course item){
            this.item_number = item.getNumber();
        }
        Button approveBtn;
        EditText grade;
        String gradeStr="333";
        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
             approveBtn = view.findViewById(R.id.approveBtn);
             grade = view.findViewById(R.id.editTextNumber1);
             gradeStr = grade.getText().toString();
             System.out.println("gradeee: " + gradeStr);

             grade.addTextChangedListener(new TextWatcher() {
                 @Override
                 public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                 }

                 @Override
                 public void onTextChanged(CharSequence s, int start, int before, int count) {
                     String gradeStr = grade.getText().toString();
                     System.out.println("gradeee2: " + gradeStr);
                 }

                 @Override
                 public void afterTextChanged(Editable s) {

                 }
             });

             approveBtn.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     gradeStr = grade.getText().toString();
                     System.out.println("clicked approve: " + item_number + " " + gradeStr);
                     if (onPopUpApproveListener != null) {
                         onPopUpApproveListener.OnPopUpClick(item_number, gradeStr);
                     }
                 }
             });
        }
    }

    public void showPopup(Course item, View view) {
        @SuppressLint("InflateParams") View popupView = LayoutInflater.from(mContext).inflate(R.layout.popup_layout, null);
        System.out.println("inside popup");
        PopupDialogFragment dialogFragment = new PopupDialogFragment(item);
        dialogFragment.show(((MainScreenActivity)mContext).getSupportFragmentManager(), "OpenPopup");
    }

    public int getItemCount() {
        if (this.list != null) {
            return this.list.size();
        }
        else{
            return 0;
        }
    }

    public ArrayList<Course> getItems(){
        return list;
    }
}
