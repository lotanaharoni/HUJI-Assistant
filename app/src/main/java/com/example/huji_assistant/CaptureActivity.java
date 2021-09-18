package com.example.huji_assistant;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CaptureActivity extends AppCompatActivity {
    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int DOCUMENTS_REQUEST_CODE = 104;
    public static final int GALLERY_REQUEST_CODE = 105;
    ImageView selectedImage;
    EditText imageTitle;
    String currentPhotoPath;
    StorageReference storageReference;
    ProgressDialog dialog;
    Uri imageuri = null;
    ImageView pdfImageUpload, galleryImageUpload, cameraImageUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);

        selectedImage = findViewById(R.id.displayImageView);
        cameraImageUpload = findViewById(R.id.cameraImageUpload);
        galleryImageUpload = findViewById(R.id.galleryImageUpload);
        imageTitle = findViewById(R.id.imageTitle);
        pdfImageUpload = findViewById(R.id.pdfImageUpload);

        imageTitle.setText("");

        storageReference = FirebaseStorage.getInstance().getReference();

        cameraImageUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CaptureActivity.this, "Camera is clicked", Toast.LENGTH_SHORT).show();
                askCameraPermissions();
            }
        });

        galleryImageUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(CaptureActivity.this, "Gallery is clicked", Toast.LENGTH_SHORT).show();
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery, GALLERY_REQUEST_CODE);
            }
        });

        pdfImageUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                // We will be redirected to choose pdf
                galleryIntent.setType("application/pdf");
                startActivityForResult(galleryIntent, DOCUMENTS_REQUEST_CODE);
            }
        });
    }

    private void askCameraPermissions() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        }else {
            dispatchTakePictureIntent();
        }

    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Camera Permission is Required to Use camera.", Toast.LENGTH_SHORT).show();
            }
        }
    }



    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                dialog = new ProgressDialog(this);
                dialog.setMessage("Uploading");

                // this will show message uploading
                // while pdf is uploading
                dialog.show();

                File f = new File(currentPhotoPath);
                selectedImage.setImageURI(Uri.fromFile(f));

                Log.d("tag", "ABsolute Url of Image is " + Uri.fromFile(f));
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);

                uploadImageToFirebase(f.getName(), contentUri);
            }
        }

        if (requestCode == GALLERY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                dialog = new ProgressDialog(this);
                dialog.setMessage("Uploading");

                // this will show message uploading
                // while pdf is uploading
                dialog.show();

                Uri contentUri = data.getData();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "JPEG_" + timeStamp + "." + getFileExt(contentUri);
                Log.d("tag", "onActivityResult: Gallery Image Uri:  " + imageFileName);
                selectedImage.setImageURI(contentUri);

                uploadImageToFirebase(imageFileName, contentUri);
            }
        }

        if (requestCode == DOCUMENTS_REQUEST_CODE){
            if (resultCode == Activity.RESULT_OK) {
                // Here we are initialising the progress dialog box
                dialog = new ProgressDialog(this);
                dialog.setMessage("Uploading");

                // this will show message uploading
                // while pdf is uploading
                dialog.show();
                imageuri = data.getData();
                final String timestamp = "" + System.currentTimeMillis();
                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                final String messagePushID = timestamp;
                Toast.makeText(CaptureActivity.this, imageuri.toString(), Toast.LENGTH_SHORT).show();

                // Here we are uploading the pdf in firebase storage with the name of current time
                final StorageReference filepath = storageReference.child(messagePushID + "." + "pdf");
                Toast.makeText(CaptureActivity.this, filepath.getName(), Toast.LENGTH_SHORT).show();
                filepath.putFile(imageuri).continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            // After uploading is done it progress
                            // dialog box will be dismissed
                            dialog.dismiss();
                            Uri uri = task.getResult();
                            String myurl;
                            myurl = uri.toString();
                            Toast.makeText(CaptureActivity.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            dialog.dismiss();
                            Toast.makeText(CaptureActivity.this, "UploadedFailed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    private void uploadImageToFirebase(String name, Uri contentUri) {
        StorageReference image = storageReference.child("image/" + name);
        image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("tag", "onSuccess: Uploaded Image URl is " + uri.toString());
                        imageTitle.setText("");
                        dialog.dismiss();
                    }
                });

                Toast.makeText(CaptureActivity.this, "Image Is Uploaded.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CaptureActivity.this, "Upload Failled.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });    }

    private String getFileExt(Uri contentUri) {
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }

    private File createImageFile() throws IOException {
//     Create an image file name
        String imageFileName = "";
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        if (!imageTitle.getText().toString().equals("")){
            imageFileName = "JPEG_" + imageTitle.getText().toString() + "_";
        }
        else{
            imageFileName = "JPEG_" + timeStamp + "_";
        }
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
//                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("imageTitle", imageTitle.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        imageTitle.setText(savedInstanceState.getString("imageTitle"));
    }

    @Override
    public void onBackPressed() {
        Intent backToLoginIntent = new Intent(this, MainActivity.class);
        startActivity(backToLoginIntent);
    }
}
