package com.example.onlinefoodordering.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import com.example.onlinefoodordering.R;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AddressActivity extends AppCompatActivity {

    EditText nameEt, phoneEt, addressEt;
    TextView locationBtn;
    ImageView backBtn;
    Button proceedBtn;

    FusedLocationProviderClient fusedLocationClient;
    FirebaseAuth auth;
    FirebaseDatabase database;
    String dbUrl = "https://onlinefoodorderingapp-7c2c0-default-rtdb.europe-west1.firebasedatabase.app/";
    String totalAmount; // Intent se amount lene ke liye

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        // --- SABSE PEHLE INITIALIZE KAREIN ---
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance(dbUrl);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Cart se total amount receive karein
        totalAmount = getIntent().getStringExtra("totalAmount");

        // Views Initialization
        nameEt = findViewById(R.id.userName);
        phoneEt = findViewById(R.id.userPhone);
        addressEt = findViewById(R.id.userAddress);
        locationBtn = findViewById(R.id.currentLocationBtn);
        proceedBtn = findViewById(R.id.proceedToPayBtn);
        backBtn = findViewById(R.id.backBtn);

        // --- AB AUTH USE KAREIN ---
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Auto-fill registered Name
            String registeredName = currentUser.getDisplayName();
            if (registeredName != null && !registeredName.isEmpty()) {
                nameEt.setText(registeredName);
            }

            // Database se purana address nikalna
            DatabaseReference userRef = database.getReference().child("Users").child(userId).child("Address");
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        nameEt.setText(snapshot.child("receiverName").getValue(String.class));
                        phoneEt.setText(snapshot.child("receiverPhone").getValue(String.class));
                        addressEt.setText(snapshot.child("receiverAddress").getValue(String.class));
                    }
                }
                @Override public void onCancelled(@NonNull DatabaseError error) {}
            });
        }

        // Click Listeners
        backBtn.setOnClickListener(v -> finish());
        locationBtn.setOnClickListener(v -> getLastLocation());
        proceedBtn.setOnClickListener(v -> {
            String name = nameEt.getText().toString().trim();
            String phone = phoneEt.getText().toString().trim();
            String address = addressEt.getText().toString().trim();

            if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                Toast.makeText(this, "Please fill all details", Toast.LENGTH_SHORT).show();
            } else {
                saveAddressToFirebase(name, phone, address);
            }
        });
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    Geocoder geocoder = new Geocoder(AddressActivity.this, Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        if (addresses != null && !addresses.isEmpty()) {
                            addressEt.setText(addresses.get(0).getAddressLine(0));
                        }
                    } catch (IOException e) {
                        Toast.makeText(this, "Location error", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Please turn on GPS", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    private void saveAddressToFirebase(String name, String phone, String address) {
        String userId = auth.getCurrentUser().getUid();
        DatabaseReference ref = database.getReference().child("Users").child(userId).child("Address");

        HashMap<String, Object> addressMap = new HashMap<>();
        addressMap.put("receiverName", name);
        addressMap.put("receiverPhone", phone);
        addressMap.put("receiverAddress", address);

        ref.setValue(addressMap).addOnSuccessListener(unused -> {
            // Address save hone ke baad PaymentActivity par bhejien
            Intent intent = new Intent(AddressActivity.this, PaymentActivity.class);
            intent.putExtra("totalAmount", totalAmount);
            intent.putExtra("address", address); // Track order page ke liye address pass karein
            startActivity(intent);
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 44 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLastLocation();
        }
    }
}