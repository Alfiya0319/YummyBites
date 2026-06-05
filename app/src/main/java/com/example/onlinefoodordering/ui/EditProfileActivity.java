package com.example.onlinefoodordering.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.onlinefoodordering.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {

    EditText editName, editEmail, editPhone;
    Button submitBtn;
    ImageView backBtn;
    TextView deleteAccountBtn; // Delete button ke liye

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // 1. Views ko link karein
        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        editPhone = findViewById(R.id.editPhone);
        submitBtn = findViewById(R.id.submitBtn);
        backBtn = findViewById(R.id.backBtn);
        deleteAccountBtn = findViewById(R.id.deleteAccountBtn);

        // 2. Firebase Initialize
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            userId = mAuth.getCurrentUser().getUid();
            mDatabase = FirebaseDatabase.getInstance("https://onlinefoodorderingapp-7c2c0-default-rtdb.europe-west1.firebasedatabase.app")
                    .getReference().child("Users").child(userId);
        }

        // 3. Data load karein
        loadUserData();

        // 4. Update Profile Logic
        submitBtn.setOnClickListener(v -> updateProfile());

        // 5. Delete Account Logic
        deleteAccountBtn.setOnClickListener(v -> showDeleteDialog());

        // 6. Back Button
        backBtn.setOnClickListener(v -> finish());
    }

    private void loadUserData() {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    editName.setText(snapshot.child("name").getValue(String.class));
                    editEmail.setText(snapshot.child("email").getValue(String.class));
                    editPhone.setText(snapshot.child("number").getValue(String.class));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void updateProfile() {
        String newName = editName.getText().toString().trim();
        String newEmail = editEmail.getText().toString().trim();
        String newPhone = editPhone.getText().toString().trim();

        if (newName.isEmpty() || newEmail.isEmpty() || newPhone.isEmpty()) {
            Toast.makeText(this, "Sabhi fields bharna zaroori hai", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, Object> updateData = new HashMap<>();
        updateData.put("name", newName);
        updateData.put("email", newEmail);
        updateData.put("number", newPhone); // Ab number bhi update hoga

        mDatabase.updateChildren(updateData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Profile Updated successfully!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Update failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeleteDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("Kya aap pakka account delete karna chahte hain? Ye wapas nahi aayega.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Database se data delete karein
                    mDatabase.removeValue().addOnCompleteListener(task -> {
                        // Auth se user delete karein
                        mAuth.getCurrentUser().delete().addOnCompleteListener(authTask -> {
                            if (authTask.isSuccessful()) {
                                Toast.makeText(this, "Account Deleted", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, LoginActivity.class));
                                finishAffinity(); // Saari activities band kar dega
                            }
                        });
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}