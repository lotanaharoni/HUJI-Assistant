package com.example.huji_assistant;

import android.Manifest;
import android.content.pm.PackageManager;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.Result;

import java.util.HashMap;
import java.util.Map;

public class ScanQrActivity extends AppCompatActivity {
    private CodeScanner mCodeScanner;
    private final int COURSE = 0;
    private final int YEAR = 1;
    private final int DAY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);
        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        verifyPermissions();
        mCodeScanner.startPreview();

        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String[] parts = result.getText().split("-");
                        // parts = "67625-2021-15/09"
                        if (parts.length != 3){
                            return;
                        }
                        User tempUser = new User("Yosi", "123456789", "yosi@gmail.com", 1, 3);
                        // todo: Get current user
                        Map<String, Object> userScan = new HashMap<>();
                        userScan.put("Yosi-121121121", tempUser); //todo
                        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                        firestore.collection(parts[COURSE]).document(parts[YEAR]).collection(parts[DAY])
                                .add(userScan)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Toast.makeText(ScanQrActivity.this, "DocumentSnapshot added", Toast.LENGTH_LONG).show();
                                        mCodeScanner.stopPreview();
                                        onBackPressed();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(ScanQrActivity.this, "DocumentSnapshot failed", Toast.LENGTH_LONG).show();                                            //todo: don't allow to continue
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
            super.onBackPressed();
        }
    }

}
