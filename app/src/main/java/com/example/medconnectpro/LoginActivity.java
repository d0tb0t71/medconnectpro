package com.example.medconnectpro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;


public class LoginActivity extends AppCompatActivity {

    TextView goToResisterBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        goToResisterBtn = findViewById(R.id.goToResisterBtn);

        goToResisterBtn.setOnClickListener(v -> {

            startActivity(new Intent(getApplicationContext(), RegAccountOption.class));

        });
    }
}