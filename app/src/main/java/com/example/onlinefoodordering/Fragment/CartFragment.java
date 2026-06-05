package com.example.onlinefoodordering.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinefoodordering.ui.AddressActivity;
import com.example.onlinefoodordering.R;
import com.example.onlinefoodordering.adapter.CartAdapter;
import com.example.onlinefoodordering.model.CartModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CartFragment extends Fragment {

    RecyclerView recyclerView;
    CartAdapter adapter;
    ArrayList<CartModel> list;
    TextView totalPriceTxt, subtotalPrice, totalAmountFinal;
    Button btnProceed;
    ImageView backBtn;
    DatabaseReference databaseReference;
    String currentUserId;
    long finalTotalBill = 0;
    String databaseUrl = "https://onlinefoodorderingapp-7c2c0-default-rtdb.europe-west1.firebasedatabase.app/";

    public CartFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_cart, container, false);

        // Views Initialization
        recyclerView = view.findViewById(R.id.cartRecyclerView);
        totalPriceTxt = view.findViewById(R.id.totalPriceTxt);
        btnProceed = view.findViewById(R.id.btnProceed);
        subtotalPrice = view.findViewById(R.id.subtotalPrice);
        totalAmountFinal = view.findViewById(R.id.totalAmountFinal);
        backBtn = view.findViewById(R.id.backBtn);

        // --- BACK BUTTON LOGIC (FOR NAVIGATION SYNC) ---
        backBtn.setOnClickListener(v -> {
            if (getActivity() != null) {
                BottomNavigationView nav = getActivity().findViewById(R.id.bottomNav);
                nav.setSelectedItemId(R.id.nav_delivery); // Home tab par wapas le jayega
            }
        });

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            databaseReference = FirebaseDatabase.getInstance(databaseUrl).getReference().child("Cart").child(currentUserId);
            list = new ArrayList<>();
            adapter = new CartAdapter(list, getContext(), this::calculateTotalBill);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);
            fetchCartData();
        }

        btnProceed.setOnClickListener(v -> {
            if (list != null && list.size() > 0) {
                Intent intent = new Intent(getContext(), AddressActivity.class);
                intent.putExtra("totalAmount", String.valueOf(finalTotalBill));
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "Aapka cart khali hai!", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void fetchCartData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    CartModel model = data.getValue(CartModel.class);
                    if (model != null) list.add(model);
                }
                adapter.notifyDataSetChanged();
                calculateTotalBill();
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void calculateTotalBill() {
        finalTotalBill = 0;
        for (CartModel model : list) {
            finalTotalBill += (model.getFoodPrice() * model.getQuantity());
        }
        String totalStr = "₹" + finalTotalBill;
        totalPriceTxt.setText(totalStr);
        if (subtotalPrice != null) subtotalPrice.setText(totalStr);
        if (totalAmountFinal != null) totalAmountFinal.setText(totalStr);
    }
}