package com.example.owner.menu_activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.owner.R;
import com.example.owner.RetrofitClient;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddMenuActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imagePreview;
    private Uri imageUrl;  // 可为空

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_menu);

        imagePreview = findViewById(R.id.imagePreview);
        EditText menuNameInput = findViewById(R.id.menuNameInput);
        EditText menuPriceInput = findViewById(R.id.menuPriceInput);
        Button selectImageButton = findViewById(R.id.selectImageButton);
        Button addMenuButton = findViewById(R.id.addMenuButton);

        selectImageButton.setOnClickListener(v -> openImageChooser());

        addMenuButton.setOnClickListener(v -> {
            String menuName = menuNameInput.getText().toString();
            String menuPrice = menuPriceInput.getText().toString();

            if (menuName.isEmpty() || menuPrice.isEmpty()) {
                Toast.makeText(AddMenuActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            double price;
            try {
                price = Double.parseDouble(menuPrice);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid price format", Toast.LENGTH_SHORT).show();
                return;
            }

            // 如果图片未选择，imageUri 可以为空
            MenuItem newMenuItem = new MenuItem(menuName, price, imageUrl != null ? imageUrl.toString() : null);
            addMenuToBackend(newMenuItem);
        });
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUrl = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUrl);
                imagePreview.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void addMenuToBackend(MenuItem menuItem) {
        MenuItemApiService apiService = RetrofitClient.getRetrofitInstance().create(MenuItemApiService.class);
        apiService.addMenuItem(menuItem).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddMenuActivity.this, "Menu item added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddMenuActivity.this, "Failed to add menu item", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AddMenuActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}