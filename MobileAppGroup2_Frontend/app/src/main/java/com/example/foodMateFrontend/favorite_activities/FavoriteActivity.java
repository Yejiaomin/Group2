package com.example.foodMateFrontend.favorite_activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.foodMateFrontend.R;
import com.example.foodMateFrontend.menu_activities.MenuListActivity;
import com.example.foodMateFrontend.combo_activities.ComboListActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class FavoriteActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_profile); // Set profile as selected
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
            startActivity(new Intent(this, FavoriteActivity.class));
        }

        return true;
    }
}