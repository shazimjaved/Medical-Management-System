package com.example.medicalmanagement;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.medicalmanagement.models.Appointment;
import com.example.medicalmanagement.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BookAppointmentActivity extends AppCompatActivity {

    private Spinner spinnerDoctor;
    private TextView tvDate, tvTime;
    private Button btnPickDate, btnPickTime, btnBookAppointment;
    private BottomNavigationView bottomNavigationView;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private List<User> doctorList = new ArrayList<>();
    private List<String> doctorNamesList = new ArrayList<>();
    private ArrayAdapter<String> doctorAdapter;

    private String selectedDate = "";
    private String selectedTime = "";
    private String currentPatientName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        spinnerDoctor = findViewById(R.id.spinnerDoctor);
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);
        btnPickDate = findViewById(R.id.btnPickDate);
        btnPickTime = findViewById(R.id.btnPickTime);
        btnBookAppointment = findViewById(R.id.btnBookAppointment);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Setup bottom navigation
        bottomNavigationView.setSelectedItemId(R.id.nav_book);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_book) {
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, PatientHomeActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_bills) {
                startActivity(new Intent(this, BillsHistoryActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_treatment) {
                startActivity(new Intent(this, TreatmentHistoryActivity.class));
                finish();
                return true;
            }
            return false;
        });

        fetchPatientName();
        fetchDoctors();

        btnPickDate.setOnClickListener(v -> showDatePicker());
        btnPickTime.setOnClickListener(v -> showTimePicker());
        btnBookAppointment.setOnClickListener(v -> bookAppointment());
    }

    private void fetchPatientName() {
        if (mAuth.getCurrentUser() != null) {
            db.collection("users").document(mAuth.getCurrentUser().getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            currentPatientName = documentSnapshot.getString("name");
                        }
                    });
        }
    }

    private void fetchDoctors() {
        db.collection("users").whereEqualTo("role", "doctor").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    doctorList.clear();
                    doctorNamesList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        User doctor = doc.toObject(User.class);
                        doctorList.add(doctor);
                        doctorNamesList.add(doctor.getName() + " (" + doctor.getSpecialization() + ")");
                    }
                    doctorAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, doctorNamesList);
                    doctorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerDoctor.setAdapter(doctorAdapter);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load doctors", Toast.LENGTH_SHORT).show());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, month1, dayOfMonth) -> {
                    // YYYY-MM-DD
                    selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year1, month1 + 1, dayOfMonth);
                    tvDate.setText(selectedDate);
                }, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute1) -> {
                    // HH:mm
                    selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute1);
                    tvTime.setText(selectedTime);
                }, hour, minute, true);
        timePickerDialog.show();
    }

    private void bookAppointment() {
        if (doctorList.isEmpty()) {
            Toast.makeText(this, "No doctors available", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedPosition = spinnerDoctor.getSelectedItemPosition();
        if (selectedPosition < 0) {
            Toast.makeText(this, "Please select a doctor", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(selectedDate)) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(selectedTime)) {
            Toast.makeText(this, "Please select a time", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(currentPatientName)) {
            Toast.makeText(this, "Loading patient info, please wait...", Toast.LENGTH_SHORT).show();
            return;
        }

        User selectedDoctor = doctorList.get(selectedPosition);

        Appointment appointment = new Appointment();
        appointment.setPatientId(mAuth.getCurrentUser().getUid());
        appointment.setPatientName(currentPatientName);
        appointment.setDoctorId(selectedDoctor.getUid());
        appointment.setDoctorName(selectedDoctor.getName());
        appointment.setDate(selectedDate);
        appointment.setTime(selectedTime);
        appointment.setStatus("pending");
        appointment.setDisease("");
        appointment.setPrescription("");
        appointment.setProgress("");
        appointment.setBillAmount(0.0);
        appointment.setBillDescription("");
        appointment.setBillGenerated(false);

        db.collection("appointments").add(appointment)
                .addOnSuccessListener(documentReference -> {
                    // Update the auto-generated ID inside the document for easier queries later
                    String generatedId = documentReference.getId();
                    documentReference.update("appointmentId", generatedId);
                    
                    Toast.makeText(this, "Appointment booked successfully", Toast.LENGTH_SHORT).show();
                    // Clear fields
                    selectedDate = "";
                    selectedTime = "";
                    tvDate.setText("Select Date");
                    tvTime.setText("Select Time");
                    spinnerDoctor.setSelection(0);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to book: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
