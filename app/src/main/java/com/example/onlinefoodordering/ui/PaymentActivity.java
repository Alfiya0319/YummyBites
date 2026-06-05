package com.example.onlinefoodordering.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import com.example.onlinefoodordering.R;

public class PaymentActivity extends AppCompatActivity {

    CardView codCard, onlineCard;
    TextView totalAmountTv;
    ImageView backBtn;
    String totalAmount, userAddress;
    String dbUrl = "https://onlinefoodorderingapp-7c2c0-default-rtdb.europe-west1.firebasedatabase.app/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        codCard = findViewById(R.id.codCard);
        onlineCard = findViewById(R.id.onlineCard);
        totalAmountTv = findViewById(R.id.totalAmountPayment);
        backBtn = findViewById(R.id.backBtn);

        totalAmount = getIntent().getStringExtra("totalAmount");
        userAddress = getIntent().getStringExtra("address");

        totalAmountTv.setText("Amount to pay: ₹" + totalAmount);

        backBtn.setOnClickListener(v -> finish());

        codCard.setOnClickListener(v -> {
            placeOrder("Cash on Delivery");
        });

        onlineCard.setOnClickListener(v -> {
            Toast.makeText(this, "Online Payment Option coming soon!", Toast.LENGTH_SHORT).show();
        });
    }

    private void placeOrder(String paymentMethod) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference orderRef = FirebaseDatabase.getInstance(dbUrl).getReference().child("Orders");

        String orderId = String.valueOf(System.currentTimeMillis());

        // --- NAYA LOGIC: DATE NIKALNE KE LIYE ---
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        HashMap<String, Object> orderMap = new HashMap<>();
        orderMap.put("orderId", orderId);
        orderMap.put("userId", userId);
        orderMap.put("paymentMethod", paymentMethod);
        orderMap.put("totalAmount", totalAmount);
        orderMap.put("address", userAddress);
        orderMap.put("status", "Pending");

        // YE FIELD FIREBASE MEIN DATE SAVE KAREGI
        orderMap.put("orderDate", currentDate);

        // Optional: Agar aapne CartActivity se pehli item ki image bheji hai toh yahan add karein
        // orderMap.put("foodImage", getIntent().getStringExtra("foodImage"));

        orderRef.child(orderId).setValue(orderMap).addOnSuccessListener(unused -> {

            FirebaseDatabase.getInstance(dbUrl).getReference()
                    .child("Cart").child(userId).removeValue();

            Toast.makeText(this, "Order Placed Successfully! 🎉", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(PaymentActivity.this, OrderConfirmActivity.class);
            intent.putExtra("orderId", orderId);
            intent.putExtra("total", totalAmount);
            intent.putExtra("address", userAddress);
            intent.putExtra("orderDate", currentDate); // Confirm page ke liye bhi bhej diya

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Order Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}