package com.example.onlinefoodordering.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinefoodordering.Fragment.CartFragment;
import com.example.onlinefoodordering.Fragment.ProfileFragment;
import com.example.onlinefoodordering.Fragment.WalletFragment;
import com.example.onlinefoodordering.R;
import com.example.onlinefoodordering.adapter.FoodAdapter;
import com.example.onlinefoodordering.model.FoodModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CategoryDetailsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FoodAdapter adapter;
    private ArrayList<FoodModel> list;
    private String selectedCategory;
    private TextView titleTxt;
    private ImageView backBtn;
    private BottomNavigationView bottomNav;
    private FrameLayout fragmentContainer; // Container variable

    private final String DB_URL = "https://onlinefoodorderingapp-7c2c0-default-rtdb.europe-west1.firebasedatabase.app/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_details);

        selectedCategory = getIntent().getStringExtra("CATEGORY_NAME");

        initViews();

        titleTxt.setText(selectedCategory);
        backBtn.setOnClickListener(v -> finish());

        setupRecyclerView();
        setupBottomNavigation();
        loadFilteredFoods();
    }

    private void initViews() {
        titleTxt = findViewById(R.id.categoryTitle);
        backBtn = findViewById(R.id.backBtn);
        recyclerView = findViewById(R.id.categoryRecyclerView);
        bottomNav = findViewById(R.id.bottomNav);
        fragmentContainer = findViewById(R.id.category_fragment_container); // Link container
    }

    private void setupBottomNavigation() {
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_delivery) {
                // Agar fragment khula hai toh band karo, warna Home par jao
                if (fragmentContainer.getVisibility() == View.VISIBLE) {
                    fragmentContainer.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    titleTxt.setText(selectedCategory);
                } else {
                    finish();
                }
                return true;
            } else if (id == R.id.nav_cart) {
                showFragment(new CartFragment(), "Cart");
                return true;
            } else if (id == R.id.nav_dining) {
                Toast.makeText(this, "Dining Coming Soon!", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.nav_money) {
                showFragment(new WalletFragment(), "My Wallet");
                return true;
            }
            return false;
        });
    }

    private void showFragment(Fragment fragment, String title) {
        recyclerView.setVisibility(View.GONE);
        fragmentContainer.setVisibility(View.VISIBLE);
        titleTxt.setText(title);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.category_fragment_container, fragment)
                .commit();
    }

    private void setupRecyclerView() {
        list = new ArrayList<>();
        adapter = new FoodAdapter(list, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadFilteredFoods() {
        DatabaseReference ref = FirebaseDatabase.getInstance(DB_URL).getReference().child("Foods");
        Query query = ref.orderByChild("category").equalTo(selectedCategory);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    FoodModel model = data.getValue(FoodModel.class);
                    if (model != null) list.add(model);
                }
                adapter.notifyDataSetChanged();
                if (list.isEmpty()) {
                    Toast.makeText(CategoryDetailsActivity.this, "No items in " + selectedCategory, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}