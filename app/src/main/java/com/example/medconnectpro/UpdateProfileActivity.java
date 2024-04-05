package com.example.medconnectpro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UpdateProfileActivity extends AppCompatActivity {

    EditText fullname_ET,username_ET, phone_ET;
    Spinner departmentSpinner,citySpinner;

    Button updateNowBtn;
    FirebaseFirestore db;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        fullname_ET = findViewById(R.id.fullname_ET);
        username_ET = findViewById(R.id.username_ET);
        phone_ET = findViewById(R.id.phone_ET);
        departmentSpinner = findViewById(R.id.deparmentSpinner);
        citySpinner = findViewById(R.id.citySpinner);
        updateNowBtn = findViewById(R.id.updateProfileBtn);

        String[] departmentList = getResources().getStringArray(R.array.doctor_department_list);
        ArrayAdapter<String> depAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, departmentList);
        depAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        departmentSpinner.setAdapter(depAdapter);

        String[] citytList = getResources().getStringArray(R.array.city_list);
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, citytList);
        depAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(cityAdapter);


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

                        fullname_ET.setText(userModel.getFullname());
                        username_ET.setText(userModel.getUsername());
                        phone_ET.setText(userModel.getPhone());

                        departmentSpinner.setSelection(depAdapter.getPosition(userModel.getDepartment()));
                        citySpinner.setSelection(depAdapter.getPosition(userModel.getCity()));


                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });

        updateNowBtn.setOnClickListener(v-> {



        });

    }
}