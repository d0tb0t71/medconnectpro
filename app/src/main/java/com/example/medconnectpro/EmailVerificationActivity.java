package com.example.medconnectpro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EmailVerificationActivity extends AppCompatActivity {

    Button verify_refresh_btn;
    FirebaseAuth firebaseAuth;
    TextView resendVmail;
    FirebaseUser user;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        verify_refresh_btn = findViewById(R.id.verify_refresh_btn);
        resendVmail = findViewById(R.id.resendVmail);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        verify_refresh_btn.setOnClickListener(v-> {

            user.reload();

            if(user.isEmailVerified()){

                Log.d("EmailVF", "user verified");

                DocumentReference docRef = db.collection("users").document(user.getUid());

                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {

                            Log.d("EmailVF", "onComplete");

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
                                Log.d("EmailVF", "No such document");
                            }
                        } else {
                            Log.d("EmailVF", "get failed with ", task.getException());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.d("EmailVF", "onFailed");

                        Toast.makeText(EmailVerificationActivity.this, "Failed, " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();


                    }
                });



            }
            else{
                Log.d("EmailVF", "user not verified");

            }

            Log.d("EmailVF", "Clicked");



        });

        resendVmail.setOnClickListener(v-> {

            user.sendEmailVerification();

            Toast.makeText(this, "Verification mail has been sent", Toast.LENGTH_SHORT).show();
        });

    }
}