package com.example.medicalmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.medicalmanagement.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPassword, etPhone, etAge, etSpecialization;
    private Spinner spinnerRole;
    private Button btnRegister;
    private TextView tvLoginLink;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etPhone = findViewById(R.id.etPhone);
        etAge = findViewById(R.id.etAge);
        spinnerRole = findViewById(R.id.spinnerRole);
        etSpecialization = findViewById(R.id.etSpecialization);
        btnRegister = findViewById(R.id.btnRegister);
        tvLoginLink = findViewById(R.id.tvLoginLink);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        spinnerRole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String role = parent.getItemAtPosition(position).toString();
                if ("Patient".equals(role)) {
                    etAge.setVisibility(View.VISIBLE);
                    etSpecialization.setVisibility(View.GONE);
                } else if ("Doctor".equals(role)) {
                    etAge.setVisibility(View.GONE);
                    etSpecialization.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnRegister.setOnClickListener(v -> registerUser());

        tvLoginLink.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String name = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String roleSelection = spinnerRole.getSelectedItem().toString();
        String ageStr = etAge.getText().toString().trim();
        String specialization = etSpecialization.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etFullName.setError("Name is required");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Phone is required");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = mAuth.getCurrentUser().getUid();
                    User user = new User();
                    user.setUid(uid);
                    user.setName(name);
                    user.setEmail(email);
                    user.setPhone(phone);
                    
                    if ("Patient".equals(roleSelection)) {
                        user.setRole("patient");
                        if (!TextUtils.isEmpty(ageStr)) {
                            try {
                                user.setAge(Integer.parseInt(ageStr));
                            } catch (NumberFormatException e) {
                                user.setAge(0);
                            }
                        }
                    } else if ("Doctor".equals(roleSelection)) {
                        user.setRole("doctor");
                        user.setSpecialization(specialization);
                    }

                    db.collection("users").document(uid).set(user)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                finish();
                            })
                            .addOnFailureListener(e -> Toast.makeText(RegisterActivity.this, "Failed to save user info: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(RegisterActivity.this, "Registration failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
