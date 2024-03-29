package com.example.medconnectpro;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CityChooserActivity extends AppCompatActivity {

    RecyclerView cityRecyclerView;
    private LinearLayoutManager linearLayoutManager;

    CityAdapter cityAdapter;

    ArrayList<CityModel> list;
    FirebaseFirestore db;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_chooser);

        String departmentName = getIntent().getStringExtra("department");

        cityRecyclerView = findViewById(R.id.cityRecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        cityRecyclerView.setLayoutManager(layoutManager);

        list = new ArrayList<>();

        cityAdapter = new CityAdapter(getApplicationContext(),list);
        cityRecyclerView.setAdapter(cityAdapter);

        db = FirebaseFirestore.getInstance();


        getData(departmentName);
    }

    private void getData(String departmentName) {

        db.collection("department").document(departmentName).collection("city")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {
                            Toast.makeText(getApplicationContext(), "Error " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        for (DocumentChange dc : value.getDocumentChanges()) {

                            if (dc.getType() == DocumentChange.Type.ADDED) {

                                CityModel cityModel = dc.getDocument().toObject(CityModel.class);

                                list.add(cityModel);

                            }

                            cityAdapter.notifyDataSetChanged();

                        }

                    }
                });

    }
}