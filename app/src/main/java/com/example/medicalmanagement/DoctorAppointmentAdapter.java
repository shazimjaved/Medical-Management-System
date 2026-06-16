package com.example.medicalmanagement;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicalmanagement.models.Appointment;

import java.util.List;

public class DoctorAppointmentAdapter extends RecyclerView.Adapter<DoctorAppointmentAdapter.DoctorAppointmentViewHolder> {

    private List<Appointment> appointmentList;
    private boolean isTodayScreen;
    private OnAppointmentActionListener listener;

    public interface OnAppointmentActionListener {
        void onAccept(Appointment appointment);
        void onReject(Appointment appointment);
        void onUpdateHistory(Appointment appointment);
    }

    public DoctorAppointmentAdapter(List<Appointment> appointmentList, boolean isTodayScreen, OnAppointmentActionListener listener) {
        this.appointmentList = appointmentList;
        this.isTodayScreen = isTodayScreen;
        this.listener = listener;
    }

    public DoctorAppointmentAdapter(List<Appointment> appointmentList) {
        this.appointmentList = appointmentList;
        this.isTodayScreen = false;
        this.listener = null;
    }

    @NonNull
    @Override
    public DoctorAppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment_doctor, parent, false);
        return new DoctorAppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorAppointmentViewHolder holder, int position) {
        Appointment appointment = appointmentList.get(position);

        holder.tvPatientName.setText("Patient: " + appointment.getPatientName());
        holder.tvDate.setText("Date: " + appointment.getDate());
        holder.tvTime.setText("Time: " + appointment.getTime());
        holder.tvStatus.setText("Status: " + appointment.getStatus());

        String status = appointment.getStatus();
        if ("pending".equalsIgnoreCase(status)) {
            holder.tvStatus.setTextColor(Color.parseColor("#FFA500")); // Orange
        } else if ("accepted".equalsIgnoreCase(status) || "completed".equalsIgnoreCase(status)) {
            holder.tvStatus.setTextColor(Color.parseColor("#008000")); // Green
        } else if ("rejected".equalsIgnoreCase(status)) {
            holder.tvStatus.setTextColor(Color.parseColor("#FF0000")); // Red
        } else {
            holder.tvStatus.setTextColor(Color.BLACK);
        }

        if (isTodayScreen) {
            if ("pending".equalsIgnoreCase(status)) {
                holder.btnAccept.setVisibility(View.VISIBLE);
                holder.btnReject.setVisibility(View.VISIBLE);
                holder.btnUpdate.setVisibility(View.GONE);
            } else if ("accepted".equalsIgnoreCase(status)) {
                holder.btnAccept.setVisibility(View.GONE);
                holder.btnReject.setVisibility(View.GONE);
                holder.btnUpdate.setVisibility(View.VISIBLE);
            } else {
                holder.btnAccept.setVisibility(View.GONE);
                holder.btnReject.setVisibility(View.GONE);
                holder.btnUpdate.setVisibility(View.GONE);
            }

            holder.btnAccept.setOnClickListener(v -> {
                if(listener != null) listener.onAccept(appointment);
            });
            holder.btnReject.setOnClickListener(v -> {
                if(listener != null) listener.onReject(appointment);
            });
            holder.btnUpdate.setOnClickListener(v -> {
                if(listener != null) listener.onUpdateHistory(appointment);
            });
        } else {
            holder.btnAccept.setVisibility(View.GONE);
            holder.btnReject.setVisibility(View.GONE);
            holder.btnUpdate.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    static class DoctorAppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView tvPatientName, tvDate, tvTime, tvStatus;
        Button btnAccept, btnReject, btnUpdate, btnBill;

        public DoctorAppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPatientName = itemView.findViewById(R.id.tvPatientName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnAccept = itemView.findViewById(R.id.btn_accept);
            btnReject = itemView.findViewById(R.id.btn_reject);
            btnUpdate = itemView.findViewById(R.id.btn_update);
            btnBill = itemView.findViewById(R.id.btn_bill);
        }
    }
}
