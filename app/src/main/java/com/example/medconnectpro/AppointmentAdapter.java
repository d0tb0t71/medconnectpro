package com.example.medconnectpro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.MyViewHolder>  {
    Context context;
    ArrayList<AppointmentModel> list;

    private OnItemClick mCallback;

    public AppointmentAdapter(Context context, ArrayList<AppointmentModel> list,  OnItemClick listener) {
        this.context = context;
        this.list = list;
        this.mCallback = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.appointment_card_view,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        AppointmentModel appointmentModel = list.get(position);

        holder.patient.setText(appointmentModel.getName());
        holder.date.setText("Date: "+appointmentModel.getDate().replaceAll("_" , "/"));
        holder.time.setText("Time: "+appointmentModel.getTime().replaceAll("_" , ":"));
        holder.status.setText("Status: "+ "Pending");


        holder.patient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mCallback.onClick(appointmentModel.getName());

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void filteredList(ArrayList<AppointmentModel> filterList) {
        list = filterList;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView patient,date, time, status;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            patient = itemView.findViewById(R.id.a_patientTV);
            date = itemView.findViewById(R.id.a_dateTV);
            time = itemView.findViewById(R.id.a_timeTV);
            status = itemView.findViewById(R.id.a_statusTV);


        }
    }
}
