package com.example.foodMateFrontend.combo_activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.foodMateFrontend.R;
import com.example.foodMateFrontend.RetrofitClient;
import com.example.foodMateFrontend.menu_activities.MenuListActivity;
import com.example.foodMateFrontend.favorite_activities.FavoriteActivity;
import com.example.foodMateFrontend.menu_activities.MenuItemApiService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.foodMateFrontend.model.FileUtils;


public class ComboListActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "ComboListActivity";
    private Button decrementAppetizer, incrementAppetizer;
    private Button decrementEntree, incrementEntree;
    private Button decrementDessert, incrementDessert;
    private Button decrementDrink, incrementDrink;
    private Button generateComboButton;
    private Button importDataButton;
    private EditText appetizerCount, entreeCount, dessertCount, drinkCount;
    private MenuItemApiService menuItemApiService;
    private static final int FILE_PICKER_REQUEST_CODE = 1;
    private Uri selectedFileUri;
    private int appetizerNum, entreeNum, dessertNum, drinkNum;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

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

        // Initialize combo generator parameters
        sharedPreferences = getSharedPreferences("comboGeneratorParams", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        appetizerCount = findViewById(R.id.appetizer_count);
        entreeCount = findViewById(R.id.entree_count);
        dessertCount = findViewById(R.id.dessert_count);
        drinkCount = findViewById(R.id.drink_count);

        // Retrieve parameters from api if exists
        appetizerNum = sharedPreferences.getInt("AppetizerNum", 0);
        entreeNum = sharedPreferences.getInt("EntreeNum", 0);
        dessertNum = sharedPreferences.getInt("DessertNum", 0);
        drinkNum = sharedPreferences.getInt("DrinkNum", 0);

        appetizerCount.setText(String.valueOf(appetizerNum));
        entreeCount.setText(String.valueOf(entreeNum));
        dessertCount.setText(String.valueOf(dessertNum));
        drinkCount.setText(String.valueOf(drinkNum));

        // 初始化生成按钮
        decrementAppetizer = findViewById(R.id.decrement_appetizer);
        incrementAppetizer = findViewById(R.id.increment_appetizer);
        decrementEntree = findViewById(R.id.decrement_entree);
        incrementEntree = findViewById(R.id.increment_entree);
        decrementDessert = findViewById(R.id.decrement_dessert);
        incrementDessert = findViewById(R.id.increment_dessert);
        decrementDrink = findViewById(R.id.decrement_drink);
        incrementDrink = findViewById(R.id.increment_drink);

        decrementAppetizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (appetizerNum > 0) appetizerNum--;
                appetizerCount.setText(String.valueOf(appetizerNum));
                updateParam("AppetizerNum");
            }
        });
        incrementAppetizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appetizerNum++;
                appetizerCount.setText(String.valueOf(appetizerNum));
                updateParam("AppetizerNum");
            }
        });
        decrementEntree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (entreeNum > 0) entreeNum--;
                entreeCount.setText(String.valueOf(entreeNum));
                updateParam("EntreeNum");
            }
        });
        incrementEntree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                entreeNum++;
                entreeCount.setText(String.valueOf(entreeNum));
                updateParam("EntreeNum");
            }
        });
        decrementDessert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dessertNum > 0) dessertNum--;
                dessertCount.setText(String.valueOf(dessertNum));
                updateParam("DessertNum");
            }
        });
        incrementDessert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dessertNum++;
                dessertCount.setText(String.valueOf(dessertNum));
                updateParam("DessertNum");
            }
        });
        decrementDrink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drinkNum > 0) drinkNum--;
                drinkCount.setText(String.valueOf(drinkNum));
                updateParam("DrinkNum");
            }
        });
        incrementDrink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drinkNum++;
                drinkCount.setText(String.valueOf(drinkNum));
                updateParam("DrinkNum");
            }
        });

        generateComboButton = findViewById(R.id.generator);
        generateComboButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateCombo();
                uploadAllParams();
            }
        });

        importDataButton = findViewById(R.id.button);
        importDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                importData();
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

    private void uploadAllParams(){
        SharedPreferences sharedPreferences = getSharedPreferences("comboGeneratorParams", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("AppetizerNum", appetizerNum);
        editor.putInt("EntreeNum", entreeNum);
        editor.putInt("DessertNum", dessertNum);
        editor.putInt("DrinkNum", drinkNum);
        editor.apply();
    }

    private void updateParam(String param){
        switch (param){
            case "AppetizerNum":
                editor.putInt("AppetizerNum", appetizerNum);
                break;
            case "EntreeNum":
                editor.putInt("EntreeNum", entreeNum);
                break;
            case "DessertNum":
                editor.putInt("DessertNum", dessertNum);
                break;
            case "DrinkNum":
                editor.putInt("DrinkNum", drinkNum);
                break;
        }
        editor.apply();
    }
    private void importData() {
        Log.d(TAG, "Import Data button clicked");

        // Open file explorer to select a JSON file
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(Intent.createChooser(intent, "Select Mock Data File"), FILE_PICKER_REQUEST_CODE);
    }

    // Handle the file result in onActivityResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                selectedFileUri = data.getData();
                Log.d(TAG, "Selected file URI: " + selectedFileUri.toString());

                // Upload the selected file
                uploadMockData(selectedFileUri);
            }
        } else {
            Toast.makeText(this, "No file selected.", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadMockData(Uri fileUri) {
        try {
            // Convert URI to File
            File file = new File(FileUtils.getPath(this, fileUri));

            if (!file.exists()) {
                Toast.makeText(this, "Selected file does not exist!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create RequestBody
            RequestBody requestFile = RequestBody.create(MediaType.get("application/json"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

            // Call the uploadMockData API
            Call<String> call = menuItemApiService.uploadMockData(body);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String serverResponse = response.body();
                        Log.d(TAG, "Import Data Response: " + serverResponse);
                        Toast.makeText(ComboListActivity.this, "Data Imported Successfully: " + serverResponse, Toast.LENGTH_LONG).show();
                    } else {
                        Log.e(TAG, "Failed to import data: " + response.code());
                        Toast.makeText(ComboListActivity.this, "Failed to import data", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e(TAG, "Error importing data: " + t.getMessage(), t);
                    Toast.makeText(ComboListActivity.this, "Error importing data.", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error handling file URI: " + e.getMessage(), e);
            Toast.makeText(this, "Error selecting file.", Toast.LENGTH_SHORT).show();
        }
    }


}

