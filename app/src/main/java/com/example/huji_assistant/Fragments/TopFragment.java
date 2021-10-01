package com.example.huji_assistant.Fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.huji_assistant.R;

public class TopFragment extends Fragment {

    public TopFragment(){
        super(R.layout.topfragment);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView text = view.findViewById(R.id.screennametextview);
        text.setText("מסך רישום משתמש חדש");
        text.setVisibility(View.VISIBLE);

    }
}

