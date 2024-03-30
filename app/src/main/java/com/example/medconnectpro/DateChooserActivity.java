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

        departmentName = getIntent().getStringExtra("department");
        cityName = getIntent().getStringExtra("city");
        doctorMail = getIntent().getStringExtra("doctorMail");



        bookAppointmentNow.setOnClickListener( v-> {

            Intent intent = new Intent(getApplicationContext(),BookAppointmentActivity.class);
            intent.putExtra("department", departmentName);
            intent.putExtra("city", cityName);
            intent.putExtra("doctorMail", doctorMail);
            startActivity(intent);

        });

    }
}