package com.example.medconnectpro;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class CityChooserActivity extends AppCompatActivity implements OnItemClick {


    DrawerLayout drawerLayout;
    ImageView backBtn, navBtn;
    NavigationView navView;
    RecyclerView cityRecyclerView;
    private LinearLayoutManager linearLayoutManager;

    CityAdapter cityAdapter;

    ArrayList<CityModel> list;
    FirebaseFirestore db;

    String departmentName = "";

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_chooser);

        drawerLayout = findViewById(R.id.drawerLayout);
        backBtn = findViewById(R.id.backBtn);
        navBtn = findViewById(R.id.navBtn);
        navView = findViewById(R.id.navView);
        navView.bringToFront();


        departmentName = getIntent().getStringExtra("docDepartment");

        cityRecyclerView = findViewById(R.id.cityRecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        cityRecyclerView.setLayoutManager(layoutManager);

        list = new ArrayList<>();

        cityAdapter = new CityAdapter(getApplicationContext(),list , this);
        cityRecyclerView.setAdapter(cityAdapter);

        db = FirebaseFirestore.getInstance();


        getData(departmentName);

        navBtn.setOnClickListener(v-> {

            drawerLayout.open();

        });

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();

                if (id == R.id.HomeMenu){
                    Intent intent = new Intent(getApplicationContext(),HomeScreen.class);
                    startActivity(intent);
                    finishAffinity();
                }

                return false;
            }
        });
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

                        }

                        Collections.sort(list, new Comparator<CityModel>() {
                            @Override
                            public int compare(CityModel a, CityModel b) {
                                // Compare the names alphabetically
                                return a.getName().compareTo(b.getName());
                            }
                        });

                        cityAdapter.notifyDataSetChanged();

                    }
                });

    }

    @Override
    public <T> void onClick(T model) {


        if (model instanceof CityModel) {
            CityModel cityModel = (CityModel) model;

            Intent intent = new Intent(getApplicationContext(), DoctorChooserActivity.class);
            intent.putExtra("docDepartment", departmentName);
            intent.putExtra("docCity", cityModel.getName());


            startActivity(intent);
        }

    }

    @Override
    public void onClickDelete(String s) {

    }
}