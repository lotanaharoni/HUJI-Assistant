package com.example.huji_assistant.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.example.huji_assistant.HujiAssistentApplication;
import com.example.huji_assistant.LocalDataBase;
import com.example.huji_assistant.R;
import com.example.huji_assistant.StudentInfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.zxing.Result;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ScanQrActivity extends AppCompatActivity {
    private CodeScanner mCodeScanner;
    LocalDataBase db;
    private final int COURSE = 0;
    private final int YEAR = 1;
    private final int DAY = 2;
    FirebaseFirestore firestore;
    FirebaseFirestoreSettings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);
        CodeScannerView scannerView = findViewById(R.id.scanner_view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        mCodeScanner = new CodeScanner(this, scannerView);
        db = HujiAssistentApplication.getInstance().getDataBase();
        verifyPermissions();
        mCodeScanner.startPreview();
        firestore = FirebaseFirestore.getInstance();
        settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firestore.setFirestoreSettings(settings);

        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        String[] parts = result.getText().split("-");
                        // parts = "67625-2021-15.09"
//                        if (parts.length != 3){
//                            return;
//                        }
                        String year =  new SimpleDateFormat("yyyy").format(new Date());
                        String day = new SimpleDateFormat("dd.MM").format(new Date());
                        String course = result.getText();
                        StudentInfo user = db.getCurrentUser();
                        Map<String, Object> userScan = new HashMap<>();
                        userScan.put(user.getEmail(), user);
                        Map<String, Object> dummyValue = new HashMap<>();
                        dummyValue.put("dummy", "dummy");
                        firestore.collection("attendance").document(course).set(dummyValue);
                        firestore.collection("attendance").document(course).collection(year).document(day).set(dummyValue);
                        firestore.collection("attendance").document(course).collection(year).document(day).collection("dummy")
//                        firestore.collection(parts[COURSE]).document(parts[YEAR]).collection(parts[DAY])
                                .add(userScan)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Toast.makeText(ScanQrActivity.this, R.string.scan_Successfully_message, Toast.LENGTH_LONG).show();
                                        mCodeScanner.stopPreview();
                                        onBackPressed();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(ScanQrActivity.this, R.string.scan_failed_message, Toast.LENGTH_LONG).show();                                            //todo: don't allow to continue
                                    }
                                });
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
    }


    private void verifyPermissions(){
        Log.d("chek", "verify permission");
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[0]) != PackageManager.PERMISSION_GRANTED |
                ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[1]) != PackageManager.PERMISSION_GRANTED |
                ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[2]) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(ScanQrActivity.this, permissions, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        verifyPermissions();
    }

    @Override
    public void onBackPressed() {
        if (mCodeScanner.isPreviewActive()){
            mCodeScanner.stopPreview();
        }
        else {
            startActivity(new Intent(this, MainScreenActivity.class));
            finish();
//            super.onBackPressed();
        }
    }

    public boolean isCodeScannerActive(){
        return mCodeScanner.isPreviewActive();
    }
}
