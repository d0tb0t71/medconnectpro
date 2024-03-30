package com.example.medconnectpro;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class DoctorChooserActivity extends AppCompatActivity implements OnItemClick {


    RecyclerView doctorRecyclerView;
    private LinearLayoutManager linearLayoutManager;

    DoctorAdapter doctorAdapter;

    ArrayList<UserModel> list;
    FirebaseFirestore db;

    String departmentName = "";
    String cityName = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_chooser);

         departmentName = getIntent().getStringExtra("docDepartment");
         cityName = getIntent().getStringExtra("docCity");

        doctorRecyclerView = findViewById(R.id.doctorRecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        doctorRecyclerView.setLayoutManager(layoutManager);

        list = new ArrayList<>();

        doctorAdapter = new DoctorAdapter(getApplicationContext(),list,this);
        doctorRecyclerView.setAdapter(doctorAdapter);

        db = FirebaseFirestore.getInstance();


        getData(departmentName, cityName);
        
        
    }

    private void getData(String departmentName, String cityName) {

        db.collection("department").document(departmentName).collection("city").document(cityName).collection("doctor")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {
                            Toast.makeText(getApplicationContext(), "Error " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        for (DocumentChange dc : value.getDocumentChanges()) {

                            if (dc.getType() == DocumentChange.Type.ADDED) {

                                UserModel userModel = dc.getDocument().toObject(UserModel.class);

                                list.add(userModel);

                            }

                            doctorAdapter.notifyDataSetChanged();

                        }

                    }
                });

    }

    @Override
    public void onClick(String value) {

        Intent intent = new Intent(getApplicationContext(),DateChooserActivity.class);
        intent.putExtra("docDepartment", departmentName);
        intent.putExtra("docCity", cityName);
        intent.putExtra("docEmail", value);
        startActivity(intent);


    }
}