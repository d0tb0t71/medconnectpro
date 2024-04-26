package com.example.medconnectpro;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.UserManager;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

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

public class HomeScreen extends AppCompatActivity implements OnItemClick {


    DrawerLayout drawerLayout;
    ImageView backBtn, navBtn;
    NavigationView navView;
    RecyclerView departmentRecyclerView;

    private LinearLayoutManager linearLayoutManager;
    DepartmentAdapter departmentAdapter;

    ArrayList<DepartmentModel> list;
    FirebaseFirestore db;

    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        drawerLayout = findViewById(R.id.drawerLayout);
        backBtn = findViewById(R.id.backBtn);
        navBtn = findViewById(R.id.navBtn);
        navView = findViewById(R.id.navView);
        navView.bringToFront();


        searchView = findViewById(R.id.depSearchView);
        departmentRecyclerView = findViewById(R.id.departmentRecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
//        layoutManager.setReverseLayout(true);
//        layoutManager.setStackFromEnd(true);
        departmentRecyclerView.setLayoutManager(layoutManager);

        list = new ArrayList<>();

        departmentAdapter = new DepartmentAdapter(getApplicationContext(),list, this);
        departmentRecyclerView.setAdapter(departmentAdapter);

        db = FirebaseFirestore.getInstance();


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

        getData();

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


        ArrayList<DepartmentModel> tempList = new ArrayList<DepartmentModel>();

        for(DepartmentModel model : list){
            if(model.getName().toLowerCase().contains(newText.toLowerCase())){
                tempList.add(model);
            }
        }

            departmentAdapter.filteredList(tempList);
            departmentRecyclerView.setAdapter(departmentAdapter);

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

                        }

                        Collections.sort(list, new Comparator<DepartmentModel>() {
                            @Override
                            public int compare(DepartmentModel a, DepartmentModel b) {
                                return a.getName().compareTo(b.getName());
                            }
                        });

                        departmentAdapter.notifyDataSetChanged();

                    }
                });

    }

    @Override
    public <T> void onClick(T model) {
        if (model instanceof DepartmentModel) {
            DepartmentModel departmentModel = (DepartmentModel) model;

            Intent intent = new Intent(getApplicationContext(), CityChooserActivity.class);
            intent.putExtra("docDepartment", departmentModel.getName());

            startActivity(intent);
        }
    }

    @Override
    public void onClickDelete(String s) {

    }
}