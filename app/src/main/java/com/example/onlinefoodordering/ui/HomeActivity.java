package com.example.onlinefoodordering.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinefoodordering.Fragment.CartFragment;
import com.example.onlinefoodordering.Fragment.DiningFragment;
import com.example.onlinefoodordering.Fragment.ProfileFragment;
import com.example.onlinefoodordering.Fragment.WalletFragment;
import com.example.onlinefoodordering.R;
import com.example.onlinefoodordering.adapter.FoodAdapter;
import com.example.onlinefoodordering.model.FoodModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private FrameLayout fragmentContainer;
    private NestedScrollView homeScrollView;
    private BottomNavigationView bottomNav;
    private ImageView profileMenu;
    private EditText searchEditText;
    private RecyclerView homeRecyclerView;
    private FoodAdapter foodAdapter;
    private ArrayList<FoodModel> foodList;
    private DatabaseReference databaseReference;
    private FusedLocationProviderClient fusedLocationClient;
    private TextView currentLocationText, homeWalletBalance;
    private final String DB_URL = "https://onlinefoodorderingapp-7c2c0-default-rtdb.europe-west1.firebasedatabase.app/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initViews();
        setupRecyclerView();
        databaseReference = FirebaseDatabase.getInstance(DB_URL).getReference().child("Foods");
        loadFoodItems();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();
        setupSearchLogic();
        initClickListeners();
        setupCategoryClicks();
        syncWalletBalance();
        handleBackNavigation();
    }

    private void initViews() {
        fragmentContainer = findViewById(R.id.fragment_container);
        homeScrollView = findViewById(R.id.homeScrollView);
        bottomNav = findViewById(R.id.bottomNav);
        profileMenu = findViewById(R.id.profileMenu);
        homeRecyclerView = findViewById(R.id.homeRecyclerView);
        currentLocationText = findViewById(R.id.currentLocationText);
        searchEditText = findViewById(R.id.searchEditText);
        homeWalletBalance = findViewById(R.id.home_wallet_balance);
    }

    private void syncWalletBalance() {
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId != null) {
            DatabaseReference walletRef = FirebaseDatabase.getInstance(DB_URL)
                    .getReference().child("Users").child(userId).child("walletBalance");
            walletRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) homeWalletBalance.setText("₹" + snapshot.getValue().toString());
                    else homeWalletBalance.setText("₹0");
                }
                @Override public void onCancelled(@NonNull DatabaseError error) {}
            });
        }
    }

    private void initClickListeners() {
        profileMenu.setOnClickListener(v -> showFragment(new ProfileFragment()));

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_delivery) {
                hideFragmentContainer();
                return true;
            } else if (id == R.id.nav_cart) {
                showFragment(new CartFragment());
                return true;
            } else if (id == R.id.nav_dining) {
                showFragment(new DiningFragment());
                return true;
            } else if (id == R.id.nav_money) {
                showFragment(new WalletFragment());
                return true;
            }
            return false;
        });
    }

    public void showFragment(Fragment fragment) {
        homeScrollView.setVisibility(View.GONE);
        fragmentContainer.setVisibility(View.VISIBLE);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    public void hideFragmentContainer() {
        fragmentContainer.setVisibility(View.GONE);
        homeScrollView.setVisibility(View.VISIBLE);
        bottomNav.getMenu().findItem(R.id.nav_delivery).setChecked(true);
    }

    private void handleBackNavigation() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (fragmentContainer.getVisibility() == View.VISIBLE) {
                    hideFragmentContainer();
                } else {
                    finish();
                }
            }
        });
    }

    // ... setupRecyclerView, loadFoodItems, getLastLocation, setupSearchLogic (same as your code) ...
    private void setupRecyclerView() { foodList = new ArrayList<>(); foodAdapter = new FoodAdapter(foodList, this); homeRecyclerView.setLayoutManager(new LinearLayoutManager(this)); homeRecyclerView.setAdapter(foodAdapter); }
    private void setupSearchLogic() { searchEditText.addTextChangedListener(new TextWatcher() { @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {} @Override public void onTextChanged(CharSequence s, int start, int before, int count) { filter(s.toString()); } @Override public void afterTextChanged(Editable s) {} }); }
    private void filter(String text) { ArrayList<FoodModel> filteredList = new ArrayList<>(); for (FoodModel item : foodList) { if (item.getFoodName().toLowerCase().contains(text.toLowerCase())) filteredList.add(item); } foodAdapter.filterList(filteredList); }
    private void loadFoodItems() { databaseReference.addValueEventListener(new ValueEventListener() { @Override public void onDataChange(@NonNull DataSnapshot snapshot) { foodList.clear(); for (DataSnapshot data : snapshot.getChildren()) { FoodModel model = data.getValue(FoodModel.class); if (model != null) foodList.add(model); } foodAdapter.notifyDataSetChanged(); } @Override public void onCancelled(@NonNull DatabaseError error) {} }); }
    private void getLastLocation() { if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) { fusedLocationClient.getLastLocation().addOnSuccessListener(location -> { if (location != null) { try { Geocoder geocoder = new Geocoder(this, Locale.getDefault()); List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); if (addresses != null && !addresses.isEmpty()) currentLocationText.setText(addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea()); } catch (Exception e) { currentLocationText.setText("Location error"); } } }); } else { ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100); } }
    private void setupCategoryClicks() { findViewById(R.id.catBurger).setOnClickListener(v -> openCategoryPage("Burger")); findViewById(R.id.catSalad).setOnClickListener(v -> openCategoryPage("Indian Cuisine")); findViewById(R.id.catPizza).setOnClickListener(v -> openCategoryPage("Pizza")); findViewById(R.id.catDessert).setOnClickListener(v -> openCategoryPage("Dessert")); findViewById(R.id.catCoffee).setOnClickListener(v -> openCategoryPage("Coffee")); findViewById(R.id.wallet_display_layout).setOnClickListener(v -> showFragment(new WalletFragment())); }
    private void openCategoryPage(String categoryName) { Intent intent = new Intent(HomeActivity.this, CategoryDetailsActivity.class); intent.putExtra("CATEGORY_NAME", categoryName); startActivity(intent); }
}