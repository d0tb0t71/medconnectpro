package com.example.medconnectpro;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class BookAppointmentActivity extends AppCompatActivity {


    EditText selectTimeET, selectDateET;
    CustomTimePickerDialog customTimePickerDialog;
    DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        selectDateET = findViewById(R.id.selectDateET);
        selectTimeET = findViewById(R.id.selectTimeET);

        selectDateET.setOnClickListener(v-> {

            openDatePickerDialog();

        });

        selectTimeET.setOnClickListener(v-> {

            openTimePickerDialog();

        });



    }

    void openTimePickerDialog(){

        int hourOfDay = 23;
        int minute = 50;
        boolean is24hourView = true;

        CustomTimePickerDialog.OnTimeSetListener timeSetListener = new CustomTimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                if(hourOfDay >= 2 && hourOfDay <=6){
                    String hour = String.format("%02d", hourOfDay);
                    String min = String.format("%02d", minute);

                    selectTimeET.setText(hour + " : " + min);
                } else{

                    Toast.makeText(BookAppointmentActivity.this, "Please select Time between 2:00 to 6:00", Toast.LENGTH_LONG).show();

                }



            }
        };

        CustomTimePickerDialog timePickerDialog = new CustomTimePickerDialog(BookAppointmentActivity.this,timeSetListener,
                Calendar.getInstance().get(Calendar.HOUR),
                CustomTimePickerDialog.getRoundedMinute(Calendar.getInstance().get(Calendar.MINUTE) + CustomTimePickerDialog.TIME_PICKER_INTERVAL),
                false
        );
        timePickerDialog.setTitle("Select Time between 2:00 to 6:00");
        timePickerDialog.show();

    }

    void openDatePickerDialog(){

        final Calendar c = Calendar.getInstance();

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                BookAppointmentActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        selectDateET.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                    }
                },
                year, month, day);
        datePickerDialog.show();

    }
}