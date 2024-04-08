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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PatientRegisterActivity extends AppCompatActivity {

    Button reg_btn;
    EditText email, phone, fullName, username, pass, confirmPass;

    Spinner citySpinner;
    String citytxt = "";
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email = findViewById(R.id.email_Reg);
        phone = findViewById(R.id.mobile_Reg);
        fullName = findViewById(R.id.fullname_Reg);
        username = findViewById(R.id.username_Reg);
        pass = findViewById(R.id.pass_Reg);
        confirmPass = findViewById(R.id.repeat_pass_Reg);
        citySpinner = findViewById(R.id.city_Reg);

        reg_btn = findViewById(R.id.reg_Btn);

        mAuth = FirebaseAuth.getInstance();

        String[] cityList = getResources().getStringArray(R.array.city_list);
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cityList);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(cityAdapter);


        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        reg_btn.setOnClickListener(v->{

            String email_st = email.getText().toString();
            String phone_st = phone.getText().toString();
            String fullname_st = fullName.getText().toString();
            String username_st = fullName.getText().toString();
            String city_st = citytxt;
            String pass_st = pass.getText().toString();
            String c_pass_st = confirmPass.getText().toString();


            if(email_st.length()>5 && pass.length()>5 && fullname_st.length() > 3 && pass_st.equals(c_pass_st) && phone_st.length() > 10 && !citytxt.equals("Select City")){

                mAuth.createUserWithEmailAndPassword(email_st,pass_st).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            FirebaseUser user = mAuth.getCurrentUser();

                            UserModel userModel = new UserModel(email_st,phone_st,fullname_st,username_st,"NO",city_st, false);

                            db = FirebaseFirestore.getInstance();

                            db.collection("users")
                                    .document(user.getUid())
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