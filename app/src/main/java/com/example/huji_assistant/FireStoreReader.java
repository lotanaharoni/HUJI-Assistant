package com.example.huji_assistant;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.huji_assistant.HujiAssistentApplication;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FireStoreReader {
//    private FirebaseFirestore db;

    public FireStoreReader() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

//        this.db = HujiAssistentApplication.getInstance().getDataBase();  // -------------------------------------------- Change this one if needed ----------------------------
        ArrayList<ArrayList<String>> dictionary = new ArrayList<>();


        firestore.collection("faculties").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Log.e("TAG", "onSuccess: faculties - first onSuccess");
                List<DocumentSnapshot> faculties = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot faculty : faculties) {
//                    Log.e("TAG", "faculty: " + faculty.get("facultyId"));
                    if (faculty.contains("facultyId")) {

                        String currFaculty = faculty.get("facultyId").toString();


                        firestore.collection("faculties").document(faculty.get("facultyId").toString()).collection("chugimInFaculty").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                Log.e("TAG", "onSuccess: chugim - second onSuccess");
                                List<DocumentSnapshot> chugim = queryDocumentSnapshots.getDocuments();
                                for (DocumentSnapshot chug : chugim) {

                                    ArrayList<String> facultyList = new ArrayList<>(); // list of all the chug's in a specific faculty
                                    facultyList.add(currFaculty);

//                                Log.e("TAG", "chug: " + chug.get("chugId").toString());
                                    if (chug.contains("chugId"))
                                        firestore.collection("faculties").document(currFaculty).collection("chugimInFaculty").document(chug.get("chugId").toString()).collection(" maslulimInChug").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                Log.e("TAG", "onSuccess: maslulim - third onSuccess");
                                                List<DocumentSnapshot> maslulim = queryDocumentSnapshots.getDocuments();
                                                for (DocumentSnapshot maslul : maslulim) {
//                                            Log.e("TAG", "onSuccess: " + maslul.get("id"));
                                                    if (maslul.contains("id"))
                                                        facultyList.add(maslul.get("id").toString());
                                                }
                                                dictionary.add(facultyList);
                                                Log.e("TAG", "FireStoreReader: " + dictionary);

                                            }

                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e("TAG", "onFailure: third onFailure");
                                            }
                                        });
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("TAG", "onFailure: second onFailure");
                            }
                        });
                    }


                }

            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("TAG", "onFailure: first onFailure");
            }
        });

    }
}

