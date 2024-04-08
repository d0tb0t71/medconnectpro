package com.example.medconnectpro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class BookAppointmentActivity extends AppCompatActivity {


    EditText patientNameET, mobileNumberET;
    TextView selectTimeET, selectDateET, docNameTV , callBtnTV;
    Button createAppointmentBtn;
    CustomTimePickerDialog customTimePickerDialog;

    Spinner bloodGroupSpinner, citySelectSpinner;
    DatePickerDialog datePickerDialog;

    String selectedDate = "";
    String selectedTime = "";
    String bloodGroup = "";
    String cityName = "";

    String departmentName = "";
    String doctorCity = "";
    String doctorEmail = "";
    String doctorName = "";
    String doctorMobile = "";
    String timeStamp = "";

    FirebaseFirestore db;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        departmentName = getIntent().getStringExtra("docDepartment");
        doctorCity = getIntent().getStringExtra("docCity");
        doctorEmail = getIntent().getStringExtra("docEmail");
        doctorName = getIntent().getStringExtra("docName");
        doctorMobile = getIntent().getStringExtra("docMobile");

        createAppointmentBtn = findViewById(R.id.createAppointmentBtn);
        bloodGroupSpinner = findViewById(R.id.bloodGroupSpinner);
        citySelectSpinner = findViewById(R.id.citySelectSpinner);

        patientNameET = findViewById(R.id.patientNameET);
        mobileNumberET = findViewById(R.id.mobileNumberET);
        selectDateET = findViewById(R.id.selectDateET);
        selectTimeET = findViewById(R.id.selectTimeET);
        docNameTV = findViewById(R.id.docNameTV);
        callBtnTV = findViewById(R.id.callBtnTV);

        String[] bloodGroupList = getResources().getStringArray(R.array.blood_group_list);
        ArrayAdapter<String> bloodAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, bloodGroupList);
        bloodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bloodGroupSpinner.setAdapter(bloodAdapter);

        String[] cityList = getResources().getStringArray(R.array.city_list);
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cityList);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySelectSpinner.setAdapter(cityAdapter);

        docNameTV.setText(doctorName);

        callBtnTV.setOnClickListener(v-> {

            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" +doctorMobile));
            startActivity(intent);

        });

        bloodGroupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = bloodGroupList[position];
                bloodGroup = selectedItem;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        citySelectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = cityList[position];
                cityName = selectedItem;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        selectDateET.setOnClickListener(v-> {

            openDatePickerDialog();

        });

        selectTimeET.setOnClickListener(v-> {

            openTimePickerDialog();

        });


        createAppointmentBtn.setOnClickListener(v -> {

            String name = patientNameET.getText().toString();
            String mobile = mobileNumberET.getText().toString();

            Timestamp timestamp = new Timestamp(System.currentTimeMillis());

            if(!name.isEmpty() && !mobile.isEmpty() && !selectedDate.isEmpty() && !selectedTime.isEmpty() && !bloodGroup.isEmpty() && !bloodGroup.contains("Blood Group") && !cityName.contains("Select City") && !cityName.isEmpty()){


                AppointmentModel appointmentModel = new AppointmentModel(timestamp.toString(),name, mobile, selectedDate, selectedTime, bloodGroup,cityName, user.getUid(), doctorName, departmentName, "Pending");

                Map<String, Object> dateModel = new HashMap();
                dateModel.put("date", selectedDate);


                DocumentReference docIdRef = db.collection("department")
                        .document(departmentName)
                        .collection("city")
                        .document(doctorCity)
                        .collection("doctor")
                        .document(doctorEmail)
                        .collection("dates")
                        .document(selectedDate)
                        .collection("appointments")
                        .document(selectedTime);
                docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {

                                AlertDialog.Builder builder1 = new AlertDialog.Builder(BookAppointmentActivity.this);
                                builder1.setTitle("The Time slot is already booked.");
                                builder1.setMessage("Select another time or check the appointment list to look for available slots.");
                                builder1.setCancelable(true);

                                builder1.setPositiveButton(
                                        "I UNDERSTAND",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });

                                AlertDialog alert11 = builder1.create();
                                alert11.show();

                            } else {

                                db.collection("users")
                                        .document(user.getUid())
                                        .collection("appointments")
                                        .document(timestamp.toString())
                                        .set(appointmentModel);

                                db.collection("department")
                                        .document(departmentName)
                                        .collection("city")
                                        .document(doctorCity)
                                        .collection("doctor")
                                        .document(doctorEmail)
                                        .collection("dates")
                                        .document(selectedDate)
                                        .set(dateModel);

                                db.collection("department")
                                        .document(departmentName)
                                        .collection("city")
                                        .document(doctorCity)
                                        .collection("doctor")
                                        .document(doctorEmail)
                                        .collection("dates")
                                        .document(selectedDate)
                                        .collection("appointments")
                                        .document(selectedTime)
                                        .set(appointmentModel);

                                    finish();
                            }
                        } else {
                            Log.d("ERROR", "Failed with: ", task.getException());
                        }
                    }
                });






            }
            else{
                Toast.makeText(this, "Invalid Input.\nPlease fill with correct information", Toast.LENGTH_LONG).show();
            }

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

                    selectedTime = hour + "_" + min;
                } else{


                    AlertDialog.Builder builder1 = new AlertDialog.Builder(BookAppointmentActivity.this);
                    builder1.setTitle("Please select Time between 2:00 to 6:00\"");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "I UNDERSTAND",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();

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

                        String dDay = String.format("%02d", dayOfMonth);
                        String dMonth = String.format("%02d", monthOfYear + 1);


                        selectDateET.setText(dDay + "/" + dMonth  + "/" + year);
                        selectedDate = (dDay + "_" + dMonth + "_" + year);

                    }
                },
                year, month, day);
        datePickerDialog.show();

    }
}