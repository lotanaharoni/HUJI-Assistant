package com.example.huji_assistant;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

public class CaptureImage2 extends AppCompatActivity {

    private Button uploadBtn, showAllBtn, button2;
    private ImageView imageView, cameraImageUpload, pdfImageUpload;
    private ProgressBar progressBar;
    private DatabaseReference root;
    private StorageReference reference;
    private Uri imageUri;
    EditText imageTitle;
    String currentPhotoPath;
    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int DOCUMENTS_REQUEST_CODE = 104;
    public static final int GALLERY_REQUEST_CODE = 105;
    private static final int GALLERY_TYPE = 0;
    private static final int CAMERA_TYPE = 1;
    private static final int PDF_TYPE = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture2);

        uploadBtn = findViewById(R.id.upload_btn);
        showAllBtn = findViewById(R.id.showall_btn);
        progressBar = findViewById(R.id.progressBar2);
        imageView = findViewById(R.id.uploadFromGallery);
        root = FirebaseDatabase.getInstance().getReference("Image");
        reference = FirebaseStorage.getInstance().getReference();
        progressBar.setVisibility(View.INVISIBLE);
        cameraImageUpload = findViewById(R.id.cameraImageUpload);
        pdfImageUpload = findViewById(R.id.pdfImageUpload);
        imageTitle = findViewById(R.id.imageTitle);
        imageTitle.setText("");
        button2 = findViewById(R.id.button2);

//        imageView.setImageURI(null); todo: reset the image
//        imageView.setImageResource(R.drawable.ic_baseline_add_photo_alternate_24);

        cameraImageUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askCameraPermissions();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                askCameraPermissions(); //todo
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
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

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri != null){
                    uploadToFirebase(imageUri, GALLERY_REQUEST_CODE, "");
                }else{
                    Toast.makeText(CaptureImage2.this, "Please select image", Toast.LENGTH_SHORT).show();
                }
            }
        });

        showAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CaptureImage2.this, ShowActivity.class));
            }
        });
    }

    private void uploadToFirebase(Uri uri, int source, String name) {
        StorageReference fileRef;
        int type;
        String fileName;
        if (source == GALLERY_REQUEST_CODE){
            fileRef = reference.child("Gallery_files/" + System.currentTimeMillis() + "." + getFileExtension(uri));
            fileName = System.currentTimeMillis() + "." + getFileExtension(uri);
            type = GALLERY_TYPE;
        }
        else if (source == CAMERA_REQUEST_CODE){
            fileRef = reference.child("Camera_images/" + name);
            type = CAMERA_TYPE;
            fileName = name;
        }
        else{
            fileRef = reference.child("Documents/" + name);
            type = PDF_TYPE;
            fileName = name;
        }
        StorageReference finalFileRef = fileRef;
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                finalFileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Model model = new Model(uri.toString(), fileName,type);
                        String modelId = root.push().getKey();
                        assert modelId != null;
                        progressBar.setVisibility(View.INVISIBLE);
                        root.child(modelId).setValue(model);
                        imageTitle.setText(""); //todo: add title
                        Toast.makeText(CaptureImage2.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                progressBar.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(CaptureImage2.this, "Failed to upload", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFileExtension(Uri mUri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
//            imageView.setImageURI(imageUri);
        }

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            File f = new File(currentPhotoPath);
//            cameraImageUpload.setImageURI(Uri.fromFile(f));

            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);

            uploadToFirebase(contentUri, CAMERA_REQUEST_CODE, f.getName());
        }

        if (requestCode == DOCUMENTS_REQUEST_CODE && resultCode == RESULT_OK){
            imageUri = data.getData();
            final String timestamp = "" + System.currentTimeMillis();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            final String messagePushID = timestamp + "." + "pdf";
            uploadToFirebase(imageUri, DOCUMENTS_REQUEST_CODE, messagePushID);
        }
    }

    private void askCameraPermissions() {
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        }else {
            dispatchTakePictureIntent();
        }
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
            }
        }
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
