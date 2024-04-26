package com.example.medconnectpro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassActivity extends AppCompatActivity {


    ImageView backBtnFPA;
    TextView backBtnLogin;
    EditText for_login_email;
    Button resetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);

        backBtnFPA = findViewById(R.id.backBtnFPA);
        backBtnLogin = findViewById(R.id.backBtnLogin);
        backBtnLogin = findViewById(R.id.backBtnLogin);
        for_login_email = findViewById(R.id.for_login_email);
        resetPassword = findViewById(R.id.resetPassword);

        backBtnFPA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getOnBackPressedDispatcher().onBackPressed();

            }
        });

        backBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getOnBackPressedDispatcher().onBackPressed();

            }
        });

        resetPassword.setOnClickListener(v -> {

            String email = for_login_email.getText().toString();

            if(email.length() > 5){

                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ForgotPassActivity.this, "Password reset mail has been sent to your email", Toast.LENGTH_SHORT).show();
                                } else{
                                    Toast.makeText(ForgotPassActivity.this, "Failed to sent password reset mail", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ForgotPassActivity.this, "Operation failed.\n" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            } else{
                Toast.makeText(ForgotPassActivity.this, "Enter valid email address", Toast.LENGTH_SHORT).show();
            }


        });

    }
}