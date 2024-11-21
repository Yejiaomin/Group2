package com.example.foodMateFrontend.menu_activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodMateFrontend.R;
import com.example.foodMateFrontend.RetrofitClient;

import java.util.List;

public class MenuListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MenuAdapter menuAdapter;
    private Button addMenuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_list);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        addMenuButton = findViewById(R.id.addMenuButton);
        addMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到 AddMenuActivity
                Intent intent = new Intent(MenuListActivity.this, AddMenuActivity.class);
                startActivity(intent);
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
}