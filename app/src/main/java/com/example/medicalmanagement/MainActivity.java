package com.example.medicalmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            db.collection("users").document(uid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String role = documentSnapshot.getString("role");
                            if ("patient".equals(role)) {
                                Toast.makeText(MainActivity.this, "Role: Patient. Starting PatientHomeActivity...", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MainActivity.this, PatientHomeActivity.class));
                            } else if ("doctor".equals(role)) {
                                Toast.makeText(MainActivity.this, "Role: Doctor. Starting DoctorHomeActivity...", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MainActivity.this, DoctorHomeActivity.class));
                            }
                        }
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(MainActivity.this, "Failed to get user role: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                    });
        } else {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
    }
}
