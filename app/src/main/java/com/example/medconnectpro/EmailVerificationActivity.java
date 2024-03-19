package com.example.medconnectpro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailVerificationActivity extends AppCompatActivity {

    Button verify_refresh_btn;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        verify_refresh_btn = findViewById(R.id.verify_refresh_btn);


        verify_refresh_btn.setOnClickListener(v-> {

            firebaseAuth = FirebaseAuth.getInstance();
            user = firebaseAuth.getCurrentUser();

            if(user.isEmailVerified()){
                startActivity(new Intent(getApplicationContext(),HomeScreen.class));
            }
            else{
                user.sendEmailVerification();
                Toast.makeText(getApplicationContext(), "Email Sent", Toast.LENGTH_SHORT).show();
            }

        });

    }
}