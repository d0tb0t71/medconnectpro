package com.example.medconnectpro;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.MyViewHolder>  {
    Context context;
    ArrayList<CityModel> list;

    private OnItemClick mCallback;

    public CityAdapter(Context context, ArrayList<CityModel> list,  OnItemClick listener) {
        this.context = context;
        this.list = list;
        this.mCallback = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        CityModel cityModel = list.get(position);

        holder.title.setText(cityModel.getName());
        holder.count.setText(""+cityModel.getCount());



        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mCallback.onClick(cityModel.getName());

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void filteredList(ArrayList<CityModel> filterList) {
        list = filterList;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title,count;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.list_title);
            count = itemView.findViewById(R.id.list_count);


        }
    }
}