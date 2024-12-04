package com.example.owner;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import com.example.foodMateFrontend.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText emailInput = findViewById(R.id.emailInput);
        EditText passwordInput = findViewById(R.id.passwordInput);
        Button registerButton = findViewById(R.id.registerSubmitButton);
        CheckBox userTypeCheckBox = findViewById(R.id.toggleUserTypeButton); // 选择用户类型的 CheckBox

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailInput.getText().toString();
                String password = passwordInput.getText().toString();

                if (!isValidEmail(email)) {
                    Toast.makeText(RegisterActivity.this, "Invalid email format", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isValidPassword(password)) {
                    Toast.makeText(RegisterActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }

                ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
                progressDialog.setMessage("Registering...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                if (userTypeCheckBox.isChecked()) {
                    registerRestaurant(email, password, progressDialog);
                } else {
                    registerUser(email, password, progressDialog);
                }
            }
        });
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 6;
    }

    private void registerRestaurant(String email, String password, ProgressDialog progressDialog) {
        Restaurant restaurant = new Restaurant(email, password, "RestaurantName", "123456789", "10:00 AM - 9:00 PM");
        RestaurantApiService restaurantApi = RetrofitClient.getRetrofitInstance().create(RestaurantApiService.class);

        restaurantApi.registerRestaurant(restaurant).enqueue(new Callback<RestaurantResponse>() {
            @Override
            public void onResponse(Call<RestaurantResponse> call, Response<RestaurantResponse> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Restaurant registered successfully", Toast.LENGTH_SHORT).show();
                    navigateToActivity(RestaurantActivity.class);
                } else {
                    Toast.makeText(RegisterActivity.this, "Restaurant registration failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RestaurantResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerUser(String email, String password, ProgressDialog progressDialog) {
        User user = new User(email, password);
        UserApiService userApi = RetrofitClient.getRetrofitInstance().create(UserApiService.class);

        userApi.registerUser(user).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserResponse> call, Response<UserResponse> response) {
                progressDialog.dismiss();
                Log.v("Response-------", response.toString());
                if (response.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                    navigateToActivity(UserActivity.class);
                } else {
                    Toast.makeText(RegisterActivity.this, "User registration failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToActivity(Class<?> targetActivity) {
        Intent intent = new Intent(RegisterActivity.this, targetActivity);
        startActivity(intent);
        finish();
    }
}