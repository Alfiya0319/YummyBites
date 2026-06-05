package com.example.onlinefoodordering.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast; // Add this
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.onlinefoodordering.R;
import com.example.onlinefoodordering.model.CartModel;
import com.google.firebase.auth.FirebaseAuth; // Add this
import com.google.firebase.database.DatabaseReference; // Add this
import com.google.firebase.database.FirebaseDatabase; // Add this
import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    ArrayList<CartModel> list;
    Context context;
    private OnQuantityChangeListener onQuantityChangeListener;
    String dbUrl = "https://onlinefoodorderingapp-7c2c0-default-rtdb.europe-west1.firebasedatabase.app/";

    public interface OnQuantityChangeListener {
        void onQuantityChanged();
    }

    public CartAdapter(ArrayList<CartModel> list, Context context, OnQuantityChangeListener listener) {
        this.list = list;
        this.context = context;
        this.onQuantityChangeListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartModel model = list.get(position);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference itemRef = FirebaseDatabase.getInstance(dbUrl).getReference()
                .child("Cart").child(userId).child(model.getFoodName());

        holder.name.setText(model.getFoodName());
        holder.price.setText("₹" + (model.getFoodPrice() * model.getQuantity()));
        holder.quantity.setText(String.valueOf(model.getQuantity()));

        Glide.with(context).load(model.getFoodImage()).into(holder.image);

        // --- Plus Button (With Firebase Update) ---
        holder.plusBtn.setOnClickListener(v -> {
            int qty = model.getQuantity();
            qty++;
            model.setQuantity(qty);
            itemRef.child("quantity").setValue(qty); // Firebase update
            notifyItemChanged(position);
            if (onQuantityChangeListener != null) onQuantityChangeListener.onQuantityChanged();
        });

        // --- Minus Button (With Firebase Update) ---
        holder.minusBtn.setOnClickListener(v -> {
            int qty = model.getQuantity();
            if (qty > 1) {
                qty--;
                model.setQuantity(qty);
                itemRef.child("quantity").setValue(qty); // Firebase update
                notifyItemChanged(position);
                if (onQuantityChangeListener != null) onQuantityChangeListener.onQuantityChanged();
            }
        });

        // --- Delete Logic (Long Click) ---
        holder.itemView.setOnLongClickListener(v -> {
            itemRef.removeValue().addOnSuccessListener(unused -> {
                Toast.makeText(context, model.getFoodName() + " removed", Toast.LENGTH_SHORT).show();
                // Item hatne ke baad list update karna zaroori hai
                if (position < list.size()) {
                    list.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, list.size());
                    if (onQuantityChangeListener != null) onQuantityChangeListener.onQuantityChanged();
                }
            });
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, price, quantity, plusBtn, minusBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.cartItemImg);
            name = itemView.findViewById(R.id.cartItemName);
            price = itemView.findViewById(R.id.cartItemPrice);
            quantity = itemView.findViewById(R.id.cartItemQty);
            plusBtn = itemView.findViewById(R.id.plusBtn);
            minusBtn = itemView.findViewById(R.id.minusBtn);
        }
    }
}