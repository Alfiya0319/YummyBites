package com.example.onlinefoodordering.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.example.onlinefoodordering.R;

public class OrderConfirmActivity extends AppCompatActivity {

    TextView nameTv, orderIdTv, addressTv, amountTv, viewDetailsLink;
    Button trackBtn, homeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirm);

        // 1. XML Views ko initialize karna
        nameTv = findViewById(R.id.billUserName);
        orderIdTv = findViewById(R.id.billOrderId);
        addressTv = findViewById(R.id.billAddress);
        amountTv = findViewById(R.id.billAmount);
        viewDetailsLink = findViewById(R.id.viewDetailsLink);
        trackBtn = findViewById(R.id.trackOrderBtn);
        homeBtn = findViewById(R.id.goHomeBtn);

        // 2. User ka naam Firebase se lekar dikhana
        String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        if (userName != null && !userName.isEmpty()) {
            nameTv.setText("Thank you, " + userName + "!");
        } else {
            nameTv.setText("Thank you for your order!");
        }

        // 3. PaymentActivity se data catch karna
        String orderId = getIntent().getStringExtra("orderId");
        String totalAmount = getIntent().getStringExtra("total");
        String deliveryAddress = getIntent().getStringExtra("address");

        // 4. Data ko views par set karna (Check null to prevent crash)
        if (orderId != null) {
            orderIdTv.setText("Order ID: #" + orderId);
        }

        if (deliveryAddress != null) {
            addressTv.setText("Delivery to: " + deliveryAddress);
        } else {
            addressTv.setText("Delivery to: Your Location");
        }

        if (totalAmount != null) {
            amountTv.setText("Total Paid: ₹" + totalAmount);
        }

        // 5. BACK TO HOME Button: Saari activities clear karke Home par jana
        homeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(OrderConfirmActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // 6. TRACK ORDER Button: TrackOrderActivity par Order ID ke sath bhejna
        trackBtn.setOnClickListener(v -> {
            if (orderId != null) {
                Intent intent = new Intent(OrderConfirmActivity.this, TrackOrderActivity.class);
                intent.putExtra("orderId", orderId);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Order ID not found. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });

        // 7. View Order Summary (Toast Message)
        viewDetailsLink.setOnClickListener(v -> {
            Toast.makeText(this, "Order summary functionality coming soon!", Toast.LENGTH_SHORT).show();
        });
    }
}