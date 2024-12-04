package com.example.foodMateFrontend.combo_activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.foodMateFrontend.R;
import com.example.foodMateFrontend.RetrofitClient;
import com.example.foodMateFrontend.menu_activities.MenuListActivity;
import com.example.foodMateFrontend.favorite_activities.FavoriteActivity;
import com.example.foodMateFrontend.menu_activities.MenuItemApiService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComboListActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "ComboListActivity";
    private Button generateComboButton;
    private MenuItemApiService menuItemApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combo_list);

        // 初始化 API 服务
        menuItemApiService = RetrofitClient.getRetrofitInstance().create(MenuItemApiService.class);

        // 初始化底部导航栏
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_combos); // 设置当前选中项
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        // 初始化生成按钮
        generateComboButton = findViewById(R.id.generator);
        generateComboButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateCombo();
            }
        });

        Log.d(TAG, "Activity initialized successfully");
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.nav_combos) {
            startActivity(new Intent(this, ComboListActivity.class));
        } else if (itemId == R.id.nav_menus) {
            startActivity(new Intent(this, MenuListActivity.class));
        } else if (itemId == R.id.nav_profile) {
            startActivity(new Intent(this, FavoriteActivity.class));
        }

        return true;
    }

    private void generateCombo() {
        Log.d(TAG, "Generate Combo button clicked");

        // 创建请求体
        Map<String, Object> request = new HashMap<>();
        request.put("model", "gpt-4");
        request.put("messages", new Object[] {
                new HashMap<String, Object>() {{
                    put("role", "user");
                    put("content", "Generate combos based on menu items.");
                }}
        });

        // 调用后端 API
        Call<String> call = menuItemApiService.generateCombo(request);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String comboResponse = response.body();
                    Log.d(TAG, "Combo Response: " + comboResponse);
                    Toast.makeText(ComboListActivity.this, "Combo Generated: " + comboResponse, Toast.LENGTH_LONG).show();
                } else {
                    Log.e(TAG, "Failed to generate combo: " + response.code());
                    Toast.makeText(ComboListActivity.this, "Failed to generate combo", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, "Error generating combo: " + t.getMessage(), t);
                Toast.makeText(ComboListActivity.this, "Error generating combo.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

