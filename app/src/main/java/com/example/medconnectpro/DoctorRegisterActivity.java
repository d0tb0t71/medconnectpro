package com.example.medconnectpro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DoctorRegisterActivity extends AppCompatActivity {

    Button doc_reg_btn;
    EditText email, phone, fullName, username;
    TextInputEditText pass, confirmPass;
    Spinner department, city;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    String deptxt = "";
    String citytxt = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_register);

        email = findViewById(R.id.doc_email_ET);
        phone = findViewById(R.id.doc_phone_ET);
        fullName = findViewById(R.id.doc_fullname_ET);
        username = findViewById(R.id.doc_username_ET);
        department = findViewById(R.id.doc_department_ET);
        city = findViewById(R.id.doc_city_ET);
        pass = findViewById(R.id.doc_pass_ET);
        confirmPass = findViewById(R.id.doc_confirmPass_ET);

        doc_reg_btn = findViewById(R.id.doc_reg_btn);

        mAuth = FirebaseAuth.getInstance();

        String[] depList = getResources().getStringArray(R.array.doctor_department_list);
        ArrayAdapter<String> depAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, depList);
        depAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        department.setAdapter(depAdapter);

        String[] cityList = getResources().getStringArray(R.array.city_list);
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cityList);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        city.setAdapter(cityAdapter);

        department.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = depList[position];
                deptxt = selectedItem;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = cityList[position];
                citytxt = selectedItem;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        doc_reg_btn.setOnClickListener(v->{

            String email_st = email.getText().toString();
            String phone_st = phone.getText().toString();
            String fullname_st = fullName.getText().toString();
            String username_st = fullName.getText().toString();
            String department_st = deptxt;
            String city_st = citytxt;
            String pass_st = pass.getText().toString();
            String c_pass_st = confirmPass.getText().toString();


            if(email_st.length()>5 && pass.length()>5 && fullname_st.length() > 3 && pass_st.equals(c_pass_st) && phone_st.length() > 10 && !deptxt.equals("Select Depertment") && !citytxt.equals("Select City")){

                mAuth.createUserWithEmailAndPassword(email_st,pass_st).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            FirebaseUser user = mAuth.getCurrentUser();

                            UserModel userModel = new UserModel(email_st,phone_st,fullname_st,username_st,department_st,city_st, true);

                            db = FirebaseFirestore.getInstance();

                            db.collection("users")
                                    .document(user.getUid())
                                    .set(userModel);
                            user.sendEmailVerification();

                            Map<String, Object> depMap = new HashMap();
                            depMap.put("name", department_st);
                            depMap.put("count", 0);


                            db.collection("department")
                                    .document(department_st)
                                    .set(depMap);

                            Map<String, Object> cityMap = new HashMap();
                            cityMap.put("name", city_st);
                            cityMap.put("count", 0);

                            db.collection("department")
                                    .document(department_st)
                                    .collection("city")
                                    .document(city_st)
                                    .set(cityMap);

                            db.collection("department")
                                    .document(department_st)
                                    .collection("city")
                                    .document(city_st)
                                    .collection("doctor")
                                    .document(email_st)
                                    .set(userModel);


                            user.sendEmailVerification();

                            Toast.makeText(getApplicationContext(), "Registration Successful", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(getApplicationContext(),EmailVerificationActivity.class));
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Registration Failed\n"+task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Registration Failed !\n"+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
            else{
                Toast.makeText(getApplicationContext(), "Please Enter Correct Information", Toast.LENGTH_SHORT).show();
            }


        });
    }
}