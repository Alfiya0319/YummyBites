package com.example.onlinefoodordering.Fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.onlinefoodordering.R;
import com.example.onlinefoodordering.adapter.RestaurantAdapter;
import com.example.onlinefoodordering.model.DiningModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.RangeSlider;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DiningFragment extends Fragment {

    private RecyclerView diningRecyclerView;
    private RestaurantAdapter restaurantAdapter;
    private ArrayList<DiningModel> diningList;
    private ArrayList<DiningModel> filteredList;
    private ImageView filterIcon;
    private AutoCompleteTextView searchBar;
    private CardView filterView;
    private ChipGroup categoryChipGroup, ratingChipGroup;
    private RangeSlider priceSlider;
    private Button showResultsButton;
    private TextView resetButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dining, container, false);

        // Initialize views
        diningRecyclerView = view.findViewById(R.id.diningRecyclerView);
        filterIcon = view.findViewById(R.id.filter_icon);
        searchBar = view.findViewById(R.id.search_bar);
        filterView = view.findViewById(R.id.filter_view);
        categoryChipGroup = view.findViewById(R.id.category_chip_group);
        ratingChipGroup = view.findViewById(R.id.rating_chip_group);
        priceSlider = view.findViewById(R.id.price_slider);
        showResultsButton = view.findViewById(R.id.show_results_button);
        resetButton = view.findViewById(R.id.reset_button);
        diningList = new ArrayList<>();
        filteredList = new ArrayList<>();

        // Setup Adapter
        restaurantAdapter = new RestaurantAdapter(filteredList, getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        diningRecyclerView.setLayoutManager(layoutManager);
        diningRecyclerView.setAdapter(restaurantAdapter);

        // Load static data
        loadStaticRestaurantData();

        // Setup search bar
        setupSearch();

        // Set click listeners
        filterIcon.setOnClickListener(v -> toggleFilterView());
        showResultsButton.setOnClickListener(v -> applyFilters());
        resetButton.setOnClickListener(v -> resetFilters());

        // Setup category chips
        setupCategoryChips();

        return view;
    }

    private void setupSearch() {
        List<String> restaurantNames = new ArrayList<>();
        for (DiningModel model : diningList) {
            restaurantNames.add(model.getRestaurantName());
        }
        ArrayAdapter<String> searchAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, restaurantNames);
        searchBar.setAdapter(searchAdapter);

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterRestaurantsByName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        searchBar.setOnItemClickListener((parent, view, position, id) -> {
            String selectedName = (String) parent.getItemAtPosition(position);
            filterRestaurantsByName(selectedName);
        });
    }

    private void filterRestaurantsByName(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(diningList);
        } else {
            for (DiningModel model : diningList) {
                if (model.getRestaurantName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(model);
                }
            }
        }
        restaurantAdapter.notifyDataSetChanged();
    }

    private void setupCategoryChips() {
        Set<String> categories = new HashSet<>();
        for (DiningModel model : diningList) {
            categories.add(model.getCategory());
        }

        categoryChipGroup.removeAllViews();
        for (String category : categories) {
            Chip chip = new Chip(getContext());
            chip.setText(category);
            chip.setCheckable(true);
            chip.setClickable(true);
            categoryChipGroup.addView(chip);
        }
    }

    private void toggleFilterView() {
        if (filterView.getVisibility() == View.VISIBLE) {
            filterView.setVisibility(View.GONE);
        } else {
            filterView.setVisibility(View.VISIBLE);
        }
    }

    private void applyFilters() {
        List<String> selectedCategories = new ArrayList<>();
        for (int i = 0; i < categoryChipGroup.getChildCount(); i++) {
            Chip chip = (Chip) categoryChipGroup.getChildAt(i);
            if (chip.isChecked()) {
                selectedCategories.add(chip.getText().toString());
            }
        }

        float selectedRating = 0;
        int checkedChipId = ratingChipGroup.getCheckedChipId();
        if (checkedChipId != View.NO_ID) {
            Chip selectedChip = getView().findViewById(checkedChipId);
            selectedRating = Float.parseFloat(selectedChip.getText().toString().replace("+", ""));
        }

        List<Float> values = priceSlider.getValues();
        float minPrice = values.get(0);
        float maxPrice = values.get(1);

        filteredList.clear();
        for (DiningModel model : diningList) {
            boolean categoryMatch = selectedCategories.isEmpty() || selectedCategories.contains(model.getCategory());
            boolean ratingMatch = model.getDiningRating() >= selectedRating;
            boolean priceMatch = model.getRestaurantPrice() >= minPrice && model.getRestaurantPrice() <= maxPrice;

            if (categoryMatch && ratingMatch && priceMatch) {
                filteredList.add(model);
            }
        }
        restaurantAdapter.notifyDataSetChanged();
        toggleFilterView(); // Hide filter view after applying
    }

    private void resetFilters() {
        categoryChipGroup.clearCheck();
        ratingChipGroup.clearCheck();
        priceSlider.setValues(priceSlider.getValueFrom(), priceSlider.getValueTo());
        filteredList.clear();
        filteredList.addAll(diningList);
        restaurantAdapter.notifyDataSetChanged();
        toggleFilterView(); // Hide filter view after resetting
    }

    private void loadStaticRestaurantData() {
        diningList.clear();
        diningList.add(new DiningModel("Osteria Francescana", "2707 Indian Creek Dr, Miami Beach, FL", 4.8, R.drawable.rest_1, 120, "Italian", "Osteria Francescana is a world-renowned restaurant located in Modena, Italy."));
        diningList.add(new DiningModel("Yardbird Table & Bar", "1600 Lenox Ave, Miami Beach, FL 33139", 4.5, R.drawable.rest_2, 80, "American", "A popular spot for Southern comfort food and craft cocktails."));
        diningList.add(new DiningModel("Bodega Taqueria y Tequila", "1220 16th St, Miami Beach, FL 33139", 4.5, R.drawable.rest_3, 50, "Mexican", "Lively taqueria with a speakeasy-style lounge in the back."));
        diningList.add(new DiningModel("Broken Shaker at Freehand", "2727 Indian Creek Dr, Miami Beach, FL", 4.3, R.drawable.rest_4, 60, "Bar", "Award-winning cocktail bar with a backyard oasis vibe."));
        diningList.add(new DiningModel("MLA Restaurant", "1628 Meridian Ave, Rooftop, Miami Be...", 4.1, R.drawable.rest_5, 90, "Modern", "Rooftop restaurant with panoramic views and modern American cuisine."));
        diningList.add(new DiningModel("The Bazaar by José Andrés", "1701 Collins Ave, Miami Beach, FL 33139", 4.6, R.drawable.rest_6, 150, "Spanish", "A whimsical dining experience with a modern take on Spanish tapas."));
        diningList.add(new DiningModel("Prime 112", "112 Ocean Dr, Miami Beach, FL 33139", 4.4, R.drawable.rest_7, 200, "Steakhouse", "A classic steakhouse known for its high-quality cuts and celebrity clientele."));
        diningList.add(new DiningModel("Joe's Stone Crab", "11 Washington Ave, Miami Beach, FL 33139", 4.7, R.drawable.rest_8, 180, "Seafood", "A Miami institution famous for its stone crabs and key lime pie."));
        diningList.add(new DiningModel("Nobu Miami", "4525 Collins Ave, Miami Beach, FL 33140", 4.5, R.drawable.rest_9, 250, "Japanese", "World-renowned Japanese restaurant with a stunning oceanfront setting."));
        diningList.add(new DiningModel("Hakkasan", "4441 Collins Ave, Miami Beach, FL 33140", 4.3, R.drawable.rest_10, 220, "Chinese", "High-end Chinese cuisine in a stylish and sophisticated atmosphere."));
        diningList.add(new DiningModel("Scarpetta by Scott Conant", "4441 Collins Ave, Miami Beach, FL 33140", 4.2, R.drawable.rest_11, 190, "Italian", "Elegant Italian restaurant with a focus on fresh, seasonal ingredients."));
        diningList.add(new DiningModel("Lure Fishbar", "1600 Collins Ave, Miami Beach, FL 33139", 4.1, R.drawable.rest_12, 170, "Seafood", "A sophisticated seafood restaurant with a yacht-inspired design."));
        diningList.add(new DiningModel("Macchialina", "820 Alton Rd, Miami Beach, FL 33139", 4.6, R.drawable.rest_13, 130, "Italian", "Cozy, rustic Italian eatery with a focus on handmade pastas."));
        diningList.add(new DiningModel("Byblos Miami", "1545 Collins Ave, Miami Beach, FL 33139", 4.4, R.drawable.rest_14, 160, "Mediterranean", "A vibrant and stylish restaurant serving Eastern Mediterranean cuisine."));
        filteredList.addAll(diningList);
        restaurantAdapter.notifyDataSetChanged();
    }
}
