package com.example.medicalmanagement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicalmanagement.models.Appointment;

import java.util.List;

public class PatientHistoryAdapter extends RecyclerView.Adapter<PatientHistoryAdapter.PatientHistoryViewHolder> {

    private List<Appointment> appointmentList;

    public PatientHistoryAdapter(List<Appointment> appointmentList) {
        this.appointmentList = appointmentList;
    }

    @NonNull
    @Override
    public PatientHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_patient_history, parent, false);
        return new PatientHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientHistoryViewHolder holder, int position) {
        Appointment appointment = appointmentList.get(position);

        holder.tvPatientName.setText(appointment.getPatientName());
        holder.tvDate.setText("Date: " + appointment.getDate());
        
        holder.tvDisease.setText("Disease: " + (appointment.getDisease() != null ? appointment.getDisease() : "N/A"));
        holder.tvPrescription.setText("Prescription: " + (appointment.getPrescription() != null ? appointment.getPrescription() : "N/A"));
        holder.tvProgress.setText("Progress: " + (appointment.getProgress() != null ? appointment.getProgress() : "N/A"));

        if (appointment.isBillGenerated()) {
            holder.tvBillAmount.setVisibility(View.VISIBLE);
            holder.tvBillAmount.setText("Bill: Rs. " + appointment.getBillAmount());
        } else {
            holder.tvBillAmount.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    static class PatientHistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvPatientName, tvDate, tvDisease, tvPrescription, tvProgress, tvBillAmount;

        public PatientHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPatientName = itemView.findViewById(R.id.tvPatientName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvDisease = itemView.findViewById(R.id.tvDisease);
            tvPrescription = itemView.findViewById(R.id.tvPrescription);
            tvProgress = itemView.findViewById(R.id.tvProgress);
            tvBillAmount = itemView.findViewById(R.id.tvBillAmount);
        }
    }
}
