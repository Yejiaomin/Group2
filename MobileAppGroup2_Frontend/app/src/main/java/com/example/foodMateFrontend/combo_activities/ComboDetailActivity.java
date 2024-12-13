package com.example.foodMateFrontend.combo_activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodMateFrontend.R;
import com.example.foodMateFrontend.RetrofitClient;
import com.example.foodMateFrontend.favorite_activities.FavoriteCombo;
import com.example.foodMateFrontend.menu_activities.MenuItemApiService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComboDetailActivity extends AppCompatActivity {

    private LinearLayout comboLayout;
    // 用于存储选中的combo
    private Map<String, Boolean> selectedCombos = new HashMap<>();
    private Map<String, FavoriteCombo> allCombos = new HashMap<>();
    // 存储用户选中的combo名称列表（或直接存储FavoriteCombo对象列表）
    private List<String> selectedComboNames = new ArrayList<>();

    private MenuItemApiService menuItemApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combo_detail);

        comboLayout = findViewById(R.id.comboListLayout);
        Button saveComboButton = findViewById(R.id.saveComboButton);

        // 初始化API服务（请确保RetrofitClient已正确配置baseUrl）
        menuItemApiService = RetrofitClient.getRetrofitInstance().create(MenuItemApiService.class);

        String comboFilePath = getIntent().getStringExtra("combo_file_path");
        loadComboData(comboFilePath);

        // 为保存按钮添加点击事件
        saveComboButton.setOnClickListener(v -> {
            if (selectedCombos.isEmpty()) {
                Toast.makeText(ComboDetailActivity.this, "No combos selected.", Toast.LENGTH_SHORT).show();
                return;
            }


            List<FavoriteCombo> selectedList = new ArrayList<>();
            for (String comboName : selectedCombos.keySet()) {
                FavoriteCombo fc = allCombos.get(comboName);
                if (fc != null) {
                    selectedList.add(fc);
                }
            }

            // 调用后端API保存
            Call<ResponseBody> call = menuItemApiService.saveFavoriteCombos(selectedList);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(ComboDetailActivity.this, "Combos saved successfully!", Toast.LENGTH_SHORT).show();
                        // 保存成功后可根据需要清空选中列表
                        selectedCombos.clear();
                    } else {
                        Toast.makeText(ComboDetailActivity.this, "Failed to save combos: " + response.code(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(ComboDetailActivity.this, "Error saving combos: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void loadComboData(String comboFilePath) {
        if (comboFilePath == null || comboFilePath.isEmpty()) {
            Log.e("ComboDetailActivity", "Combo file path is missing or empty.");
            showEmptyMessage();
            return;
        }

        File comboFile = new File(comboFilePath);
        if (!comboFile.exists()) {
            Log.e("ComboDetailActivity", "Combo file not found at: " + comboFilePath);
            showEmptyMessage();
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(comboFile))) {
            String line;
            comboLayout.removeAllViews();

            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty() && line.split("-").length == 2) {
                    createComboItem(line);
                }
            }
        } catch (IOException e) {
            Log.e("ComboDetailActivity", "Error reading combo file: " + e.getMessage(), e);
            showEmptyMessage();
        }
    }
    private void createComboItem(String comboLine) {
        // 按 " - " 分割，前半部分是 "comboX: Dish1, Dish2, ...", 后半部分是 "Total Price: $XX.XX"
        String[] comboParts = comboLine.split(" - ");
        if (comboParts.length < 2) {
            Log.e("ComboDetailActivity", "Invalid combo line format: " + comboLine);
            return;
        }

        String comboInfo = comboParts[0].trim();
        String totalPricePart = comboParts[1].trim(); // e.g. "Total Price: $37.50"

        String[] nameAndDishes = comboInfo.split(": ");
        if (nameAndDishes.length < 2) {
            Log.e("ComboDetailActivity", "Invalid combo info format: " + comboInfo);
            return;
        }

        String comboName = nameAndDishes[0].trim();   // e.g. "combo1"
        String dishes = nameAndDishes[1].trim();
        // 解析totalPricePart中的价格值
        double totalPrice = 0.0;
        if (totalPricePart.startsWith("Total Price: $")) {
            String priceStr = totalPricePart.replace("Total Price: $", "").trim();
            try {
                totalPrice = Double.parseDouble(priceStr);
            } catch (NumberFormatException e) {
                Log.e("ComboDetailActivity", "Error parsing price: " + e.getMessage());
            }
        }

        // 创建FavoriteCombo对象并存入allCombos中
        FavoriteCombo favoriteCombo = new FavoriteCombo(comboName, dishes, totalPrice);
        allCombos.put(comboName, favoriteCombo);

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
        comboTitle.setText(comboName);
        comboTitle.setTextSize(16);
        comboTitle.setTextColor(Color.BLACK);
        comboTitle.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1
        ));

        ImageView selectCircle = new ImageView(this);
        selectCircle.setImageResource(R.drawable.circle_empty); // 空心圆圈
        LinearLayout.LayoutParams circleParams = new LinearLayout.LayoutParams(40, 40);
        circleParams.setMargins(16, 0, 16, 0);
        selectCircle.setLayoutParams(circleParams);

        selectCircle.setOnClickListener(v -> {
            boolean currentlySelected = selectedCombos.containsKey(comboName);
            if (currentlySelected) {
                selectedCombos.remove(comboName);
                selectCircle.setImageResource(R.drawable.circle_empty);
            } else {
                selectedCombos.put(comboName, true);
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

        TextView comboDetailsView = new TextView(this);
        comboDetailsView.setText(dishes + "\n" + totalPricePart);
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

        comboLayout.addView(comboContainer);
    }

    private void showEmptyMessage() {
        comboLayout.removeAllViews();
        TextView emptyMessage = new TextView(this);
        emptyMessage.setText("Waiting.");
        emptyMessage.setTextSize(16);
        emptyMessage.setTextColor(Color.GRAY);
        emptyMessage.setGravity(View.TEXT_ALIGNMENT_CENTER);
        comboLayout.addView(emptyMessage);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ComboDetailActivity.this, ComboListActivity.class);
        startActivity(intent);
        finish();
    }

}
