package com.example.onlinefoodordering.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.onlinefoodordering.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.Collections;
import com.example.onlinefoodordering.adapter.OrderAdapter;
import com.example.onlinefoodordering.model.OrderModel;

public class MyOrdersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private ArrayList<OrderModel> orderList;
    private ProgressBar progressBar;
    private ImageView backBtn;

    // Firebase URL (Ensure this is correct)
    private final String dbUrl = "https://onlinefoodorderingapp-7c2c0-default-rtdb.europe-west1.firebasedatabase.app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        // 1. Views Initialization (setContentView ke BAAD hi karein)
        recyclerView = findViewById(R.id.ordersRecyclerView);
        progressBar = findViewById(R.id.ordersProgress);
        backBtn = findViewById(R.id.backBtn);

        // 2. Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderList = new ArrayList<>();

        // Context bhejna zaroori hai images load karne ke liye
        adapter = new OrderAdapter(orderList, this);
        recyclerView.setAdapter(adapter);

        // 3. Back Button Functionality
        if (backBtn != null) {
            backBtn.setOnClickListener(v -> finish());
        }

        // 4. Data Load Karein
        loadOrders();
    }

    private void loadOrders() {
        String userId = FirebaseAuth.getInstance().getUid();

        if (userId == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        DatabaseReference ref = FirebaseDatabase.getInstance(dbUrl).getReference("Orders");

        // Firebase Query: User ID ke hisaab se filter karna
        // NOTE: Iske liye Firebase Console mein ".indexOn": "userId" hona chahiye
        ref.orderByChild("userId").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear();

                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        OrderModel model = ds.getValue(OrderModel.class);
                        if (model != null) {
                            orderList.add(model);
                        }
                    }

                    // Naye orders ko list mein sabse upar dikhane ke liye reverse karein
                    Collections.reverse(orderList);
                } else {
                    Toast.makeText(MyOrdersActivity.this, "No orders found.", Toast.LENGTH_SHORT).show();
                }

                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MyOrdersActivity.this, "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}