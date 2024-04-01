package com.example.medconnectpro;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AppointmentListActivity extends AppCompatActivity implements OnItemClick{

    RecyclerView appointmentRecyclerView;
    private LinearLayoutManager linearLayoutManager;

    AppointmentAdapter appointmentAdapter;

    ArrayList<AppointmentModel> list;
    FirebaseFirestore db;
    Button bookAppointmentNow;

    String departmentName = "";
    String cityName = "";
    String doctorMail = "";
    String docDate = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_list);

        appointmentRecyclerView = findViewById(R.id.appointmentRecyclerView);

        departmentName = getIntent().getStringExtra("docDepartment");
        cityName = getIntent().getStringExtra("docCity");
        doctorMail = getIntent().getStringExtra("docEmail");
        docDate = getIntent().getStringExtra("docDate");

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        appointmentRecyclerView.setLayoutManager(layoutManager);

        list = new ArrayList<>();

        appointmentAdapter = new AppointmentAdapter(getApplicationContext(),list,this);
        appointmentRecyclerView.setAdapter(appointmentAdapter);

        db = FirebaseFirestore.getInstance();

        getData(departmentName, cityName, doctorMail, docDate);

    }

    private void getData(String departmentName, String cityName, String docMail, String docDate) {

        db.collection("department").document(departmentName).collection("city").document(cityName).collection("doctor").document(docMail).collection("dates").document(docDate).collection("appointments")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {
                            Toast.makeText(getApplicationContext(), "Error " + error.getMessage(), Toast.LENGTH_LONG).show();
                            return;
                        }

                        for (DocumentChange dc : value.getDocumentChanges()) {

                            if (dc.getType() == DocumentChange.Type.ADDED) {

                                AppointmentModel appointmentModel = dc.getDocument().toObject(AppointmentModel.class);

                                list.add(appointmentModel);

                            }

//                            Collections.sort(list, new Comparator<DateModel>() {
//                                @Override
//                                public int compare(DateModel o1, DateModel o2) {
//                                    // Assuming date format is in "yyyy-MM-dd"
//                                    return o1.getDate().compareTo(o2.getDate());
//                                }
//                            });

                            appointmentAdapter.notifyDataSetChanged();

                        }

                    }
                });

    }

    @Override
    public void onClick(String value) {

    }
}