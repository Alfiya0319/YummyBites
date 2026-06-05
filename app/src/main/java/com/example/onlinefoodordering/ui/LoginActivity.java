package com.example.onlinefoodordering.ui;

import android.app.ProgressDialog; // ProgressDialog import kiya
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.onlinefoodordering.R;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText emailEt, passwordEt;
    Button loginBtn;
    TextView signupTxt, forgotPasswordTxt;
    ProgressDialog loader; // Loader define kiya

    // Firebase declaration
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Loader setup
        loader = new ProgressDialog(this);
        loader.setMessage("Logging in... Please wait");
        loader.setCancelable(false);

        // linking XML views
        emailEt = findViewById(R.id.emailEt);
        passwordEt = findViewById(R.id.passwordEt);
        loginBtn = findViewById(R.id.loginBtn);
        signupTxt = findViewById(R.id.signupTxt);
        forgotPasswordTxt = findViewById(R.id.forgotPasswordTxt);

        // LOGIN BUTTON CLICK
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = emailEt.getText().toString().trim();
                String password = passwordEt.getText().toString().trim();

                if (email.isEmpty()) {
                    emailEt.setError("Email required");
                    return;
                }

                if (password.isEmpty()) {
                    passwordEt.setError("Password required");
                    return;
                }

                // 🚀 Loader dikhayein
                loader.show();

                // 🚀 REAL FIREBASE LOGIN
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            loader.dismiss(); // Result aate hi loader band

                            if (task.isSuccessful()) {
                                // Session save karna
                                SharedPreferences sp = getSharedPreferences("UserSession", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putBoolean("isLoggedIn", true);
                                editor.apply();

                                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                                // HomeActivity par bhejna
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Login Failed: " + task.getException().getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        // SIGN UP TEXT CLICK
        signupTxt.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });

        // FORGOT PASSWORD TEXT CLICK
        forgotPasswordTxt.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }
}