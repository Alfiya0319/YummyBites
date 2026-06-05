package com.example.onlinefoodordering.ui;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.onlinefoodordering.R;

public class RestaurantDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_details);

        ImageView restaurantImage = findViewById(R.id.restaurant_image);
        TextView restaurantName = findViewById(R.id.restaurant_name);
        TextView restaurantAddress = findViewById(R.id.restaurant_address);
        TextView cuisines = findViewById(R.id.cuisines);
        TextView openingHours = findViewById(R.id.opening_hours);
        TextView diningRating = findViewById(R.id.dining_rating);
        TextView diningReviewCount = findViewById(R.id.dining_review_count);
        TextView deliveryRating = findViewById(R.id.delivery_rating);
        TextView deliveryReviewCount = findViewById(R.id.delivery_review_count);
        TextView description = findViewById(R.id.description);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            restaurantName.setText(extras.getString("restaurantName"));
            restaurantAddress.setText(extras.getString("restaurantAddress"));
            cuisines.setText(extras.getString("cuisines"));
            openingHours.setText(extras.getString("openingHours"));
            diningRating.setText(String.valueOf(extras.getFloat("diningRating")));
            diningReviewCount.setText(extras.getString("diningReviewCount"));
            deliveryRating.setText(String.valueOf(extras.getFloat("deliveryRating")));
            deliveryReviewCount.setText(extras.getString("deliveryReviewCount"));
            description.setText(extras.getString("description"));

            Glide.with(this)
                    .load(extras.getInt("restaurantImage"))
                    .into(restaurantImage);
        }
    }
}

