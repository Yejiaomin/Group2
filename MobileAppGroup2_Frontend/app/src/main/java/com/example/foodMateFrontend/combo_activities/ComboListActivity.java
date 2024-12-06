package com.example.foodMateFrontend.combo_activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.example.foodMateFrontend.LoadingActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.foodMateFrontend.R;
import com.example.foodMateFrontend.RetrofitClient;
import com.example.foodMateFrontend.menu_activities.MenuListActivity;
import com.example.foodMateFrontend.favorite_activities.FavoriteActivity;
import com.example.foodMateFrontend.menu_activities.MenuItemApiService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.foodMateFrontend.model.FileUtils;


public class ComboListActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "ComboListActivity";
    private Button generateComboButton;
    private Button importDataButton;
    private MenuItemApiService menuItemApiService;
    private static final int FILE_PICKER_REQUEST_CODE = 1;
    private Uri selectedFileUri;

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
    private void handleError() {
        // 回到 ComboListActivity 并显示错误信息
        Intent intent = new Intent(this, ComboListActivity.class);
        intent.putExtra("error_message", "Failed to generate combos. Please try again.");
        startActivity(intent);
        finish(); // 关闭当前活动
    }

    private void generateCombo() {
        Log.d(TAG, "Generate Combo button clicked");
        // Show loading screen
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Generating combos, please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();



        // 调用后端 API
        Call<ResponseBody> call = menuItemApiService.generateCombo();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss(); // 隐藏加载框
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String comboResponse = response.body().string();
                        Log.d(TAG, "Combo Response: " + comboResponse);
                        String filePath = saveComboDataToFile(comboResponse);
                        navigateToDetailActivity(filePath);

                        // transfer to ComboDetailActivity and add response
                        Intent intent = new Intent(ComboListActivity.this, ComboDetailActivity.class);
                        intent.putExtra("comboDetails", comboResponse);
                        startActivity(intent);
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading response body: " + e.getMessage(), e);
                        Toast.makeText(ComboListActivity.this, "Error reading response. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Failed to generate combo: " + response.code());
                    Toast.makeText(ComboListActivity.this, "Failed to generate combo. Response code: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss(); // 隐藏加载框
                Log.e(TAG, "Error generating combo: " + t.getMessage(), t);
                Toast.makeText(ComboListActivity.this, "Error generating combo. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
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
    private String saveComboDataToFile(String comboData) throws IOException {
        // Create a temporary file in the cache directory
        File file = new File(getCacheDir(), "comboData.txt");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(comboData); // Write the combo data to the file
        }
        Log.d(TAG, "Combo data saved to: " + file.getAbsolutePath());
        return file.getAbsolutePath(); // Return the file's absolute path
    }
    private void navigateToDetailActivity(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            Log.e(TAG, "File path is null or empty. Navigation aborted.");
            Toast.makeText(this, "Error: File path is not valid.", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d(TAG, "Navigating to ComboDetailActivity with file path: " + filePath);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(ComboListActivity.this, ComboDetailActivity.class);
            intent.putExtra("combo_file_path", filePath);
            startActivity(intent);
        }, 400); // 延迟 300 毫秒（根据实际需要调整时间）
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
            Call<Map<String, String>> call = menuItemApiService.uploadMockData(body);

            call.enqueue(new Callback<Map<String, String>>() {
                @Override
                public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Map<String, String> responseBody = response.body();
                        String message = responseBody.get("message");
                        String filePath = responseBody.get("filePath");

                        Log.d(TAG, "Response: " + message + " File saved at: " + filePath);
                        Toast.makeText(ComboListActivity.this, message, Toast.LENGTH_LONG).show();
                    } else {
                        Log.e(TAG, "Error importing data: " + response.code());
                        Toast.makeText(ComboListActivity.this, "Failed to import data. Response code: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Map<String, String>> call, Throwable t) {
                    Log.e(TAG, "Error importing data: " + t.getMessage(), t);
                    Toast.makeText(ComboListActivity.this, "Error importing data. Please try again.", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error handling file URI: " + e.getMessage(), e);
            Toast.makeText(this, "Error selecting file.", Toast.LENGTH_SHORT).show();
        }
    }


}

