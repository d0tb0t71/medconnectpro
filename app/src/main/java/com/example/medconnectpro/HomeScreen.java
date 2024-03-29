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

public class HomeScreen extends AppCompatActivity implements OnItemClick{

    RecyclerView departmentRecyclerView;
    private LinearLayoutManager linearLayoutManager;

    DepartmentAdapter departmentAdapter;

    ArrayList<DepartmentModel> list;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        departmentRecyclerView = findViewById(R.id.departmentRecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        departmentRecyclerView.setLayoutManager(layoutManager);

        list = new ArrayList<>();

        departmentAdapter = new DepartmentAdapter(getApplicationContext(),list, this);
        departmentRecyclerView.setAdapter(departmentAdapter);

        db = FirebaseFirestore.getInstance();


        getData();


    }

    private void getData() {

        db.collection("department")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {
                            Toast.makeText(getApplicationContext(), "Error " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        for (DocumentChange dc : value.getDocumentChanges()) {

                            if (dc.getType() == DocumentChange.Type.ADDED) {

                                DepartmentModel depModel = dc.getDocument().toObject(DepartmentModel.class);

                                list.add(depModel);

                            }

                            departmentAdapter.notifyDataSetChanged();

                        }

                    }
                });

    }

    @Override
    public void onClick(String depName) {

        Intent intent = new Intent(getApplicationContext(),CityChooserActivity.class);
        intent.putExtra("department", depName);
        startActivity(intent);

    }
}