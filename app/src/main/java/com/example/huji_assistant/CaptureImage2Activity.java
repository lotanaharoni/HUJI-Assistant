package com.example.huji_assistant;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class CaptureImage2Activity extends AppCompatActivity {

    private Button uploadBtn, showAllBtn, button2;
    private TextView pdfName;
    private ImageView imageView, cameraImageUpload, pdfImageUpload, imageShow;
    private int uploadChoose;
    private String fName, messagePushId;
    private ProgressBar progressBar;
    private DatabaseReference root;
    private StorageReference reference;
    private Uri imageUri, classContentUri;
    EditText imageTitle;
    String currentPhotoPath;
    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int DOCUMENTS_REQUEST_CODE = 104;
    public static final int GALLERY_REQUEST_CODE = 105;
    private static final int GALLERY_TYPE = 0;
    private static final int CAMERA_TYPE = 1;
    private static final int PDF_TYPE = 2;
    private ActivityResultLauncher<Intent> cameraUploadActivityResultLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture2);

        uploadBtn = findViewById(R.id.upload_btn);
        uploadBtn.setEnabled(false);
        showAllBtn = findViewById(R.id.showall_btn);
        progressBar = findViewById(R.id.progressBar2);
        imageView = findViewById(R.id.uploadFromGallery);
        root = FirebaseDatabase.getInstance().getReference("Image");
        reference = FirebaseStorage.getInstance().getReference();
        progressBar.setVisibility(View.INVISIBLE);
        cameraImageUpload = findViewById(R.id.cameraImageUpload);
        pdfName = findViewById(R.id.PDFName);
        pdfImageUpload = findViewById(R.id.pdfImageUpload);
        imageShow = findViewById(R.id.imageShow);
        imageTitle = findViewById(R.id.imageTitle);
        imageTitle.setText("");
        imageShow.setVisibility(View.INVISIBLE);
        button2 = findViewById(R.id.button2);
        uploadChoose = -1;
        fName = "";
        pdfName.setText("");
        pdfName.setVisibility(View.INVISIBLE);

        cameraImageUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askCameraPermissions();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // startActivity(new Intent(CaptureImage2.this, PDFMainActivity.class));
           // }

                startActivity(new Intent(CaptureImage2Activity.this, PDFMainActivity.class));            }

        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uploadChoose == GALLERY_TYPE){
                    uploadToFirebase(imageUri, GALLERY_REQUEST_CODE, imageTitle.getText().toString());
                }
                else if (uploadChoose == CAMERA_TYPE){
                    uploadToFirebase(classContentUri, CAMERA_REQUEST_CODE, fName);
                }
                else if (uploadChoose == PDF_TYPE){
                    uploadToFirebase(imageUri, DOCUMENTS_REQUEST_CODE, messagePushId);
                }
            }
        });

        showAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CaptureImage2Activity.this, ShowActivity.class));
            }
        });

        cameraUploadActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();
                            File f = new File(currentPhotoPath);
//                            cameraImageUpload.setImageURI(Uri.fromFile(f));

                            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            Uri contentUri = Uri.fromFile(f);
                            mediaScanIntent.setData(contentUri);
                            sendBroadcast(mediaScanIntent);
                            hideButtonsAfterChooseImage();
                            imageShow.setImageURI(contentUri);
                            uploadChoose = CAMERA_TYPE;
                            classContentUri = contentUri;
                            fName = f.getName();
                            uploadBtn.setEnabled(true);
                        }
                    }
                });

        ActivityResultLauncher<Intent> GalleryUploadActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();
                            imageUri = data.getData();
                            hideButtonsAfterChooseImage();
                            imageShow.setImageURI(imageUri);
                            uploadBtn.setEnabled(true);
                            uploadChoose = GALLERY_TYPE;
                        }
                    }
                });

        ActivityResultLauncher<Intent> pdfImageUploadActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();
                            assert data != null;
                            imageUri = data.getData();
                            final String timestamp = "" + System.currentTimeMillis();
                            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                            final String messagePushID = timestamp + "." + "pdf";
                            hideButtonsAfterChooseImage();
                            imageShow.setImageResource(R.drawable.ic_pdf_icon);
                            uploadChoose = PDF_TYPE;
                            messagePushId = messagePushID;
                            uploadBtn.setEnabled(true);
                            pdfName.setVisibility(View.VISIBLE);
                            pdfName.setText(messagePushID);
                        }
                    }
                });

        pdfImageUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                // We will be redirected to choose pdf
                galleryIntent.setType("application/pdf");
                pdfImageUploadActivityResultLauncher.launch(galleryIntent);
//                startActivityForResult(galleryIntent, DOCUMENTS_REQUEST_CODE);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
//                someActivityResultLauncher.launch(galleryIntent, GALLERY_REQUEST_CODE);
                GalleryUploadActivityResultLauncher.launch(galleryIntent);
//                startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
            }
        });
    }

    private void uploadToFirebase(Uri uri, int source, String name) {
        name = System.currentTimeMillis() + "_" + imageTitle.getText().toString();
        StorageReference fileRef;
        int type;
        String fileName;
        if (source == GALLERY_REQUEST_CODE){
            fileRef = reference.child("Gallery_files/" + name + "." + getFileExtension(uri));
            fileName = System.currentTimeMillis() + "." + getFileExtension(uri);
            type = GALLERY_TYPE;
        }
        else if (source == CAMERA_REQUEST_CODE){
            fileRef = reference.child("Camera_images/" + imageTitle.getText().toString() + "_" + fName);
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
                        imageTitle.setText("");
                        uploadBtn.setEnabled(false);
                        showButtonsAfterChooseImage();
                        Toast.makeText(CaptureImage2Activity.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(CaptureImage2Activity.this, "Failed to upload", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFileExtension(Uri mUri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null){
//            imageUri = data.getData();
//            hideButtonsAfterChooseImage();
//            imageShow.setImageURI(imageUri);
//            uploadBtn.setEnabled(true);
//            uploadChoose = GALLERY_TYPE;
////            imageView.setImageURI(imageUri);
//        }
//
//        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
//            File f = new File(currentPhotoPath);
////            cameraImageUpload.setImageURI(Uri.fromFile(f));
//
//            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//            Uri contentUri = Uri.fromFile(f);
//            mediaScanIntent.setData(contentUri);
//            this.sendBroadcast(mediaScanIntent);
//            hideButtonsAfterChooseImage();
//            imageShow.setImageURI(contentUri);
//            uploadChoose = CAMERA_TYPE;
//            classContentUri = contentUri;
//            fName = f.getName();
//            uploadBtn.setEnabled(true);
//        }
//
//        if (requestCode == DOCUMENTS_REQUEST_CODE && resultCode == RESULT_OK){
//            imageUri = data.getData();
//            final String timestamp = "" + System.currentTimeMillis();
//            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
//            final String messagePushID = timestamp + "." + "pdf";
//            hideButtonsAfterChooseImage();
//            imageShow.setImageResource(R.drawable.ic_pdf_icon);
//            uploadChoose = PDF_TYPE;
//            messagePushId = messagePushID;
//            uploadBtn.setEnabled(true);
//            pdfName.setVisibility(View.VISIBLE);
//            pdfName.setText(messagePushID);
//        }
//    }

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
                cameraUploadActivityResultLauncher.launch(takePictureIntent);
//                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    private File createImageFile() throws IOException {
//     Create an image file name
        String imageFileName = "";
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        if (!imageTitle.getText().toString().equals("")){
            imageFileName = imageTitle.getText().toString() + "_";
        }
        else{
            imageFileName = timeStamp + "_";
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
        outState.putParcelable("imageUri", imageUri);
        outState.putInt("progressBarVisibility", progressBar.getVisibility());
        outState.putInt("cameraUploadVisibility", cameraImageUpload.getVisibility());
        outState.putInt("pdfImageVisibility", pdfImageUpload.getVisibility());
        outState.putInt("imageShowVisibility", imageShow.getVisibility());
        outState.putString("pdfNameText", pdfName.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        imageTitle.setText(savedInstanceState.getString("imageTitle"));
        imageUri = savedInstanceState.getParcelable("imageUri");
        imageShow.setImageURI(imageUri);
        progressBar.setVisibility(savedInstanceState.getInt("progressBarVisibility"));
        cameraImageUpload.setVisibility(savedInstanceState.getInt("cameraUploadVisibility"));
        pdfImageUpload.setVisibility(savedInstanceState.getInt("pdfImageVisibility"));
        imageShow.setVisibility(savedInstanceState.getInt("imageShowVisibility"));
        pdfName.setText(savedInstanceState.getString("pdfNameText"));
    }

    private void hideButtonsAfterChooseImage(){
        imageView.setVisibility(View.INVISIBLE);
        cameraImageUpload.setVisibility(View.INVISIBLE);
        pdfImageUpload.setVisibility(View.INVISIBLE);
        imageShow.setVisibility(View.VISIBLE);
    }

    private void showButtonsAfterChooseImage(){
        imageView.setVisibility(View.VISIBLE);
        cameraImageUpload.setVisibility(View.VISIBLE);
        pdfImageUpload.setVisibility(View.VISIBLE);
        imageShow.setVisibility(View.INVISIBLE);
        imageTitle.setText("");
        pdfName.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (imageShow.getVisibility() == View.VISIBLE){
            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE: {
                        showButtonsAfterChooseImage();
                        break;
                    }
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure to reset the image?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }
        else{
            super.onBackPressed();
        }
    }
}
