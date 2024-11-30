package com.example.foodMateFrontend;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.foodMateFrontend.combo_activities.ComboListActivity;
import com.example.foodMateFrontend.menu_activities.MenuListActivity;
import com.example.foodMateFrontend.profile_activities.ProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class RestaurantActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant); // 使用修改后的XML布局

        // 获取 BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // 设置监听器
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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