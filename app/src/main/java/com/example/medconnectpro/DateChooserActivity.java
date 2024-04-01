package com.example.medconnectpro;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DateChooserActivity extends AppCompatActivity implements OnItemClick {


    RecyclerView datesRecyclerView;
    private LinearLayoutManager linearLayoutManager;

    DateAdapter dateAdapter;

    ArrayList<DateModel> list;
    FirebaseFirestore db;
    Button bookAppointmentNow;

    String departmentName = "";
    String cityName = "";
    String doctorMail = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_chooser);

        bookAppointmentNow = findViewById(R.id.bookAppointmentBtn);
        datesRecyclerView = findViewById(R.id.datesRecyclerView);

        departmentName = getIntent().getStringExtra("docDepartment");
        cityName = getIntent().getStringExtra("docCity");
        doctorMail = getIntent().getStringExtra("docEmail");

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        datesRecyclerView.setLayoutManager(layoutManager);

        list = new ArrayList<>();

        dateAdapter = new DateAdapter(getApplicationContext(),list,this);
        datesRecyclerView.setAdapter(dateAdapter);

        db = FirebaseFirestore.getInstance();



        bookAppointmentNow.setOnClickListener( v-> {

            Intent intent = new Intent(getApplicationContext(),BookAppointmentActivity.class);
            intent.putExtra("docDepartment", departmentName);
            intent.putExtra("docCity", cityName);
            intent.putExtra("docEmail", doctorMail);
            startActivity(intent);

        });

        getData(departmentName, cityName,doctorMail);

    }

    private void getData(String departmentName, String cityName, String docMail) {

        db.collection("department").document(departmentName).collection("city").document(cityName).collection("doctor").document(docMail).collection("dates")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {
                            Toast.makeText(getApplicationContext(), "Error " + error.getMessage(), Toast.LENGTH_LONG).show();
                            return;
                        }

                        for (DocumentChange dc : value.getDocumentChanges()) {

                            if (dc.getType() == DocumentChange.Type.ADDED) {

                                DateModel dateModel = dc.getDocument().toObject(DateModel.class);

                                list.add(dateModel);

                            }

//                            Collections.sort(list, new Comparator<DateModel>() {
//                                @Override
//                                public int compare(DateModel o1, DateModel o2) {
//                                    // Assuming date format is in "yyyy-MM-dd"
//                                    return o1.getDate().compareTo(o2.getDate());
//                                }
//                            });

                            dateAdapter.notifyDataSetChanged();

                        }

                    }
                });

    }

    @Override
    public void onClick(String value) {

        Intent intent = new Intent(getApplicationContext(),AppointmentListActivity.class);
        intent.putExtra("docDepartment", departmentName);
        intent.putExtra("docCity", cityName);
        intent.putExtra("docEmail", doctorMail);
        intent.putExtra("docDate", value);
        startActivity(intent);

    }
}