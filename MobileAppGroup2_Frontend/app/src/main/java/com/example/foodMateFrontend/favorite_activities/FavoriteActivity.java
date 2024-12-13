package com.example.foodMateFrontend.favorite_activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.graphics.Color;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.foodMateFrontend.R;
import com.example.foodMateFrontend.RetrofitClient;
import com.example.foodMateFrontend.combo_activities.ComboListActivity;
import com.example.foodMateFrontend.menu_activities.MenuItemApiService;
import com.example.foodMateFrontend.menu_activities.MenuListActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    private MenuItemApiService menuItemApiService;
    private LinearLayout favoriteListLayout;
    private Button deleteComboButton;

    // 用于存储用户选中的FavoriteCombo的id
    private List<Long> selectedComboIds = new ArrayList<>();
    // 用于缓存获取到的favorite combos
    private List<FavoriteCombo> favoriteCombos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_profile); // Set profile as selected
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        favoriteListLayout = findViewById(R.id.favoriteListLayout);
        deleteComboButton = findViewById(R.id.deleteComboButton);

        menuItemApiService = RetrofitClient.getRetrofitInstance().create(MenuItemApiService.class);

        loadFavoriteCombos();

        deleteComboButton.setOnClickListener(v -> {
            if (selectedComboIds.isEmpty()) {
                Toast.makeText(FavoriteActivity.this, "No combos selected to delete.", Toast.LENGTH_SHORT).show();
                return;
            }
            deleteSelectedCombos();
        });
    }

    private void loadFavoriteCombos() {
        Call<List<FavoriteCombo>> call = menuItemApiService.getFavoriteCombos();
        call.enqueue(new Callback<List<FavoriteCombo>>() {
            @Override
            public void onResponse(Call<List<FavoriteCombo>> call, Response<List<FavoriteCombo>> response) {
                if (response.isSuccessful()) {
                    favoriteCombos = response.body();
                    if (favoriteCombos == null || favoriteCombos.isEmpty()) {
                        showEmptyMessage();
                    } else {
                        displayFavoriteCombos(favoriteCombos);
                    }
                } else {
                    Toast.makeText(FavoriteActivity.this, "Failed to load favorites: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<FavoriteCombo>> call, Throwable t) {
                Toast.makeText(FavoriteActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("FavoriteActivity", "Error fetching favorite combos", t);
            }
        });
    }

    private void displayFavoriteCombos(List<FavoriteCombo> combos) {
        favoriteListLayout.removeAllViews();
        for (FavoriteCombo combo : combos) {
            // 类似ComboDetail的展示方式
            LinearLayout comboContainer = new LinearLayout(this);
            comboContainer.setOrientation(LinearLayout.VERTICAL);
            comboContainer.setBackgroundColor(Color.parseColor("#FFFFFF"));
            comboContainer.setPadding(16, 16, 16, 16);
            comboContainer.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));

            LinearLayout titleLayout = new LinearLayout(this);
            titleLayout.setOrientation(LinearLayout.HORIZONTAL);
            titleLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));

            TextView comboTitle = new TextView(this);
            comboTitle.setText(combo.getCombo_name());
            comboTitle.setTextSize(16);
            comboTitle.setTextColor(Color.BLACK);
            comboTitle.setLayoutParams(new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1
            ));

            ImageView selectCircle = new ImageView(this);
            selectCircle.setImageResource(R.drawable.circle_empty);
            LinearLayout.LayoutParams circleParams = new LinearLayout.LayoutParams(40, 40);
            circleParams.setMargins(16, 0, 16, 0);
            selectCircle.setLayoutParams(circleParams);

            selectCircle.setOnClickListener(v -> {
                if (selectedComboIds.contains(combo.getId())) {
                    // 已选中则取消
                    selectedComboIds.remove(combo.getId());
                    selectCircle.setImageResource(R.drawable.circle_empty);
                } else {
                    // 未选中则加入
                    selectedComboIds.add(combo.getId());
                    selectCircle.setImageResource(R.drawable.circle_filled);
                }
            });

            ImageView dropdownArrow = new ImageView(this);
            dropdownArrow.setImageResource(R.drawable.ic_dropdown_arrow);
            dropdownArrow.setLayoutParams(new LinearLayout.LayoutParams(40, 40));

            titleLayout.addView(comboTitle);
            titleLayout.addView(selectCircle);
            titleLayout.addView(dropdownArrow);
            comboContainer.addView(titleLayout);

            // 显示详情：dishes和price
            String detailText = combo.getDishes() + "\nTotal Price: $" + combo.getTotal_price();
            TextView comboDetailsView = new TextView(this);
            comboDetailsView.setText(detailText);
            comboDetailsView.setTextSize(14);
            comboDetailsView.setTextColor(Color.GRAY);
            comboDetailsView.setVisibility(View.GONE);

            comboContainer.addView(comboDetailsView);

            titleLayout.setOnClickListener(v -> {
                if (comboDetailsView.getVisibility() == View.GONE) {
                    comboDetailsView.setVisibility(View.VISIBLE);
                    dropdownArrow.setRotation(180);
                } else {
                    comboDetailsView.setVisibility(View.GONE);
                    dropdownArrow.setRotation(0);
                }
            });

            favoriteListLayout.addView(comboContainer);
        }
    }

    private void showEmptyMessage() {
        favoriteListLayout.removeAllViews();
        TextView emptyMessage = new TextView(this);
        emptyMessage.setText("No favorite combos saved.");
        emptyMessage.setTextSize(16);
        emptyMessage.setTextColor(Color.GRAY);
        favoriteListLayout.addView(emptyMessage);
    }

    private void deleteSelectedCombos() {
        Call<ResponseBody> call = menuItemApiService.deleteFavoriteCombos(selectedComboIds);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(FavoriteActivity.this, "Deleted successfully!", Toast.LENGTH_SHORT).show();
                    // 删除成功后，清除选中列表并重新加载
                    selectedComboIds.clear();
                    loadFavoriteCombos();
                } else {
                    Toast.makeText(FavoriteActivity.this, "Failed to delete combos: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(FavoriteActivity.this, "Error deleting combos: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.nav_combos) {
            startActivity(new Intent(this, ComboListActivity.class));
        } else if (itemId == R.id.nav_menus) {
            startActivity(new Intent(this, MenuListActivity.class));
        } else if (itemId == R.id.nav_profile) {
            // 当前页面就是FavoriteActivity, 这里可以不做处理或重新加载
            startActivity(new Intent(this, FavoriteActivity.class));
        }

        return true;
    }
}
