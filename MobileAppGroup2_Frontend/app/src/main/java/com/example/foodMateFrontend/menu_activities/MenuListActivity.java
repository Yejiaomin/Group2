package com.example.foodMateFrontend.menu_activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodMateFrontend.R;
import com.example.foodMateFrontend.RetrofitClient;

import java.util.List;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.Window;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ImageButton;

import com.example.foodMateFrontend.combo_activities.ComboListActivity;
import com.example.foodMateFrontend.profile_activities.ProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MenuListActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    private RecyclerView recyclerView;
    private MenuAdapter menuAdapter;
    private FloatingActionButton fabAdd;
    private Dialog addMenuDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_list);

//        recyclerView = findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_menus);

        // 设置监听器
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        fabAdd = findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddMenuDialog();
            }
        });

//        fetchMenus();

        setupExpandableMenus();
        fetchMenuItemsByCategory();
    }

//    private void fetchMenus() {
//        MenuItemApiService apiService = RetrofitClient.getRetrofitInstance().create(MenuItemApiService.class);
//        apiService.getAllMenuItems().enqueue(new retrofit2.Callback<List<MenuItem>>() {
//            @Override
//            public void onResponse(retrofit2.Call<List<MenuItem>> call, retrofit2.Response<List<MenuItem>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    menuAdapter = new MenuAdapter(response.body());
//                    recyclerView.setAdapter(menuAdapter);
//                } else {
//                    showErrorMessage("Failed to load menu items");
//                }
//            }
//
//            @Override
//            public void onFailure(retrofit2.Call<List<MenuItem>> call, Throwable t) {
//                showErrorMessage("Network error: " + t.getMessage());
//            }
//        });
//    }

    private void showErrorMessage(String message) {
        Toast.makeText(MenuListActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void showAddMenuDialog() {
        addMenuDialog = new Dialog(MenuListActivity.this);
        addMenuDialog.setContentView(R.layout.dialog_add_menu_item);

        // Set dialog window attributes
        Window window = addMenuDialog.getWindow();
        if (window != null) {
            window.setGravity(Gravity.CENTER);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // Initialize close button
        ImageButton closeButton = addMenuDialog.findViewById(R.id.close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMenuDialog.dismiss();
            }
        });

        // Initialize dialog views
        Spinner categorySpinner = addMenuDialog.findViewById(R.id.menu_category_spinner);
        EditText itemNameInput = addMenuDialog.findViewById(R.id.item_name_input);
        EditText itemPriceInput = addMenuDialog.findViewById(R.id.item_price_input);
        Button addItemButton = addMenuDialog.findViewById(R.id.add_item_button);

        // Handle add item button click
        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String category = categorySpinner.getSelectedItem().toString();
                String itemName = itemNameInput.getText().toString();
                String priceStr = itemPriceInput.getText().toString();

                if (itemName.isEmpty() || priceStr.isEmpty()) {
                    showErrorMessage("Please fill in all fields");
                    return;
                }

                try {
                    double price = Double.parseDouble(priceStr);
                    MenuItem newItem = new MenuItem();
                    newItem.setName(itemName);
                    newItem.setPrice(price);
                    newItem.setCategory(category);

                    // Add item to the menu (you'll need to implement this API call)
                    addMenuItem(newItem);
                    
                    // Close dialog
                    addMenuDialog.dismiss();
                } catch (NumberFormatException e) {
                    showErrorMessage("Please enter a valid price");
                }
            }
        });

        addMenuDialog.show();
    }

    private void addMenuItem(MenuItem newItem) {
        MenuItemApiService apiService = RetrofitClient.getRetrofitInstance().create(MenuItemApiService.class);
        apiService.addMenuItem(newItem).enqueue(new retrofit2.Callback<MenuItem>() {
            @Override
            public void onResponse(retrofit2.Call<MenuItem> call, retrofit2.Response<MenuItem> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LinearLayout contentLayout = null;
                    switch (newItem.getCategory().toLowerCase()) {
                        case "appetizers":
                            contentLayout = findViewById(R.id.appetizers_content);
                            break;
                        case "entrees":
                            contentLayout = findViewById(R.id.entrees_content);
                            break;
                        case "desserts":
                            contentLayout = findViewById(R.id.desserts_content);
                            break;
                        case "drinks":
                            contentLayout = findViewById(R.id.drinks_content);
                            break;
                    }

                    if (contentLayout != null) {
                        // Add new item to the category's content
                        TextView newItemView = new TextView(MenuListActivity.this);
                        newItemView.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        newItemView.setText(String.format("%s - $%.2f", newItem.getName(), newItem.getPrice()));
                        newItemView.setPadding(32, 32, 32, 32);
                        newItemView.setTextSize(16);
                        contentLayout.addView(newItemView);
                    }

                    showErrorMessage("Item added successfully");
                } else {
                    showErrorMessage("Failed to add item");
                }
            }

            @Override
            public void onFailure(retrofit2.Call<MenuItem> call, Throwable t) {
                showErrorMessage("Network error: " + t.getMessage());
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull android.view.MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.nav_combos) {
            // 跳转到 ComboListActivity
            startActivity(new Intent(this, ComboListActivity.class));
        } else if (itemId == R.id.nav_menus) {
            // 跳转到 MenuListActivity
            startActivity(new Intent(this, MenuListActivity.class));
        } else if (itemId == R.id.nav_profile) {
            // 跳转到 ProfileActivity
            startActivity(new Intent(this, ProfileActivity.class));
        }

        return true;
    }

    private void setupExpandableMenus() {
        // Setup for Appetizers
        View appetizersHeader = findViewById(R.id.appetizers_header);
        ImageView appetizersArrow = findViewById(R.id.appetizers_arrow);
        LinearLayout appetizersContent = findViewById(R.id.appetizers_content);
        
        appetizersHeader.setOnClickListener(v -> toggleSection(appetizersArrow, appetizersContent));

        // Setup for Entrees
        View entreesHeader = findViewById(R.id.entrees_header);
        ImageView entreesArrow = findViewById(R.id.entrees_arrow);
        LinearLayout entreesContent = findViewById(R.id.entrees_content);
        
        entreesHeader.setOnClickListener(v -> toggleSection(entreesArrow, entreesContent));

        // Setup for Desserts
        View dessertsHeader = findViewById(R.id.desserts_header);
        ImageView dessertsArrow = findViewById(R.id.desserts_arrow);
        LinearLayout dessertsContent = findViewById(R.id.desserts_content);
        
        dessertsHeader.setOnClickListener(v -> toggleSection(dessertsArrow, dessertsContent));

        // Setup for Drinks
        View drinksHeader = findViewById(R.id.drinks_header);
        ImageView drinksArrow = findViewById(R.id.drinks_arrow);
        LinearLayout drinksContent = findViewById(R.id.drinks_content);
        
        drinksHeader.setOnClickListener(v -> toggleSection(drinksArrow, drinksContent));
    }

    private void toggleSection(ImageView arrow, LinearLayout content) {
        if (content.getVisibility() == View.VISIBLE) {
            content.setVisibility(View.GONE);
            arrow.setRotation(0);
        } else {
            content.setVisibility(View.VISIBLE);
            arrow.setRotation(180);
        }
    }

    private void fetchMenuItemsByCategory() {
        MenuItemApiService apiService = RetrofitClient.getRetrofitInstance().create(MenuItemApiService.class);
        
        // Fetch Appetizers
        fetchItemsForCategory(apiService, "appetizers", findViewById(R.id.appetizers_content));
        
        // Fetch Entrees
        fetchItemsForCategory(apiService, "entrees", findViewById(R.id.entrees_content));
        
        // Fetch Desserts
        fetchItemsForCategory(apiService, "desserts", findViewById(R.id.desserts_content));
        
        // Fetch Drinks
        fetchItemsForCategory(apiService, "drinks", findViewById(R.id.drinks_content));
    }

    private void fetchItemsForCategory(MenuItemApiService apiService, String category, LinearLayout contentLayout) {
        apiService.getMenuItemsByCategory(category).enqueue(new retrofit2.Callback<List<MenuItem>>() {
            @SuppressLint("ResourceType")
            @Override
            public void onResponse(retrofit2.Call<List<MenuItem>> call, retrofit2.Response<List<MenuItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    contentLayout.removeAllViews();
                    for (MenuItem item : response.body()) {
                        TextView itemView = new TextView(MenuListActivity.this);
                        itemView.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        itemView.setText(String.format("%s - $%.2f", item.getName(), item.getPrice()));
                        itemView.setPadding(32, 32, 32, 32);
                        itemView.setTextSize(16);
                        itemView.setClickable(true);
                        itemView.setFocusable(true);
//                        itemView.setBackgroundResource(android.R.attr.selectableItemBackground);
                        
                        // Add click listener to show details dialog
                        itemView.setOnClickListener(v -> showItemDetailsDialog(item));
                        
                        contentLayout.addView(itemView);
                    }
                } else {
                    showErrorMessage("Failed to load " + category);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<MenuItem>> call, Throwable t) {
                showErrorMessage("Network error loading " + category + ": " + t.getMessage());
            }
        });
    }

    private void showItemDetailsDialog(MenuItem item) {
        Dialog dialog = new Dialog(MenuListActivity.this);
        dialog.setContentView(R.layout.dialog_menu_item_details);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setGravity(Gravity.CENTER);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // Initialize views
        EditText nameInput = dialog.findViewById(R.id.item_name_input);
        EditText priceInput = dialog.findViewById(R.id.item_price_input);
        Spinner categorySpinner = dialog.findViewById(R.id.menu_category_spinner);
        ImageButton closeButton = dialog.findViewById(R.id.close_button);
        Button updateButton = dialog.findViewById(R.id.update_button);
        Button deleteButton = dialog.findViewById(R.id.delete_button);

        // Set current values
        nameInput.setText(item.getName());
        priceInput.setText(String.format("%.2f", item.getPrice()));
        // Set spinner selection based on item category
        setSpinnerSelection(categorySpinner, item.getCategory());

        // Handle close button
        closeButton.setOnClickListener(v -> dialog.dismiss());

        // Handle update button
        updateButton.setOnClickListener(v -> {
            try {
                String newName = nameInput.getText().toString();
                double newPrice = Double.parseDouble(priceInput.getText().toString());
                String newCategory = categorySpinner.getSelectedItem().toString();

                MenuItem updatedItem = new MenuItem();
                updatedItem.setId(item.getId());
                updatedItem.setName(newName);
                updatedItem.setPrice(newPrice);
                updatedItem.setCategory(newCategory);

                updateMenuItem(updatedItem, dialog);
            } catch (NumberFormatException e) {
                showErrorMessage("Please enter a valid price");
            }
        });

        // Handle delete button
        deleteButton.setOnClickListener(v -> {
            deleteMenuItem(item.getId(), dialog);
        });

        dialog.show();
    }

    private void updateMenuItem(MenuItem item, Dialog dialog) {
        MenuItemApiService apiService = RetrofitClient.getRetrofitInstance().create(MenuItemApiService.class);
        apiService.updateMenuItem(item.getId(), item).enqueue(new retrofit2.Callback<MenuItem>() {
            @Override
            public void onResponse(retrofit2.Call<MenuItem> call, retrofit2.Response<MenuItem> response) {
                if (response.isSuccessful()) {
                    showErrorMessage("Item updated successfully");
                    dialog.dismiss();
                    fetchMenuItemsByCategory(); // Refresh the lists
                } else {
                    showErrorMessage("Failed to update item");
                }
            }

            @Override
            public void onFailure(retrofit2.Call<MenuItem> call, Throwable t) {
                showErrorMessage("Network error: " + t.getMessage());
            }
        });
    }

    private void deleteMenuItem(Integer itemId, Dialog dialog) {
        MenuItemApiService apiService = RetrofitClient.getRetrofitInstance().create(MenuItemApiService.class);
        apiService.deleteMenuItem(itemId).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    showErrorMessage("Item deleted successfully");
                    dialog.dismiss();
                    fetchMenuItemsByCategory(); // Refresh the lists
                } else {
                    showErrorMessage("Failed to delete item");
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                showErrorMessage("Network error: " + t.getMessage());
            }
        });
    }

    private void setSpinnerSelection(Spinner spinner, String category) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(category)) {
                spinner.setSelection(i);
                break;
            }
        }
    }
}

