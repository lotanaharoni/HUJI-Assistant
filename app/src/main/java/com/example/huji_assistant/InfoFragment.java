package com.example.huji_assistant;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.huji_assistant.databinding.FragmentInfoBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;


public class InfoFragment extends Fragment {

    public InfoFragment(){
        super(R.layout.personalinfofragment);
    }
    FirebaseFirestore firebaseInstancedb = FirebaseFirestore.getInstance();
    public interface itemSelectedDropDownFaculty{
        public void onFacultyItemSelected();
    }
    public InfoFragment.itemSelectedDropDownFaculty itemSelectedDropDownFacultyListener = null;
    HashMap<Integer, String> faculties_position_map = new HashMap<>();

    FragmentInfoBinding binding;
    AutoCompleteTextView dropdownHugim;
    AutoCompleteTextView dropdownFaculty;
    AutoCompleteTextView dropdownMaslulim;
    AutoCompleteTextView dropdownYear;
    private ViewModelApp viewModelApp;
    String facultyId;
    String chugId;
    String maslulId;
    public LocalDataBase dataBase;
    public interface continueButtonClickListener{
        public void continueBtnClicked();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // return super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentInfoBinding.inflate(inflater, container, false);
        faculties_position_map.put(0, "01");
        faculties_position_map.put(1, "02");
        faculties_position_map.put(2, "03");
        faculties_position_map.put(3, "04");
        faculties_position_map.put(4, "05");
        faculties_position_map.put(5, "06");
        faculties_position_map.put(6, "07");
        faculties_position_map.put(7, "08");
        faculties_position_map.put(8, "09");
        faculties_position_map.put(9, "11");
        faculties_position_map.put(10, "12");
        faculties_position_map.put(11, "16");
        faculties_position_map.put(12, "30");

        // Get values recourse
        String[] facultyArray = getResources().getStringArray(R.array.faculty);
        String[] computerScienceHugimArray = getResources().getStringArray(R.array.school_science_faculty);
        String[] ruachHugimArray = getResources().getStringArray(R.array.ruach_faculty);

        // todo get faculties from firestore delete string array

        // Get items to show in drop down faculty
        ArrayAdapter arrayAdapter = new ArrayAdapter(requireContext(), R.layout.dropdownfacultyitem, facultyArray);
        arrayAdapter.getFilter().filter("");
        binding.autoCompleteTextViewFaculty.setAdapter(arrayAdapter);

        //binding.autoCompleteTextViewFaculty.setAdapter(new ArrayAdapter(requireContext(), R.layout.dropdownfacultyitem, facultyArray));

        // binding.autoCompleteTextViewChug.setAdapter(new ArrayAdapter(requireContext(), R.layout.dropdownhugimitem, computerScienceHugimArray));

        return binding.getRoot();
    }

    public InfoFragment.continueButtonClickListener continueListener = null;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // db
        if (dataBase == null){
            dataBase = HujiAssistentApplication.getInstance().getDataBase();
        }
        String[] facultyArray = getResources().getStringArray(R.array.faculty);
        // Get items to show in drop down faculty

        ArrayAdapter arrayAdapter = new ArrayAdapter(requireContext(), R.layout.dropdownfacultyitem, facultyArray);
        arrayAdapter.getFilter().filter("");
        binding.autoCompleteTextViewFaculty.setAdapter(arrayAdapter);

        AutoCompleteTextView autoCompleteTextViewFaculty = (AutoCompleteTextView) getView().findViewById(R.id.autoCompleteTextViewFaculty);
        AutoCompleteTextView autoCompleteTextViewChug = (AutoCompleteTextView) getView().findViewById(R.id.autoCompleteTextViewChug);
        AutoCompleteTextView autoCompleteTextViewDegree = (AutoCompleteTextView) getView().findViewById(R.id.autoCompleteTextViewDegree);
        AutoCompleteTextView autoCompleteTextViewMaslul = (AutoCompleteTextView) getView().findViewById(R.id.autoCompleteTextViewMaslul);
        AutoCompleteTextView autoCompleteTextViewYear = (AutoCompleteTextView) getView().findViewById(R.id.autoCompleteTextViewYear);
        ProgressBar progressBar = getView().findViewById(R.id.progressBar);

        autoCompleteTextViewChug.setEnabled(false);
        autoCompleteTextViewDegree.setEnabled(false);
        autoCompleteTextViewMaslul.setEnabled(false);
        autoCompleteTextViewYear.setEnabled(false);
        progressBar.setVisibility(View.INVISIBLE);

        viewModelApp = new ViewModelProvider(requireActivity()).get(ViewModelApp.class);
        view.findViewById(R.id.continuePersBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (continueListener != null) {
                    StudentInfo currentStudent = viewModelApp.get().getValue();

                    //  currentStudent.setName(nameEditText.getText().toString());
                    // currentStudent.setYear(yearEditText.getText().toString());
                    //  currentStudent.setDegreeName(degreeNameEditText.getText().toString());
                    // todo validation check for inserted values
                    // todo bar

                    continueListener.continueBtnClicked();
                }
            }
        });

        dropdownHugim = view.findViewById(R.id.autoCompleteTextViewChug);
        dropdownFaculty = view.findViewById(R.id.autoCompleteTextViewFaculty);
        dropdownMaslulim = view.findViewById(R.id.autoCompleteTextViewMaslul);
        dropdownYear = view.findViewById(R.id.autoCompleteTextViewYear);


        dropdownFaculty.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String)parent.getItemAtPosition(position);
              //  System.out.println("selection "+ selection);
                //System.out.println("position "+ position);
                //dropdownHugim = view.findViewById(R.id.autoCompleteTextViewChug);
                ArrayList<String> s = new ArrayList<>();
                autoCompleteTextViewChug.setAdapter(new ArrayAdapter(requireContext(), R.layout.dropdownhujiitem, s));
                progressBar.setVisibility(View.VISIBLE);


               // autoCompleteTextViewChug.setEnabled(true);
                facultyId = faculties_position_map.get(position);
                //System.out.println("faculty id " + facultyId);

                Task<QuerySnapshot> document = firebaseInstancedb.collection("faculties").document(facultyId)
                        .collection("chugimInFaculty").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                progressBar.setVisibility(View.INVISIBLE);

                                autoCompleteTextViewChug.setEnabled(true);
                                List<DocumentSnapshot> documents = task.getResult().getDocuments();

                                ArrayList<String> chugimInFaculty = new ArrayList<>();

                                for (DocumentSnapshot document1 : documents){
                                    // retrieve for each chug id it's name
                                    String docId  = document1.getId().toString();
                                    Chug chug = document1.toObject(Chug.class);
                                    // chugId = chug.getId();
                                    //  System.out.println("chug "+ chug.toStringP());
                                    String chugTitle = chug.getTitle();
                                    chugimInFaculty.add(chugTitle);
                                }

                                binding.autoCompleteTextViewChug.setAdapter(new ArrayAdapter(requireContext(), R.layout.dropdownhujiitem, chugimInFaculty));
                            }
                        });
                /**

                // Gets chugim list from firebase
                Task<QuerySnapshot> document = firebaseInstancedb.collection("faculties").whereEqualTo("title", selection).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                autoCompleteTextViewFaculty.setEnabled(true);
                                List<DocumentSnapshot> documents = task.getResult().getDocuments();

                                for (DocumentSnapshot document1 : documents){
                                    Faculty faculty = document1.toObject(Faculty.class);
                                    facultyId = faculty.getFacultyId();
                                    System.out.println("chosen faculty: " + facultyId);

                                    Task<QuerySnapshot> document = firebaseInstancedb.collection("faculties").document(facultyId)
                                            .collection("chugimInFaculty").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    autoCompleteTextViewChug.setEnabled(true);
                                                    List<DocumentSnapshot> documents = task.getResult().getDocuments();

                                                    ArrayList<String> chugimInFaculty = new ArrayList<>();

                                                    for (DocumentSnapshot document1 : documents){
                                                        // retrieve for each chug id it's name
                                                        String docId  = document1.getId().toString();
                                                        Chug chug = document1.toObject(Chug.class);
                                                        // chugId = chug.getId();
                                                        //  System.out.println("chug "+ chug.toStringP());
                                                        String chugTitle = chug.getTitle();
                                                        chugimInFaculty.add(chugTitle);
                                                    }

                                                    binding.autoCompleteTextViewChug.setAdapter(new ArrayAdapter(requireContext(), R.layout.dropdownhujiitem, chugimInFaculty));
                                                }
                                            });


                                }
                            }
                        });*/
                        /**
                        .document(facultyId).collection("chugimInFaculty")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                autoCompleteTextViewChug.setEnabled(true);
                                List<DocumentSnapshot> documents = task.getResult().getDocuments();

                                 ArrayList<String> chugimInFaculty = new ArrayList<>();

                                for (DocumentSnapshot document1 : documents){
                                    // retrieve for each chug id it's name
                                    String docId  = document1.getId().toString();
                                    Chug chug = document1.toObject(Chug.class);
                                   // chugId = chug.getId();
                                  //  System.out.println("chug "+ chug.toStringP());
                                    String chugTitle = chug.getTitle();
                                    chugimInFaculty.add(chugTitle);
                                }

                                binding.autoCompleteTextViewChug.setAdapter(new ArrayAdapter(requireContext(), R.layout.dropdownhujiitem, chugimInFaculty));
                            }
                        });
                         */
            }
        });

        dropdownHugim.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                progressBar.setVisibility(View.VISIBLE);
                String selection = (String)parent.getItemAtPosition(position);
                System.out.println("selection "+ selection);
                System.out.println("position "+ position);
                dropdownMaslulim = view.findViewById(R.id.autoCompleteTextViewMaslul);
                //autoCompleteTextViewMaslul.setEnabled(true);
                ArrayList<String> s = new ArrayList<>();
                autoCompleteTextViewMaslul.setAdapter(new ArrayAdapter(requireContext(), R.layout.dropdownmaslulitem, s));


                // Get the chosen document
                Task<QuerySnapshot> document = firebaseInstancedb.collection("faculties").
                        document(facultyId).collection("chugimInFaculty")
                        .whereEqualTo("title", selection)
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                progressBar.setVisibility(View.INVISIBLE);
                                autoCompleteTextViewMaslul.setEnabled(true);
                                List<DocumentSnapshot> documents = task.getResult().getDocuments();

                                for (DocumentSnapshot document1 : documents){
                                    // retrieve for each chug id it's name
                                    Chug chug = document1.toObject(Chug.class);
                                    chugId = chug.getId();
                                    System.out.println("chosen chug: " + chugId);


                                    Task<QuerySnapshot> querySnapshotTask = firebaseInstancedb.collection("faculties").document(facultyId)
                                            .collection("chugimInFaculty").document(document1.getId().toString())
                                            .collection("maslulimInChug").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    List<DocumentSnapshot> documents1 = task.getResult().getDocuments();

                                                    ArrayList<String> maslulimInFaculty = new ArrayList<>();

                                                    for (DocumentSnapshot document2 : documents1){
                                                       // String docId  = document1.getId().toString();
                                                        Maslul maslul = document2.toObject(Maslul.class);
                                                        System.out.println("maslul "+ maslul.toStringP());
                                                        String maslulTitle = maslul.getTitle();
                                                        maslulimInFaculty.add(maslulTitle);
                                                    }
                                                    binding.autoCompleteTextViewMaslul.setAdapter(new ArrayAdapter(requireContext(), R.layout.dropdownmaslulitem, maslulimInFaculty));
                                                }
                                            });
                                }
                            }
                        });
            }
        });


        dropdownMaslulim.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String)parent.getItemAtPosition(position);
                System.out.println("selection "+ selection);
                System.out.println("position "+ position);

                // Save id's

            }
        });

        /**
        dropdownHugim.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String)parent.getItemAtPosition(position);
                long itemIdAtPosition = parent.getItemIdAtPosition(position);
                System.out.println("item id "+ itemIdAtPosition);
                Object selectedItem = parent.getSelectedItem();
                System.out.println("selectedItem "+ selectedItem);

                int ruach_faculty = getResources().getIdentifier(selection, "ruach_faculty", "");
                System.out.println("ruach_faculty "+ ruach_faculty);
                String[] ruachHugimArray = getResources().getStringArray(R.array.ruach_faculty);
                System.out.println("length" + ruachHugimArray.length);
                System.out.println("tostring" + Arrays.toString(ruachHugimArray));
                int i = Arrays.binarySearch(ruachHugimArray, selection);
                System.out.println("i" + i);
                List<String> list = Arrays.asList(ruachHugimArray);
                System.out.println("list" + list);
                int i1 = list.indexOf(selection);
                System.out.println("i1" + i1);


                String item = dataBase.ruach_faculty_values_names_map.get(selection);
                System.out.println("item" + item);


            }
        });
*/



    }
}
