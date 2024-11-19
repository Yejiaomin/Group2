package com.example.owner.menu_activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.owner.R;
import com.example.owner.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MenuAdapter menuAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_detail);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchMenuItems();
    }

    private void fetchMenuItems() {
        MenuItemApiService apiService = RetrofitClient.getRetrofitInstance().create(MenuItemApiService.class);

        apiService.getAllMenuItems().enqueue(new Callback<List<MenuItem>>() {
            @Override
            public void onResponse(Call<List<MenuItem>> call, Response<List<MenuItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MenuItem> menuItems = response.body();
                    menuAdapter = new MenuAdapter(menuItems);
                    recyclerView.setAdapter(menuAdapter);
                } else {
                    Toast.makeText(MenuActivity.this, "Failed to load menu items", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<MenuItem>> call, Throwable t) {
                Toast.makeText(MenuActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}