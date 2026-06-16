package com.example.medicalmanagement;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class GenerateBillActivity extends AppCompatActivity {

    private TextView tvPatientName, tvDate, tvDisease;
    private EditText etBillDescription, etBillAmount;
    private Button btnGenerateBill;
    
    private CardView cardBillPreview;
    private TextView tvPreviewPatient, tvPreviewDescription, tvPreviewAmount;

    private FirebaseFirestore db;
    private String appointmentId;
    private String currentPatientName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_bill);

        tvPatientName = findViewById(R.id.tvPatientName);
        tvDate = findViewById(R.id.tvDate);
        tvDisease = findViewById(R.id.tvDisease);
        
        etBillDescription = findViewById(R.id.et_bill_description);
        etBillAmount = findViewById(R.id.et_bill_amount);
        btnGenerateBill = findViewById(R.id.btn_generate_bill);

        cardBillPreview = findViewById(R.id.card_bill_preview);
        tvPreviewPatient = findViewById(R.id.tvPreviewPatient);
        tvPreviewDescription = findViewById(R.id.tvPreviewDescription);
        tvPreviewAmount = findViewById(R.id.tvPreviewAmount);

        db = FirebaseFirestore.getInstance();

        appointmentId = getIntent().getStringExtra("appointmentId");
        if (appointmentId == null || appointmentId.isEmpty()) {
            Toast.makeText(this, "Error: Invalid appointment ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadAppointmentData();

        btnGenerateBill.setOnClickListener(v -> generateBill());
    }

    private void loadAppointmentData() {
        db.collection("appointments").document(appointmentId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String patientName = documentSnapshot.getString("patientName");
                        String date = documentSnapshot.getString("date");
                        String disease = documentSnapshot.getString("disease");

                        currentPatientName = patientName != null ? patientName : "N/A";
                        
                        tvPatientName.setText("Patient: " + currentPatientName);
                        tvDate.setText("Date: " + (date != null ? date : "N/A"));
                        tvDisease.setText("Disease: " + (disease != null ? disease : "N/A"));
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load data", Toast.LENGTH_SHORT).show());
    }

    private void generateBill() {
        String descriptionStr = etBillDescription.getText().toString().trim();
        String amountStr = etBillAmount.getText().toString().trim();

        if (TextUtils.isEmpty(descriptionStr)) {
            etBillDescription.setError("Description is required");
            return;
        }

        if (TextUtils.isEmpty(amountStr)) {
            etBillAmount.setError("Amount is required");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            etBillAmount.setError("Invalid amount");
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("billDescription", descriptionStr);
        updates.put("billAmount", amount);
        updates.put("billGenerated", true);

        db.collection("appointments").document(appointmentId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Bill generated", Toast.LENGTH_SHORT).show();
                    btnGenerateBill.setEnabled(false);
                    
                    tvPreviewPatient.setText("Patient: " + currentPatientName);
                    tvPreviewDescription.setText("Description: " + descriptionStr);
                    tvPreviewAmount.setText("Amount: Rs. " + amount);
                    
                    cardBillPreview.setVisibility(View.VISIBLE);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to generate bill: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
