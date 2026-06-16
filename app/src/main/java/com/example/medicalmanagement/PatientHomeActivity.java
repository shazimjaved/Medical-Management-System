package com.example.medicalmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class PatientHomeActivity extends AppCompatActivity {

    private TextView tvName, tvEmail, tvPhone, tvAge;
    private BottomNavigationView bottomNavigationView;
    private Button btnLogout;
    
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_home);

        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvAge = findViewById(R.id.tvAge);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        btnLogout = findViewById(R.id.btnLogout);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(PatientHomeActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        if (mAuth.getCurrentUser() != null) {
            String uid = mAuth.getCurrentUser().getUid();
            db.collection("users").document(uid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            String email = documentSnapshot.getString("email");
                            String phone = documentSnapshot.getString("phone");
                            Long age = documentSnapshot.getLong("age");

                            tvName.setText("Name: " + (name != null ? name : "N/A"));
                            tvEmail.setText("Email: " + (email != null ? email : "N/A"));
                            tvPhone.setText("Phone: " + (phone != null ? phone : "N/A"));
                            tvAge.setText("Age: " + (age != null ? age : "N/A"));
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(PatientHomeActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show());
        }

        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_profile) {
                // Already here
                return true;
            } else if (id == R.id.nav_book) {
                startActivity(new Intent(PatientHomeActivity.this, BookAppointmentActivity.class));
                return true;
            } else if (id == R.id.nav_bills) {
                startActivity(new Intent(PatientHomeActivity.this, BillsHistoryActivity.class));
                return true;
            } else if (id == R.id.nav_treatment) {
                startActivity(new Intent(PatientHomeActivity.this, TreatmentHistoryActivity.class));
                return true;
            }
            return false;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, 1, Menu.NONE, "Logout").setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == 1) {
            mAuth.signOut();
            startActivity(new Intent(PatientHomeActivity.this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
