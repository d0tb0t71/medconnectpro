package com.example.medconnectpro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity {


    Button login_now_btn;
    EditText login_email,login_pass;
    FirebaseAuth mAuth;
    FirebaseUser user;
    TextView goToResisterBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login_email = findViewById(R.id.doc_login_email);
        login_pass = findViewById(R.id.doc_login_pass);
        login_now_btn = findViewById(R.id.login_now_btn);

        mAuth = FirebaseAuth.getInstance();


        goToResisterBtn = findViewById(R.id.goToResisterBtn);


        login_now_btn.setOnClickListener(v->{

            String email = login_email.getText().toString();
            String pass = login_pass.getText().toString();



            if(email.length()>5 && pass.length()>5){
                mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        user = mAuth.getCurrentUser();

                        if(task.isSuccessful() ){
                            if(user.isEmailVerified()){
                                startActivity(new Intent(getApplicationContext(),HomeScreen.class));
                            }
                            else
                            {
                                startActivity(new Intent(getApplicationContext(),EmailVerificationActivity.class));
                            }

                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Login Failed \n"+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else{
                Toast.makeText(getApplicationContext(), "Enter Valid Information", Toast.LENGTH_SHORT).show();
            }




        });


        goToResisterBtn.setOnClickListener(v -> {

            startActivity(new Intent(getApplicationContext(), RegAccountOption.class));

        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null ){

            if(user.isEmailVerified()){
                startActivity(new Intent(getApplicationContext(),HomeScreen.class));
            }
            else{
                startActivity(new Intent(getApplicationContext(),EmailVerificationActivity.class));
            }

        }


    }
}