package com.example.medconnectpro;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DateChooserActivity extends AppCompatActivity implements OnItemClick {


    TextView nobookTV;
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

    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_chooser);

        drawerLayout = findViewById(R.id.drawerLayout);
        backBtn = findViewById(R.id.backBtn);
        navBtn = findViewById(R.id.navBtn);
        navView = findViewById(R.id.navView);
        navView.bringToFront();

        nobookTV = findViewById(R.id.nobookTV);
        bookAppointmentNow = findViewById(R.id.bookAppointmentBtn);
        datesRecyclerView = findViewById(R.id.datesRecyclerView);
        searchView = findViewById(R.id.dateSearchView);
        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(Color.BLACK);
        searchEditText.setHintTextColor(Color.LTGRAY);

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

        UserDataManager userDataManager = UserDataManager.getInstance();

        if(userDataManager.isDoctor()){
            bookAppointmentNow.setVisibility(View.GONE);
            MenuItem historyItem = navView.getMenu().findItem(R.id.HistoryMenu);
            historyItem.setVisible(false);
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                filterList(newText);
                return false;
            }
        });

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

        backBtn.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();

                if (id == R.id.HomeMenu){
                    UserDataManager userDataManager = UserDataManager.getInstance();

                    if(userDataManager.isDoctor()){

                        Log.d("NAVOPER", "DOCTOR");

                        Intent intent = new Intent(getApplicationContext(),DateChooserActivity.class);
                        intent.putExtra("docDepartment", userDataManager.getDepartment());
                        intent.putExtra("docCity", userDataManager.getCity());
                        intent.putExtra("docEmail", userDataManager.getEmail());

                        startActivity(intent);
                        finishAffinity();

                    } else{

                        Log.d("NAVOPER", "PATIENT");

                        Intent intent = new Intent(getApplicationContext(),HomeScreen.class);
                        startActivity(intent);
                        finishAffinity();
                    }
                }
                else if (id == R.id.ProfileMenu){
                    Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                    startActivity(intent);
                }
                else if (id == R.id.HistoryMenu){
                    Intent intent = new Intent(getApplicationContext(), HistoryActivity.class);
                    startActivity(intent);
                }
                else if (id == R.id.AboutMenu){
                    Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
                    startActivity(intent);
                }
                else if (id == R.id.LogoutMenu){
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                    finishAffinity();
                }

                return false;
            }
        });

    }

    public void filterList(String newText){


        ArrayList<DateModel> tempList = new ArrayList<DateModel>();

        for(DateModel model : list){
            if(model.getDate().toLowerCase().replaceAll("_", "/").contains(newText.toLowerCase())){
                tempList.add(model);
            }
        }

        dateAdapter.filteredList(tempList);
        datesRecyclerView.setAdapter(dateAdapter);

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

                        if(list.isEmpty()){
                            nobookTV.setVisibility(View.VISIBLE);
                        }else {
                            nobookTV.setVisibility(View.GONE);
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



        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Delete entry");
        alert.setMessage("Are you sure you want to close the date?");
        alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // continue with delete
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
        });
        alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // close dialog
                dialog.cancel();
            }
        });
        alert.show();




    }
}