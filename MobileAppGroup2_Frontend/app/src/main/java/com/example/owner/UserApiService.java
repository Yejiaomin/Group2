package com.example.owner;

import com.example.owner.model.LoginResponse;
import com.example.owner.model.LoginRequest;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UserApiService {

    // 用户注册
    @POST("api/users/register")
    Call<UserResponse> registerUser(@Body User user);

    @GET("api/account/exists")
    Call<Map<String, Object>> checkAccountExists(@Query("email") String email);

    // 用户登录
    @POST("api/users/login")
    Call<LoginResponse> loginUser(@Body LoginRequest request);
}