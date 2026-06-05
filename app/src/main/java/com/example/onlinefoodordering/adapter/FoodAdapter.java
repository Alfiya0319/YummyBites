package com.example.onlinefoodordering.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.onlinefoodordering.R;
import com.example.onlinefoodordering.model.CartModel;
import com.example.onlinefoodordering.model.FoodModel;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {

    ArrayList<FoodModel> list;
    Context context;
    String databaseUrl = "https://onlinefoodorderingapp-7c2c0-default-rtdb.europe-west1.firebasedatabase.app/";

    public FoodAdapter(ArrayList<FoodModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public void filterList(ArrayList<FoodModel> filteredList) {
        this.list = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_food, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodModel model = list.get(position);

        holder.name.setText(model.getFoodName());
        holder.price.setText("₹" + model.getFoodPrice());
        holder.resName.setText(model.getResName());

        // 🖼️ Glide fixed: Refresh images instantly
        Glide.with(context)
                .load(model.getFoodImage())
                .placeholder(R.drawable.burger)
                .error(R.drawable.burger)
                .skipMemoryCache(false) // <--- Isse memory mein save hoga
                .diskCacheStrategy(DiskCacheStrategy.ALL) // <--- Isse storage mein save hoga (Offline ke liye zaroori)
                .into(holder.image);

        // 🛒 Add to Cart Click Logic
        holder.addBtn.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                DatabaseReference cartRef = FirebaseDatabase.getInstance(databaseUrl)
                        .getReference().child("Cart")
                        .child(user.getUid());

                // Cart mein store karne ke liye naya object
                CartModel cartItem = new CartModel(
                        model.getFoodName(),
                        model.getFoodPrice(),
                        model.getFoodImage(),
                        1 // default quantity
                );

                cartRef.child(model.getFoodName()).setValue(cartItem)
                        .addOnSuccessListener(unused -> Toast.makeText(context, "Added to Cart!", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(context, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(context, "Please Login First!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() { return list.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, price, resName;
        MaterialButton addBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.foodImg);
            name = itemView.findViewById(R.id.foodName);
            price = itemView.findViewById(R.id.foodPrice);
            resName = itemView.findViewById(R.id.resName);
            addBtn = itemView.findViewById(R.id.addBtn);
        }
    }
}