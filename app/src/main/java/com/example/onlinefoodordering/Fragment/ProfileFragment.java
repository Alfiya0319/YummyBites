package com.example.onlinefoodordering.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.onlinefoodordering.R;
import com.example.onlinefoodordering.ui.EditProfileActivity;
import com.example.onlinefoodordering.ui.LoginActivity;
import com.example.onlinefoodordering.ui.MyOrdersActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    TextView profileName, profileNumber;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    public ProfileFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profileName = view.findViewById(R.id.txt_user_name);
        profileNumber = view.findViewById(R.id.txt_user_number);
        LinearLayout btnLogout = view.findViewById(R.id.btn_logout);
        LinearLayout btnEditProfile = view.findViewById(R.id.btn_profile_details);
        LinearLayout btnMyOrders = view.findViewById(R.id.btn_my_orders);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            // URL ke sath initialize karein fast connection ke liye
            mDatabase = FirebaseDatabase.getInstance("https://onlinefoodorderingapp-7c2c0-default-rtdb.europe-west1.firebasedatabase.app")
                    .getReference().child("Users").child(userId);

            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // Safety check: Fragment attach hona chahiye
                    if (!isAdded() || getActivity() == null) return;

                    if (snapshot.exists()) {
                        String name = snapshot.child("name").getValue(String.class);
                        String number = snapshot.child("number").getValue(String.class);

                        if (name != null) profileName.setText(name);
                        if (number != null) profileNumber.setText("+91 " + number);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // 🛠️ FIX: Yahan crash ho raha tha.
                    // Toast tabhi dikhayein agar fragment attached hai.
                    if (isAdded() && getActivity() != null) {
                        Toast.makeText(getActivity(), "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        if (btnMyOrders != null) {
            btnMyOrders.setOnClickListener(v -> {
                if (getActivity() != null) {
                    Intent intent = new Intent(getActivity(), MyOrdersActivity.class);
                    startActivity(intent);
                }
            });
        }

        if (btnEditProfile != null) {
            btnEditProfile.setOnClickListener(v -> {
                if (getActivity() != null) {
                    Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                    startActivity(intent);
                }
            });
        }

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> showLogoutConfirmation());
        }

        return view;
    }

    private void showLogoutConfirmation() {
        if (getContext() == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");

        builder.setPositiveButton("Logout", (dialog, which) -> {
            FirebaseAuth.getInstance().signOut();
            if (getActivity() != null) {
                SharedPreferences sp = getActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                sp.edit().putBoolean("isLoggedIn", false).apply();

                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}