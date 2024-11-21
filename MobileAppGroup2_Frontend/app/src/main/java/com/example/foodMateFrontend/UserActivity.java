package com.example.foodMateFrontend;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodMateFrontend.menu_activities.MenuAdapter;
import com.example.foodMateFrontend.menu_activities.MenuItem;
import com.example.foodMateFrontend.menu_activities.MenuItemApiService;

import java.util.List;

public class UserActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MenuAdapter menuAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user); // 确保存在这个布局文件

        recyclerView = findViewById(R.id.recyclerView); // 确保 activity_user.xml 中有 recyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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
        Toast.makeText(UserActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
