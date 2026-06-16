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

import java.util.ArrayList;
import java.util.List;

public class PendingAppointmentsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewPending;
    private TextView tvEmpty;
    private BottomNavigationView bottomNavigationView;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DoctorAppointmentAdapter adapter;
    private List<Appointment> appointmentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_appointments);

        recyclerViewPending = findViewById(R.id.recyclerViewPending);
        tvEmpty = findViewById(R.id.tvEmpty);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        recyclerViewPending.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DoctorAppointmentAdapter(appointmentList);
        recyclerViewPending.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        bottomNavigationView.setSelectedItemId(R.id.nav_pending);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_pending) {
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, DoctorHomeActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_today) {
                startActivity(new Intent(this, TodaysAppointmentsActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_patient_history) {
                startActivity(new Intent(this, PatientHistoryActivity.class));
                finish();
                return true;
            }
            return false;
        });

        fetchPendingAppointments();
    }

    private void fetchPendingAppointments() {
        if (mAuth.getCurrentUser() == null) return;
        
        String currentUid = mAuth.getCurrentUser().getUid();

        db.collection("appointments")
                .whereEqualTo("doctorId", currentUid)
                .whereEqualTo("status", "pending")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    appointmentList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Appointment appt = doc.toObject(Appointment.class);
                        appointmentList.add(appt);
                    }
                    adapter.notifyDataSetChanged();

                    if (appointmentList.isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                        recyclerViewPending.setVisibility(View.GONE);
                    } else {
                        tvEmpty.setVisibility(View.GONE);
                        recyclerViewPending.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load appointments: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
