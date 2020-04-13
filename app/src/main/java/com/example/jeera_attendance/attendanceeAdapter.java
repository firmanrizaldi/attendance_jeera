package com.example.jeera_attendance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class attendanceeAdapter extends RecyclerView.Adapter<attendanceeAdapter.MyViewHolder>{
    private Context context;
    private List<hr_attendance> hr_attendances;

    public attendanceeAdapter(Context context, List<hr_attendance> hr_attendances) {
        this.context = context;
        this.hr_attendances = hr_attendances;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attendance, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        hr_attendance hr_attendance = hr_attendances.get(position);
        holder.tvCheckin.setText(hr_attendance.getCheckin());
        holder.tvCheckout.setText(String.valueOf(hr_attendance.getCheckout()));
    }

    @Override
    public int getItemCount() {
        if (hr_attendances != null) return hr_attendances.size();
        else return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvCheckin, tvCheckout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCheckin = itemView.findViewById(R.id.tvCheckin);
            tvCheckout = itemView.findViewById(R.id.tvCheckout);
        }
    }
}
