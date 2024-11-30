package com.example.foodMateFrontend.menu_activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

        fetchMenus();
    }

    private void fetchMenus() {
        MenuItemApiService apiService = RetrofitClient.getRetrofitInstance().create(MenuItemApiService.class);
        apiService.getAllMenuItems().enqueue(new retrofit2.Callback<List<MenuItem>>() {
            @Override
            public void onResponse(retrofit2.Call<List<MenuItem>> call, retrofit2.Response<List<MenuItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    menuAdapter = new MenuAdapter(response.body());
                    recyclerView.setAdapter(menuAdapter);
                } else {
                    showErrorMessage("Failed to load menu items");
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<MenuItem>> call, Throwable t) {
                showErrorMessage("Network error: " + t.getMessage());
            }
        });
    }

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
                    // Create new menu item
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
                    // Refresh the menu list
                    fetchMenus();
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
}

