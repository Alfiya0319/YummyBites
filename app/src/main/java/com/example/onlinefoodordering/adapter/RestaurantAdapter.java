package com.example.onlinefoodordering.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.onlinefoodordering.R;
import com.example.onlinefoodordering.model.DiningModel;
import com.example.onlinefoodordering.ui.RestaurantDetailsActivity;

import java.util.ArrayList;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {

    ArrayList<DiningModel> list;
    Context context;

    public RestaurantAdapter(ArrayList<DiningModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.restaurant_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DiningModel model = list.get(position);

        holder.name.setText(model.getRestaurantName());
        holder.address.setText(model.getRestaurantAddress());
        holder.rating.setText(String.valueOf(model.getDiningRating()));
        holder.description.setText(model.getDescription());

        Glide.with(context)
                .load(model.getRestaurantImage())
                .placeholder(R.drawable.food_logo2)
                .error(R.drawable.ic_sccs)
                .into(holder.image);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RestaurantDetailsActivity.class);
            intent.putExtra("restaurantName", model.getRestaurantName());
            intent.putExtra("restaurantAddress", model.getRestaurantAddress());
            intent.putExtra("cuisines", model.getCuisines());
            intent.putExtra("openingHours", model.getOpeningHours());
            intent.putExtra("restaurantImage", model.getRestaurantImage());
            intent.putExtra("diningRating", (float) model.getDiningRating());
            intent.putExtra("diningReviewCount", model.getDiningReviewCount());
            intent.putExtra("deliveryRating", (float) model.getDeliveryRating());
            intent.putExtra("deliveryReviewCount", model.getDeliveryReviewCount());
            intent.putExtra("description", model.getDescription());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, address, rating, description;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.restaurant_image);
            name = itemView.findViewById(R.id.restaurant_name);
            address = itemView.findViewById(R.id.restaurant_address);
            rating = itemView.findViewById(R.id.restaurant_rating);
            description = itemView.findViewById(R.id.restaurant_description);
        }
    }
}
