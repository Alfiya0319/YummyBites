package com.example.onlinefoodordering.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.onlinefoodordering.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {

    EditText nameEt, numberEt, emailEt, passwordEt, confirmPasswordEt;
    Button signupBtn;
    TextView loginTxt;
    ProgressDialog loader;

    // Firebase
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // 1. Initialize Firebase with Belgium URL
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance("https://onlinefoodorderingapp-7c2c0-default-rtdb.europe-west1.firebasedatabase.app").getReference();

        loader = new ProgressDialog(this);
        loader.setMessage("Creating your account...");
        loader.setCancelable(false);

        // 2. Link XML Views
        nameEt = findViewById(R.id.nameEt);
        numberEt = findViewById(R.id.numberEt);
        emailEt = findViewById(R.id.emailEt);
        passwordEt = findViewById(R.id.passwordEt);
        confirmPasswordEt = findViewById(R.id.confirmPasswordEt);
        signupBtn = findViewById(R.id.signupBtn);
        loginTxt = findViewById(R.id.loginTxt);

        // 3. Signup Button Click
        signupBtn.setOnClickListener(v -> {
            String name = nameEt.getText().toString().trim();
            String number = numberEt.getText().toString().trim();
            String email = emailEt.getText().toString().trim();
            String pass = passwordEt.getText().toString().trim();
            String confirmPass = confirmPasswordEt.getText().toString().trim();

            // Validation
            if (name.isEmpty() || number.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!pass.equals(confirmPass)) {
                confirmPasswordEt.setError("Passwords do not match");
                return;
            }

            if (pass.length() < 6) {
                passwordEt.setError("Min 6 characters required");
                return;
            }

            loader.show();

            // 🚀 STEP 1: Create User in Firebase Auth
            mAuth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String userId = mAuth.getCurrentUser().getUid();

                            // 🚀 STEP 2: Prepare Data for Realtime Database
                            HashMap<String, Object> userMap = new HashMap<>();
                            userMap.put("name", name);
                            userMap.put("number", number); // Variable 'number' use kiya hai
                            userMap.put("email", email);
                            userMap.put("uid", userId);

                            // 🚀 STEP 3: Save to Database
                            mDatabase.child("Users").child(userId).setValue(userMap)
                                    .addOnSuccessListener(aVoid -> {
                                        loader.dismiss();
                                        Toast.makeText(SignupActivity.this, "Account Created!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(SignupActivity.this, HomeActivity.class));
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        loader.dismiss();
                                        Toast.makeText(SignupActivity.this, "Database Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    });

                        } else {
                            loader.dismiss();
                            Toast.makeText(SignupActivity.this, "Auth Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });

        // 4. Back to Login
        loginTxt.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            finish();
        });
    }
}