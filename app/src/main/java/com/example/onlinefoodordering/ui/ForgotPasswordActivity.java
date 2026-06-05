package com.example.onlinefoodordering.ui;

import android.app.ProgressDialog; // 👈 ProgressDialog import karein
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.onlinefoodordering.R;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText forgotEmailEt;
    Button resetBtn;
    TextView backToLoginTxt;
    FirebaseAuth mAuth;
    ProgressDialog loader; // 👈 Loader define karein

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mAuth = FirebaseAuth.getInstance();

        // Loader setup
        loader = new ProgressDialog(this);
        loader.setMessage("Sending reset link...");
        loader.setCancelable(false);

        forgotEmailEt = findViewById(R.id.forgotEmailEt);
        resetBtn = findViewById(R.id.resetBtn);
        backToLoginTxt = findViewById(R.id.backToLoginTxt);

        resetBtn.setOnClickListener(v -> {
            String email = forgotEmailEt.getText().toString().trim();

            if (email.isEmpty()) {
                forgotEmailEt.setError("Email required");
                return;
            }

            // 🚀 Loader dikhayein
            loader.show();

            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        loader.dismiss(); // 🚀 Loader band karein

                        if (task.isSuccessful()) {
                            Toast.makeText(ForgotPasswordActivity.this,
                                    "Reset link sent! Please check your email.",
                                    Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(ForgotPasswordActivity.this,
                                    "Error: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        backToLoginTxt.setOnClickListener(v -> finish());
    }
}