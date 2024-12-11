package com.example.foodMateFrontend;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.foodMateFrontend.menu_activities.MenuListActivity;

import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    Button sendVerificationCodeBtn;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    EditText verificationCodeInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText emailInput = findViewById(R.id.emailInput);
        EditText passwordInput = findViewById(R.id.passwordInput);
        Button registerButton = findViewById(R.id.registerSubmitButton);
        sendVerificationCodeBtn = findViewById(R.id.sendVerificationCodeBtn);
        verificationCodeInput = findViewById(R.id.verificationCodeInput);

        sharedPreferences = getSharedPreferences("verification", MODE_PRIVATE);
        editor = sharedPreferences.edit();

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
                String verificationCode = sharedPreferences.getString(email, "");
                String userInputVerificationCode = verificationCodeInput.getText().toString();
                if (!verificationCode.equals(userInputVerificationCode)){
                    Toast.makeText(RegisterActivity.this, "Incorrect verification code. ", Toast.LENGTH_SHORT).show();
                    return;
                }

                ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
                progressDialog.setMessage("Registering...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                registerRestaurant(email, password, progressDialog);
            }
        });

        sendVerificationCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random random = new Random();
                int code = random.nextInt(1000000); // Generates a number from 0 to 999999
                String verificationCode = String.format("%06d", code);
                String email = emailInput.getText().toString();
                editor.putString(email, verificationCode);
                editor.apply();
                EmailSender.sendEmail(email, verificationCode);
                Toast.makeText(RegisterActivity.this, "Verification code sent. ", Toast.LENGTH_SHORT).show();
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
                    navigateToActivity(MenuListActivity.class);
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
                    navigateToActivity(MenuListActivity.class);
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