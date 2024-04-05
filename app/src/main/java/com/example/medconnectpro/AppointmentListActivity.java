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
import android.widget.Button;
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

public class AppointmentListActivity extends AppCompatActivity implements OnItemClick{


    DrawerLayout drawerLayout;
    ImageView backBtn, navBtn;
    NavigationView navView;
    RecyclerView appointmentRecyclerView;
    private LinearLayoutManager linearLayoutManager;

    AppointmentAdapter appointmentAdapter;

    ArrayList<AppointmentModel> list;
    FirebaseFirestore db;
    Button bookAppointmentNow;

    String departmentName = "";
    String cityName = "";
    String doctorMail = "";
    String docDate = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_list);

        drawerLayout = findViewById(R.id.drawerLayout);
        backBtn = findViewById(R.id.backBtn);
        navBtn = findViewById(R.id.navBtn);
        navView = findViewById(R.id.navView);
        navView.bringToFront();

        appointmentRecyclerView = findViewById(R.id.appointmentRecyclerView);

        departmentName = getIntent().getStringExtra("docDepartment");
        cityName = getIntent().getStringExtra("docCity");
        doctorMail = getIntent().getStringExtra("docEmail");
        docDate = getIntent().getStringExtra("docDate");

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        //layoutManager.setReverseLayout(true);
        //layoutManager.setStackFromEnd(true);
        appointmentRecyclerView.setLayoutManager(layoutManager);

        list = new ArrayList<>();

        appointmentAdapter = new AppointmentAdapter(getApplicationContext(),list,this);
        appointmentRecyclerView.setAdapter(appointmentAdapter);

        db = FirebaseFirestore.getInstance();

        getData(departmentName, cityName, doctorMail, docDate);

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

    private void getData(String departmentName, String cityName, String docMail, String docDate) {

        db.collection("department").document(departmentName).collection("city").document(cityName).collection("doctor").document(docMail).collection("dates").document(docDate).collection("appointments")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {
                            Toast.makeText(getApplicationContext(), "Error " + error.getMessage(), Toast.LENGTH_LONG).show();
                            return;
                        }

                        for (DocumentChange dc : value.getDocumentChanges()) {

                            if (dc.getType() == DocumentChange.Type.ADDED) {

                                AppointmentModel appointmentModel = dc.getDocument().toObject(AppointmentModel.class);

                                list.add(appointmentModel);

                            }



                        }

                        Collections.sort(list, new Comparator<AppointmentModel>() {
                            @Override
                            public int compare(AppointmentModel a, AppointmentModel b) {
                                String[] timeA = a.getTime().split("_");
                                String[] timeB = b.getTime().split("_");

                                int hourA = Integer.parseInt(timeA[0]);
                                int minuteA = Integer.parseInt(timeA[1]);
                                int hourB = Integer.parseInt(timeB[0]);
                                int minuteB = Integer.parseInt(timeB[1]);

                                if (hourA != hourB) {
                                    return hourA - hourB;
                                } else {
                                    return minuteA - minuteB;
                                }
                            }
                        });

                        appointmentAdapter.notifyDataSetChanged();

                    }
                });

    }

    @Override
    public <T> void onClick(T model) {

    }
}