package com.example.medicalmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicalmanagement.models.Appointment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TodaysAppointmentsActivity extends AppCompatActivity implements DoctorAppointmentAdapter.OnAppointmentActionListener {

    private RecyclerView recyclerViewToday;
    private TextView tvEmpty, tvDateLabel;
    private BottomNavigationView bottomNavigationView;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DoctorAppointmentAdapter adapter;
    private List<Appointment> appointmentList = new ArrayList<>();
    private String todayDateStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todays_appointments);

        recyclerViewToday = findViewById(R.id.recyclerViewToday);
        tvEmpty = findViewById(R.id.tvEmpty);
        tvDateLabel = findViewById(R.id.tvDateLabel);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        todayDateStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        tvDateLabel.setText("Date: " + todayDateStr);

        recyclerViewToday.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DoctorAppointmentAdapter(appointmentList, true, this);
        recyclerViewToday.setAdapter(adapter);

        bottomNavigationView.setSelectedItemId(R.id.nav_today);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_today) {
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, DoctorHomeActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_pending) {
                startActivity(new Intent(this, PendingAppointmentsActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_patient_history) {
                startActivity(new Intent(this, PatientHistoryActivity.class));
                finish();
                return true;
            }
            return false;
        });

        fetchTodaysAppointments();
    }

    private void fetchTodaysAppointments() {
        if (mAuth.getCurrentUser() == null) return;
        
        String currentUid = mAuth.getCurrentUser().getUid();

        db.collection("appointments")
                .whereEqualTo("doctorId", currentUid)
                .whereEqualTo("date", todayDateStr)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    appointmentList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Appointment appt = doc.toObject(Appointment.class);
                        // Ensure we have a valid appointment ID for updating status later
                        if (appt.getAppointmentId() == null || appt.getAppointmentId().isEmpty()) {
                            appt.setAppointmentId(doc.getId());
                        }
                        appointmentList.add(appt);
                    }
                    adapter.notifyDataSetChanged();

                    if (appointmentList.isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                        recyclerViewToday.setVisibility(View.GONE);
                    } else {
                        tvEmpty.setVisibility(View.GONE);
                        recyclerViewToday.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onAccept(Appointment appointment) {
        updateAppointmentStatus(appointment, "accepted");
    }

    @Override
    public void onReject(Appointment appointment) {
        updateAppointmentStatus(appointment, "rejected");
    }

    @Override
    public void onUpdateHistory(Appointment appointment) {
        startActivity(new Intent(this, HistoryUpdateActivity.class).putExtra("appointmentId", appointment.getAppointmentId()));
    }

    private void updateAppointmentStatus(Appointment appointment, String newStatus) {
        if (appointment.getAppointmentId() == null || appointment.getAppointmentId().isEmpty()) {
            Toast.makeText(this, "Invalid appointment ID", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("appointments").document(appointment.getAppointmentId())
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Appointment " + newStatus, Toast.LENGTH_SHORT).show();
                    fetchTodaysAppointments(); // Refresh the list
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
