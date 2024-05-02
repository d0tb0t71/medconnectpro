package com.example.medconnectpro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class LoginActivity extends AppCompatActivity {


    Button login_now_btn;
    EditText login_email,login_pass;
    FirebaseAuth mAuth;
    FirebaseUser user;
    TextView goToResisterBtn , forgotPassTV;

    ProgressBar progressView;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login_email = findViewById(R.id.doc_login_email);
        login_pass = findViewById(R.id.doc_login_pass);
        login_now_btn = findViewById(R.id.login_now_btn);
        forgotPassTV = findViewById(R.id.forgotPassTV);

        mAuth = FirebaseAuth.getInstance();


        goToResisterBtn = findViewById(R.id.goToResisterBtn);
        progressView = findViewById(R.id.progressView);

        //progressView.setVisibility(View.VISIBLE);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();


        forgotPassTV.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), ForgotPassActivity.class));
        });

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

                                DocumentReference docRef = db.collection("users").document(user.getUid());

                                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {

                                                Log.d("EmailVF", "document exist");

                                                UserModel userModel = document.toObject(UserModel.class);

                                                UserDataManager userDataManager = UserDataManager.getInstance();
                                                userDataManager.setIsDoctor(userModel.isDoctor());
                                                userDataManager.setDepartment(userModel.getDepartment());
                                                userDataManager.setCity(userModel.getCity());
                                                userDataManager.setMobile(userModel.getPhone());
                                                userDataManager.setEmail(userModel.getEmail());

                                                if(userModel.isDoctor()){

                                                    Log.d("EmailVF", "Doctor Login");

                                                    Intent intent = new Intent(getApplicationContext(),DateChooserActivity.class);
                                                    intent.putExtra("docDepartment", userModel.getDepartment());
                                                    intent.putExtra("docCity", userModel.getCity());
                                                    intent.putExtra("docEmail", userModel.getEmail());

                                                    intent.putExtra("docName", userModel.getFullname());
                                                    intent.putExtra("docMobile", userModel.getPhone());

                                                    startActivity(intent);

                                                } else {

                                                    Log.d("EmailVF", "Patient Login");

                                                    startActivity(new Intent(getApplicationContext(),HomeScreen.class));
                                                    finishAffinity();
                                                }

                                            } else {
                                                Toast.makeText(getApplicationContext(), "Login Failed.", Toast.LENGTH_SHORT).show();
                                                Log.d("EmailVF", "No such document");
                                            }
                                        } else {
                                            Log.d("EmailVF", "get failed with ", task.getException());
                                            Toast.makeText(getApplicationContext(), "Login Failed.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                            else {
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

        if (user != null ){

            if(user.isEmailVerified()){

                DocumentReference docRef = db.collection("users").document(user.getUid());

                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {

                                Log.d("EmailVF", "document exist");

                                UserModel userModel = document.toObject(UserModel.class);

                                UserDataManager userDataManager = UserDataManager.getInstance();
                                userDataManager.setIsDoctor(userModel.isDoctor());
                                userDataManager.setDepartment(userModel.getDepartment());
                                userDataManager.setCity(userModel.getCity());
                                userDataManager.setMobile(userModel.getPhone());
                                userDataManager.setEmail(userModel.getEmail());

                                if(userModel.isDoctor()){

                                    Log.d("EmailVF", "Doctor Login");

                                    Intent intent = new Intent(getApplicationContext(),DateChooserActivity.class);
                                    intent.putExtra("docDepartment", userModel.getDepartment());
                                    intent.putExtra("docCity", userModel.getCity());
                                    intent.putExtra("docEmail", userModel.getEmail());

                                    intent.putExtra("docName", userModel.getFullname());
                                    intent.putExtra("docMobile", userModel.getPhone());

                                    startActivity(intent);
                                    finishAffinity();

                                } else {

                                    Log.d("EmailVF", "Patient Login");

                                    startActivity(new Intent(getApplicationContext(),HomeScreen.class));
                                    finishAffinity();
                                }

                            } else {
                                Log.d("EmailVF", "No such document");
                            }
                        } else {
                            Log.d("EmailVF", "get failed with ", task.getException());
                        }
                    }
                });

            }

        }
        else{
            progressView.setVisibility(View.GONE);
        }


    }
}