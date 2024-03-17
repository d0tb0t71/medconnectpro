package com.example.medconnectpro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

public class RegAccountOption extends AppCompatActivity {

    ImageView patientIV, doctorIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_account_option);

        patientIV = findViewById(R.id.patientIV);
        doctorIV = findViewById(R.id.doctorIV);

        patientIV.setOnClickListener(v -> {

            startActivity(new Intent(getApplicationContext(), PatientRegisterActivity.class));

        });

        doctorIV.setOnClickListener(v -> {

            startActivity(new Intent(getApplicationContext(), DoctorRegisterActivity.class));

        });


    }
}