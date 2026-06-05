package com.example.onlinefoodordering.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.onlinefoodordering.R;
import com.example.onlinefoodordering.model.OrderModel;
import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    ArrayList<OrderModel> list;
    Context context;

    public OrderAdapter(ArrayList<OrderModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // YAHAN CHANGE KIYA: Naya item_order layout use ho raha hai
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderModel model = list.get(position);

        // 1. Order ID aur Date set karna
        holder.orderDate.setText(model.getOrderDate()); // Ensure model has getOrderDate()
        holder.orderItemsNames.setText("Order #" + model.getOrderId());

        // 2. Price aur Status
        holder.orderTotalPrice.setText("Total: ₹" + model.getTotalAmount());
        holder.orderStatus.setText(model.getStatus());

        // 3. Status Color Logic (Visual Improvement)
        if ("Delivered".equalsIgnoreCase(model.getStatus())) {
            holder.orderStatus.setTextColor(Color.parseColor("#2E7D32")); // Green
            holder.orderStatus.setBackgroundResource(R.drawable.circle1_bg); // Agar green bg hai toh
        } else {
            holder.orderStatus.setTextColor(Color.parseColor("#FF7043")); // Orange
        }

        // 4. Image load karna
        Glide.with(context)
                .load(model.getFoodImage()) // Ensure model has getFoodImage()
                .placeholder(R.drawable.burger)
                .into(holder.orderImg);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        ImageView orderImg;
        TextView orderDate, orderStatus, orderItemsNames, orderTotalPrice;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            // YAHAN CHANGE KIYA: Naye layout ki IDs match kar di hain
            orderImg = itemView.findViewById(R.id.orderImg);
            orderDate = itemView.findViewById(R.id.orderDate);
            orderStatus = itemView.findViewById(R.id.orderStatus);
            orderItemsNames = itemView.findViewById(R.id.orderItemsNames);
            orderTotalPrice = itemView.findViewById(R.id.orderTotalPrice);

        }
    }
}