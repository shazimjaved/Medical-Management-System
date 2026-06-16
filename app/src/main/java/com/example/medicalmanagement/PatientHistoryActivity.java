package com.example.medicalmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PatientHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerViewHistory;
    private TextView tvEmpty;
    private BottomNavigationView bottomNavigationView;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private PatientHistoryAdapter adapter;
    private List<Appointment> appointmentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_history);

        recyclerViewHistory = findViewById(R.id.recyclerViewHistory);
        tvEmpty = findViewById(R.id.tvEmpty);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        recyclerViewHistory.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PatientHistoryAdapter(appointmentList);
        recyclerViewHistory.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        bottomNavigationView.setSelectedItemId(R.id.nav_patient_history);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_patient_history) {
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, DoctorHomeActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_pending) {
                startActivity(new Intent(this, PendingAppointmentsActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_today) {
                startActivity(new Intent(this, TodaysAppointmentsActivity.class));
                finish();
                return true;
            }
            return false;
        });

        fetchPatientHistory();
    }

    private void fetchPatientHistory() {
        if (mAuth.getCurrentUser() == null) return;
        
        String currentUid = mAuth.getCurrentUser().getUid();

        db.collection("appointments")
                .whereEqualTo("doctorId", currentUid)
                .whereEqualTo("status", "completed")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    appointmentList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Appointment appt = doc.toObject(Appointment.class);
                        appointmentList.add(appt);
                    }
                    
                    // Sort descending by date locally to avoid Firestore composite index requirement
                    Collections.sort(appointmentList, (a1, a2) -> {
                        String d1 = a1.getDate() != null ? a1.getDate() : "";
                        String d2 = a2.getDate() != null ? a2.getDate() : "";
                        return d2.compareTo(d1);
                    });
                    
                    adapter.notifyDataSetChanged();

                    if (appointmentList.isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                        recyclerViewHistory.setVisibility(View.GONE);
                    } else {
                        tvEmpty.setVisibility(View.GONE);
                        recyclerViewHistory.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load history: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("PatientHistory", "Firestore error: ", e);
                    // A message will appear in Logcat containing a link to build the index if necessary
                });
    }
}
