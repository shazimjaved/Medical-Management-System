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

public class BillsHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerViewBills;
    private TextView tvEmpty;
    private BottomNavigationView bottomNavigationView;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private AppointmentAdapter adapter;
    private List<Appointment> appointmentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bills_history);

        recyclerViewBills = findViewById(R.id.recyclerViewBills);
        tvEmpty = findViewById(R.id.tvEmpty);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        recyclerViewBills.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AppointmentAdapter(appointmentList);
        recyclerViewBills.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        bottomNavigationView.setSelectedItemId(R.id.nav_bills);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_bills) {
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, PatientHomeActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_book) {
                startActivity(new Intent(this, BookAppointmentActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_treatment) {
                startActivity(new Intent(this, TreatmentHistoryActivity.class));
                finish();
                return true;
            }
            return false;
        });

        fetchBills();
    }

    private void fetchBills() {
        if (mAuth.getCurrentUser() == null) return;
        
        String currentUid = mAuth.getCurrentUser().getUid();

        db.collection("appointments")
                .whereEqualTo("patientId", currentUid)
                .whereEqualTo("status", "completed")
                .whereEqualTo("billGenerated", true)
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
                        recyclerViewBills.setVisibility(View.GONE);
                    } else {
                        tvEmpty.setVisibility(View.GONE);
                        recyclerViewBills.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load bills: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
