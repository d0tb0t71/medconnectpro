package com.example.medconnectpro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

    UserModel uModel;
    String cityName = "", depName = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        UserDataManager userDataManager = UserDataManager.getInstance();

        fullname_ET = findViewById(R.id.fullname_ET);
        username_ET = findViewById(R.id.username_ET);
        phone_ET = findViewById(R.id.phone_ET);
        departmentSpinner = findViewById(R.id.deparmentSpinner);
        citySpinner = findViewById(R.id.citySpinner);
        updateNowBtn = findViewById(R.id.updateNowBtn);

        String[] departmentList = getResources().getStringArray(R.array.doctor_department_list);
        ArrayAdapter<String> depAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, departmentList);
        depAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        departmentSpinner.setAdapter(depAdapter);

        String[] citytList = getResources().getStringArray(R.array.city_list);
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, citytList);
        depAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(cityAdapter);

        if(!userDataManager.isDoctor()){
            departmentSpinner.setVisibility(View.GONE);
        }

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        DocumentReference docRef = db.collection("users").document(user.getUid());

        departmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = departmentList[position];
                depName = selectedItem;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = citytList[position];
                cityName = selectedItem;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        UserModel userModel = document.toObject(UserModel.class);

                        uModel = userModel;

                        fullname_ET.setText(userModel.getFullname());
                        username_ET.setText(userModel.getUsername());
                        phone_ET.setText(userModel.getPhone());

                        departmentSpinner.setSelection(depAdapter.getPosition(userModel.getDepartment()));
                        citySpinner.setSelection(cityAdapter.getPosition(userModel.getCity()));



                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });

        updateNowBtn.setOnClickListener(v-> {

            String fName = fullname_ET.getText().toString();
            String uName = username_ET.getText().toString();
            String mobile = phone_ET.getText().toString();

            if(userDataManager.isDoctor() & uModel != null && fName.length() > 0 && uName.length() > 0 && mobile.length() > 0 && cityName.length() > 0 && depName.length() > 0 && !cityName.equals("Select City") && !depName.equals("Select Department")){

                uModel.setFullname(fName);
                uModel.setUsername(uName);
                uModel.setCity(cityName);
                uModel.setPhone(mobile);
                uModel.setDepartment(depName);

                docRef.set(uModel);

                Intent intent = new Intent(getApplicationContext(),DateChooserActivity.class);
                intent.putExtra("docDepartment", uModel.getDepartment());
                intent.putExtra("docCity", uModel.getCity());
                intent.putExtra("docEmail", uModel.getEmail());

                intent.putExtra("docName", uModel.getFullname());
                intent.putExtra("docMobile", uModel.getPhone());

                startActivity(intent);
                finishAffinity();
            }

            else if(uModel != null && fName.length() > 0 && uName.length() > 0 && mobile.length() > 0 && cityName.length() > 0 && !cityName.equals("Select City") ){

                uModel.setFullname(fName);
                uModel.setUsername(uName);
                uModel.setPhone(mobile);
                uModel.setCity(cityName);

                docRef.set(uModel);
                startActivity(new Intent(getApplicationContext() , HomeScreen.class));
                finishAffinity();

            } else {
                Toast.makeText(this, "Invalid Input!!!", Toast.LENGTH_SHORT).show();
            }


        });

    }
}