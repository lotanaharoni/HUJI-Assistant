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

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ShowImagesAdapter extends RecyclerView.Adapter<ShowImagesAdapter.MyViewHolder> {

    private ArrayList<Model> mList;
    private Context context;

    public ShowImagesAdapter(Context context, ArrayList<Model> mList){
        this.context = context;
        this.mList = mList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(mList.get(position).getImageUrl()).into(holder.imageView);
        holder.imageTitle.setText(mList.get(position).getName());
//        Glide.with(context).load(mList.get(position).getName()).into(holder.imageTitle);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView imageTitle;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.m_image);
            imageTitle = itemView.findViewById(R.id.info_text);
            imageTitle.setTypeface(null, Typeface.BOLD_ITALIC);

        }
    }
}
