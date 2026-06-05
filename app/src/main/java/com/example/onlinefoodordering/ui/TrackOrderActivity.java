package com.example.onlinefoodordering.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.*;
import com.example.onlinefoodordering.R;

public class TrackOrderActivity extends AppCompatActivity {

    ImageView s1, s2, s3, s4, backBtn;
    Button callBtn;
    String orderId;
    DatabaseReference orderRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_order);

        // 1. Views Initialization with Safety Checks
        s1 = findViewById(R.id.status1);
        s2 = findViewById(R.id.status2);
        s3 = findViewById(R.id.status3);
        s4 = findViewById(R.id.status4);
        backBtn = findViewById(R.id.backBtn);
        callBtn = findViewById(R.id.callSupportBtn);

        // 2. Intent Data Receive karna
        orderId = getIntent().getStringExtra("orderId");

        // AGAR Order ID nahi mili toh Activity band kar do (Crash rokne ke liye)
        if (orderId == null || orderId.isEmpty()) {
            Toast.makeText(this, "Order ID not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 3. Database URL (Europe Region ke saath)
        orderRef = FirebaseDatabase.getInstance("https://onlinefoodorderingapp-7c2c0-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference().child("Orders").child(orderId);

        if (backBtn != null) backBtn.setOnClickListener(v -> finish());

        if (callBtn != null) {
            callBtn.setOnClickListener(v -> Toast.makeText(this, "Calling Restaurant...", Toast.LENGTH_SHORT).show());
        }

        checkStatus();
    }

    private void checkStatus() {
        // pure node ko monitor karein
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String status = snapshot.child("status").getValue(String.class);
                    if (status != null) {
                        updateUI(status);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Yahan Toast tabhi dikhayein jab activity alive ho
                if (!isFinishing()) {
                    Toast.makeText(TrackOrderActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateUI(String status) {
        // DRAWABLE CHECK: Check karein ki ye icons aapke project mein hain
        // Agar nahi hain, toh niche wala code crash karega
        try {
            int green = R.drawable.ic_check_circle_green;
            int grey = R.drawable.ic_circle_grey;

            // Sabko pehle grey karein
            if(s1!=null) s1.setImageResource(grey);
            if(s2!=null) s2.setImageResource(grey);
            if(s3!=null) s3.setImageResource(grey);
            if(s4!=null) s4.setImageResource(grey);

            if (status.equalsIgnoreCase("Pending") || status.equalsIgnoreCase("Placed")) {
                if(s1!=null) s1.setImageResource(green);
            } else if (status.equalsIgnoreCase("Preparing")) {
                if(s1!=null) s1.setImageResource(green);
                if(s2!=null) s2.setImageResource(green);
            } else if (status.equalsIgnoreCase("Out for Delivery")) {
                if(s1!=null) s1.setImageResource(green);
                if(s2!=null) s2.setImageResource(green);
                if(s3!=null) s3.setImageResource(green);
            } else if (status.equalsIgnoreCase("Delivered")) {
                if(s1!=null) s1.setImageResource(green);
                if(s2!=null) s2.setImageResource(green);
                if(s3!=null) s3.setImageResource(green);
                if(s4!=null) s4.setImageResource(green);
            }
        } catch (Exception e) {
            // Agar icon nahi mila toh crash nahi hoga, sirf error dikhayega
            e.printStackTrace();
        }
    }
}