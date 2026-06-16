package com.example.medicalmanagement;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicalmanagement.models.Appointment;

import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {

    private List<Appointment> appointmentList;

    public AppointmentAdapter(List<Appointment> appointmentList) {
        this.appointmentList = appointmentList;
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment_patient, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        Appointment appointment = appointmentList.get(position);

        holder.tvDoctorName.setText("Doctor: " + appointment.getDoctorName());
        holder.tvDateTime.setText("Date: " + appointment.getDate() + " | Time: " + appointment.getTime());
        holder.tvStatus.setText("Status: " + appointment.getStatus());

        if (!TextUtils.isEmpty(appointment.getDisease())) {
            holder.tvDisease.setVisibility(View.VISIBLE);
            holder.tvDisease.setText("Disease: " + appointment.getDisease());
        } else {
            holder.tvDisease.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(appointment.getPrescription())) {
            holder.tvPrescription.setVisibility(View.VISIBLE);
            holder.tvPrescription.setText("Prescription: " + appointment.getPrescription());
        } else {
            holder.tvPrescription.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(appointment.getProgress())) {
            holder.tvProgress.setVisibility(View.VISIBLE);
            holder.tvProgress.setText("Progress: " + appointment.getProgress());
        } else {
            holder.tvProgress.setVisibility(View.GONE);
        }

        if (appointment.isBillGenerated()) {
            holder.tvBillAmount.setVisibility(View.VISIBLE);
            holder.tvBillAmount.setText("Bill Amount: $" + appointment.getBillAmount());
            if (!TextUtils.isEmpty(appointment.getBillDescription())) {
                holder.tvBillDescription.setVisibility(View.VISIBLE);
                holder.tvBillDescription.setText("Bill Description: " + appointment.getBillDescription());
            } else {
                holder.tvBillDescription.setVisibility(View.GONE);
            }
        } else {
            holder.tvBillAmount.setVisibility(View.GONE);
            holder.tvBillDescription.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView tvDoctorName, tvDateTime, tvStatus, tvDisease, tvPrescription, tvProgress, tvBillDescription, tvBillAmount;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDoctorName = itemView.findViewById(R.id.tvDoctorName);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDisease = itemView.findViewById(R.id.tvDisease);
            tvPrescription = itemView.findViewById(R.id.tvPrescription);
            tvProgress = itemView.findViewById(R.id.tvProgress);
            tvBillDescription = itemView.findViewById(R.id.tvBillDescription);
            tvBillAmount = itemView.findViewById(R.id.tvBillAmount);
        }
    }
}
