package com.example.foodMateFrontend;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.foodMateFrontend.model.LoginRequest;
import com.example.foodMateFrontend.model.LoginResponse;
import com.example.foodMateFrontend.menu_activities.MenuListActivity;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText emailInput = findViewById(R.id.emailInput);
        EditText passwordInput = findViewById(R.id.passwordInput);
        Button loginButton = findViewById(R.id.loginSubmitButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailInput.getText().toString();
                String password = passwordInput.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 先检查账户是否存在
                UserApiService userApi = RetrofitClient.getRetrofitInstance().create(UserApiService.class);
                userApi.checkAccountExists(email).enqueue(new Callback<Map<String, Object>>() {
                    @Override
                    public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Map<String, Object> result = response.body();
                            boolean exists = (Boolean) result.get("exists");

                            if (exists) {
                                String userType = (String) result.get("userType");
                                loginBasedOnUserType(email, password, userType); // 根据用户类型执行登录
                            } else {
                                showAlertDialog("User not found", "The email does not exist. Do you want to register?");
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Error checking account", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                        Toast.makeText(LoginActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void loginBasedOnUserType(String email, String password, String userType) {
        if ("user".equalsIgnoreCase(userType)) {
            loginUser(email, password);
        } else if ("restaurant".equalsIgnoreCase(userType)) {
            loginRestaurant(email, password);
            // reset combo generator params
            SharedPreferences.Editor editor = getSharedPreferences("comboGeneratorParams", MODE_PRIVATE).edit();
            editor.clear();
            editor.apply();
        } else {
            Toast.makeText(LoginActivity.this, "Invalid user type", Toast.LENGTH_SHORT).show();
        }
    }

    private void loginUser(String email, String password) {
        UserApiService userApi = RetrofitClient.getRetrofitInstance().create(UserApiService.class);
        userApi.loginUser(new LoginRequest(email, password)).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isAuthenticated()) {
                    navigateToMenuListActivity();
                } else {
                    Toast.makeText(LoginActivity.this, "Incorrect email or password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginRestaurant(String email, String password) {
        RestaurantApiService restaurantApi = RetrofitClient.getRetrofitInstance().create(RestaurantApiService.class);
        restaurantApi.loginRestaurant(new LoginRequest(email, password)).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isAuthenticated()) {
                    navigateToMenuListActivity();
                } else {
                    Toast.makeText(LoginActivity.this, "Incorrect email or password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToMenuListActivity() {
        Intent intent = new Intent(LoginActivity.this, MenuListActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToUserActivity() {
        Intent intent = new Intent(LoginActivity.this, MenuListActivity.class);
        startActivity(intent);
        finish();
    }

    private void showAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("Register", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}