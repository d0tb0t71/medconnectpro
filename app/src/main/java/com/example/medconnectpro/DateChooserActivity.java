package com.example.medconnectpro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class DateChooserActivity extends AppCompatActivity {


    Button bookAppointmentNow;

    String departmentName = "";
    String cityName = "";
    String doctorMail = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_chooser);

        bookAppointmentNow = findViewById(R.id.bookAppointmentBtn);

        departmentName = getIntent().getStringExtra("docDepartment");
        cityName = getIntent().getStringExtra("docCity");
        doctorMail = getIntent().getStringExtra("docEmail");



        bookAppointmentNow.setOnClickListener( v-> {

            Intent intent = new Intent(getApplicationContext(),BookAppointmentActivity.class);
            intent.putExtra("docDepartment", departmentName);
            intent.putExtra("docCity", cityName);
            intent.putExtra("docEmail", doctorMail);
            startActivity(intent);

        });

    }
}