package com.example.medconnectpro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class AboutActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ImageView backBtn, navBtn;
    NavigationView navView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        UserDataManager userDataManager = UserDataManager.getInstance();

        drawerLayout = findViewById(R.id.drawerLayout);
        backBtn = findViewById(R.id.backBtn);
        navBtn = findViewById(R.id.navBtn);
        navView = findViewById(R.id.navView);
        navView.bringToFront();

        if(userDataManager.isDoctor()){
            MenuItem historyItem = navView.getMenu().findItem(R.id.HistoryMenu);
            historyItem.setVisible(false);
        }

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
}