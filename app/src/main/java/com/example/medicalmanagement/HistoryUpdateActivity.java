package com.example.medicalmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class HistoryUpdateActivity extends AppCompatActivity {

    private TextView tvPatientName, tvDate, tvTime;
    private EditText etDisease, etPrescription, etProgress;
    private Button btnSaveHistory, btnGoToBill;

    private FirebaseFirestore db;
    private String appointmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_update);

        tvPatientName = findViewById(R.id.tvPatientName);
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);
        etDisease = findViewById(R.id.et_disease);
        etPrescription = findViewById(R.id.et_prescription);
        etProgress = findViewById(R.id.et_progress);
        btnSaveHistory = findViewById(R.id.btn_save_history);
        btnGoToBill = findViewById(R.id.btn_go_to_bill);

        db = FirebaseFirestore.getInstance();

        appointmentId = getIntent().getStringExtra("appointmentId");
        if (appointmentId == null || appointmentId.isEmpty()) {
            Toast.makeText(this, "Error: Invalid appointment ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadAppointmentData();

        btnSaveHistory.setOnClickListener(v -> saveHistory());

        btnGoToBill.setOnClickListener(v -> {
            startActivity(new Intent(HistoryUpdateActivity.this, GenerateBillActivity.class).putExtra("appointmentId", appointmentId));
        });
    }

    private void loadAppointmentData() {
        db.collection("appointments").document(appointmentId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String patientName = documentSnapshot.getString("patientName");
                        String date = documentSnapshot.getString("date");
                        String time = documentSnapshot.getString("time");

                        String disease = documentSnapshot.getString("disease");
                        String prescription = documentSnapshot.getString("prescription");
                        String progress = documentSnapshot.getString("progress");

                        tvPatientName.setText("Patient: " + (patientName != null ? patientName : "N/A"));
                        tvDate.setText("Date: " + (date != null ? date : "N/A"));
                        tvTime.setText("Time: " + (time != null ? time : "N/A"));

                        if (disease != null) etDisease.setText(disease);
                        if (prescription != null) etPrescription.setText(prescription);
                        if (progress != null) etProgress.setText(progress);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load data", Toast.LENGTH_SHORT).show());
    }

    private void saveHistory() {
        String diseaseStr = etDisease.getText().toString().trim();
        String prescriptionStr = etPrescription.getText().toString().trim();
        String progressStr = etProgress.getText().toString().trim();

        if (TextUtils.isEmpty(diseaseStr)) {
            etDisease.setError("Disease/Diagnosis is required");
            return;
        }

        if (TextUtils.isEmpty(prescriptionStr)) {
            etPrescription.setError("Prescription is required");
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("disease", diseaseStr);
        updates.put("prescription", prescriptionStr);
        updates.put("progress", progressStr);
        updates.put("status", "completed");

        db.collection("appointments").document(appointmentId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "History saved", Toast.LENGTH_SHORT).show();
                    btnGoToBill.setVisibility(View.VISIBLE);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to save history: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
