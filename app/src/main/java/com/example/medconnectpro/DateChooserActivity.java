package com.example.medconnectpro;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DateChooserActivity extends AppCompatActivity implements OnItemClick {


    DrawerLayout drawerLayout;
    ImageView backBtn, navBtn;
    NavigationView navView;
    RecyclerView datesRecyclerView;
    private LinearLayoutManager linearLayoutManager;

    DateAdapter dateAdapter;

    ArrayList<DateModel> list;
    FirebaseFirestore db;
    Button bookAppointmentNow;

    String departmentName = "";
    String cityName = "";
    String doctorMail = "";
    String doctorName = "";
    String doctorMobile = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_chooser);

        drawerLayout = findViewById(R.id.drawerLayout);
        backBtn = findViewById(R.id.backBtn);
        navBtn = findViewById(R.id.navBtn);
        navView = findViewById(R.id.navView);
        navView.bringToFront();

        bookAppointmentNow = findViewById(R.id.bookAppointmentBtn);
        datesRecyclerView = findViewById(R.id.datesRecyclerView);

        departmentName = getIntent().getStringExtra("docDepartment");
        cityName = getIntent().getStringExtra("docCity");
        doctorMail = getIntent().getStringExtra("docEmail");
        doctorName = getIntent().getStringExtra("docName");
        doctorMobile = getIntent().getStringExtra("docMobile");

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
//        layoutManager.setReverseLayout(true);
//        layoutManager.setStackFromEnd(true);
        datesRecyclerView.setLayoutManager(layoutManager);

        list = new ArrayList<>();

        dateAdapter = new DateAdapter(getApplicationContext(),list,this);
        datesRecyclerView.setAdapter(dateAdapter);

        db = FirebaseFirestore.getInstance();



        bookAppointmentNow.setOnClickListener( v-> {

            Intent intent = new Intent(getApplicationContext(),BookAppointmentActivity.class);
            intent.putExtra("docDepartment", departmentName);
            intent.putExtra("docCity", cityName);
            intent.putExtra("docEmail", doctorMail);
            intent.putExtra("docName", doctorName);
            intent.putExtra("docMobile", doctorMobile);
            startActivity(intent);

        });

        getData(departmentName, cityName,doctorMail);

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

    private void getData(String departmentName, String cityName, String docMail) {

        db.collection("department").document(departmentName).collection("city").document(cityName).collection("doctor").document(docMail).collection("dates")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {
                            Toast.makeText(getApplicationContext(), "Error " + error.getMessage(), Toast.LENGTH_LONG).show();
                            return;
                        }

                        for (DocumentChange dc : value.getDocumentChanges()) {

                            if (dc.getType() == DocumentChange.Type.ADDED) {

                                DateModel dateModel = dc.getDocument().toObject(DateModel.class);

                                list.add(dateModel);

                            }

                            Collections.sort(list, new Comparator<DateModel>() {
                                @Override
                                public int compare(DateModel a, DateModel b) {
                                    // Split the date strings
                                    String[] dateA = a.getDate().split("_");
                                    String[] dateB = b.getDate().split("_");

                                    // Convert date strings to integers
                                    int dayA = Integer.parseInt(dateA[0]);
                                    int monthA = Integer.parseInt(dateA[1]);
                                    int yearA = Integer.parseInt(dateA[2]);
                                    int dayB = Integer.parseInt(dateB[0]);
                                    int monthB = Integer.parseInt(dateB[1]);
                                    int yearB = Integer.parseInt(dateB[2]);

                                    // Compare years
                                    if (yearA != yearB) {
                                        return yearA - yearB;
                                    } else if (monthA != monthB) { // Compare months if years are equal
                                        return monthA - monthB;
                                    } else { // Compare days if both years and months are equal
                                        return dayA - dayB;
                                    }
                                }
                            });

                            dateAdapter.notifyDataSetChanged();

                        }

                    }
                });

    }

    @Override
    public <T> void onClick(T model) {

        if (model instanceof DateModel) {
            DateModel dateModel = (DateModel) model;

            Intent intent = new Intent(getApplicationContext(),AppointmentListActivity.class);
            intent.putExtra("docDepartment", departmentName);
            intent.putExtra("docCity", cityName);
            intent.putExtra("docEmail", doctorMail);
            intent.putExtra("docDate", ((DateModel) model).getDate());

            startActivity(intent);

        }


    }

    @Override
    public void onClickDelete(String s) {

        UserDataManager userDataManager = UserDataManager.getInstance();

        db.collection("department").document(userDataManager.getDepartment()).collection("city").document(userDataManager.getCity()).collection("doctor").document(userDataManager.getEmail()).collection("dates").document(s).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                Toast.makeText(DateChooserActivity.this, "Date has been closed", Toast.LENGTH_SHORT).show();
                recreate();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(DateChooserActivity.this, "Unknown Error", Toast.LENGTH_SHORT).show();

            }
        });



    }
}