package com.example.huji_assistant;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainScreenFragment extends Fragment {

    private ViewModelApp viewModelApp;

    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    private DatabaseReference root;
    private StorageReference reference;
    String currentPhotoPath;
//84705ccd5979e94064a85a2ea4ab231fb90221b1

    public interface endRegistrationButtonClickListener{
        public void onEndRegistrationBtnClicked();
    }
    public MainScreenFragment.endRegistrationButtonClickListener newUserBtnListener = null;
    public MainScreenFragment.myCoursesButtonListener myCoursesButtonListenerBtn = null;

    public interface myCoursesButtonListener{
        public void onMyCoursesButtonClicked();
    }
    public MainScreenFragment(){
        super(R.layout.mainscreen);
    }
    Spinner dropdown;
    Button myCourses;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        root = FirebaseDatabase.getInstance().getReference("Image");
        reference = FirebaseStorage.getInstance().getReference();
        viewModelApp = new ViewModelProvider(requireActivity()).get(ViewModelApp.class);
        myCourses = view.findViewById(R.id.myCoursesButton);

        FloatingActionButton openCameraFloatingButton = view.findViewById(R.id.open_camera_floating_button);
        viewModelApp.get().observe(getViewLifecycleOwner(), item->{

        });

        myCourses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myCoursesButtonListenerBtn != null) {
                    myCoursesButtonListenerBtn.onMyCoursesButtonClicked();
                }
            }
        });

        openCameraFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(requireActivity(), "Camera is clicked", Toast.LENGTH_SHORT).show();
                // todo add action
                askCameraPermissions();
            }
        });

        //spinner
    }

    private void askCameraPermissions() {
        if(ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        }else {
            dispatchTakePictureIntent();
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(requireActivity(),
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            File f = new File(currentPhotoPath);
//            cameraImageUpload.setImageURI(Uri.fromFile(f));

            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            requireActivity().sendBroadcast(mediaScanIntent);

            uploadToFirebase(contentUri, CAMERA_REQUEST_CODE, f.getName());
        }
    }

    private void uploadToFirebase(Uri uri, int source, String name) {
        StorageReference fileRef = reference.child("Camera_images/" + name);
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Model model = new Model(uri.toString());
                        String modelId = root.push().getKey();
                        assert modelId != null;
//                        progressBar.setVisibility(View.INVISIBLE);
                        root.child(modelId).setValue(model);
                        Toast.makeText(getActivity(), "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
//                progressBar.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(getActivity(), "Failed to upload", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private File createImageFile() throws IOException {
//     Create an image file name
        String imageFileName = "";
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

//     Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

}
