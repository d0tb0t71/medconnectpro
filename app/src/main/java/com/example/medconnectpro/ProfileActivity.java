package com.example.medconnectpro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {


    TextView p_name, p_email, p_city, p_dep, p_mobile;

    Button updateProfileBtn;
    FirebaseFirestore db;
    FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        p_name = findViewById(R.id.p_name);
        p_email = findViewById(R.id.p_email);
        p_city = findViewById(R.id.p_city);
        p_dep = findViewById(R.id.p_dep);
        p_mobile = findViewById(R.id.p_mobile);
        updateProfileBtn = findViewById(R.id.updateProfileBtn);


        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        DocumentReference docRef = db.collection("users").document(user.getUid());

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        UserModel userModel = document.toObject(UserModel.class);

                        p_name.setText(userModel.getFullname());
                        p_city.setText(userModel.getCity());
                        p_dep.setText(userModel.getDepartment());
                        p_mobile.setText(userModel.getPhone());
                        p_email.setText(userModel.getEmail());

                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });

        updateProfileBtn.setOnClickListener(v-> {

            startActivity(new Intent(getApplicationContext(), UpdateProfileActivity.class));

        });

    }
}