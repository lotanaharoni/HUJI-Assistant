package com.example.huji_assistant.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.huji_assistant.Course;
import com.example.huji_assistant.R;
import com.example.huji_assistant.databinding.FragmentCoursesBinding;
import com.example.huji_assistant.databinding.FragmentShowfilesBinding;

public class ShowFilesFragment extends Fragment {

    FragmentShowfilesBinding binding;
    TextView showImages;
    TextView showFiles;

    public interface showImagesClicked{
        public void onImageShowClick();
    }

    public interface showFilesClicked{
        public void onFilesShowClick();
    }
    public ShowFilesFragment.showFilesClicked filesClickedListener = null;
    public ShowFilesFragment.showImagesClicked imagesClickedListener = null;

    public ShowFilesFragment() {
        super(R.layout.fragment_showfiles);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentShowfilesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showFiles = view.findViewById(R.id.showFilesBtn);
        showImages = view.findViewById(R.id.showImagesBtn);

        showFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filesClickedListener != null){
                    System.out.println("files");
                    filesClickedListener.onFilesShowClick();
                }
            }
        });

        showImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imagesClickedListener != null){
                    System.out.println("images");
                    imagesClickedListener.onImageShowClick();
                }
            }
        });

    }
}
