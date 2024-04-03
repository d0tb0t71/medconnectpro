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

public class DoctorChooserActivity extends AppCompatActivity implements OnItemClick {


    DrawerLayout drawerLayout;
    ImageView backBtn, navBtn;
    NavigationView navView;
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

        drawerLayout = findViewById(R.id.drawerLayout);
        backBtn = findViewById(R.id.backBtn);
        navBtn = findViewById(R.id.navBtn);
        navView = findViewById(R.id.navView);
        navView.bringToFront();

         departmentName = getIntent().getStringExtra("docDepartment");
         cityName = getIntent().getStringExtra("docCity");

        doctorRecyclerView = findViewById(R.id.doctorRecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
//        layoutManager.setReverseLayout(true);
//        layoutManager.setStackFromEnd(true);
        doctorRecyclerView.setLayoutManager(layoutManager);

        list = new ArrayList<>();

        doctorAdapter = new DoctorAdapter(getApplicationContext(),list,this);
        doctorRecyclerView.setAdapter(doctorAdapter);

        db = FirebaseFirestore.getInstance();


        getData(departmentName, cityName);


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

                        }

                        Collections.sort(list, new Comparator<UserModel>() {
                            @Override
                            public int compare(UserModel a, UserModel b) {
                                // Compare the names alphabetically
                                return a.getFullname().compareTo(b.getFullname());
                            }
                        });

                        doctorAdapter.notifyDataSetChanged();

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