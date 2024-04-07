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


        verify_refresh_btn.setOnClickListener(v-> {

            firebaseAuth = FirebaseAuth.getInstance();
            user = firebaseAuth.getCurrentUser();
            db = FirebaseFirestore.getInstance();


            if(user.isEmailVerified()){

                DocumentReference docRef = db.collection("users").document(user.getUid());

                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                UserModel userModel = document.toObject(UserModel.class);

                                if(userModel.isDoctor()){

                                    UserDataManager userDataManager = UserDataManager.getInstance();
                                    userDataManager.setIsDoctor(userModel.isDoctor());

                                    Intent intent = new Intent(getApplicationContext(),DateChooserActivity.class);
                                    intent.putExtra("docDepartment", userModel.getDepartment());
                                    intent.putExtra("docCity", userModel.getCity());
                                    intent.putExtra("docEmail", userModel.getEmail());

                                    intent.putExtra("docName", userModel.getFullname());
                                    intent.putExtra("docMobile", userModel.getPhone());

                                    startActivity(intent);

                                } else {
                                    startActivity(new Intent(getApplicationContext(),HomeScreen.class));
                                    finishAffinity();
                                }

                            } else {
                                Log.d("TAG", "No such document");
                            }
                        } else {
                            Log.d("TAG", "get failed with ", task.getException());
                        }
                    }
                });



            }

        });

        resendVmail.setOnClickListener(v-> {

            user.sendEmailVerification();

        });

    }
}