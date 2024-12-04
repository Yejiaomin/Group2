package com.example.owner;

import com.example.owner.model.LoginRequest;
import com.example.owner.model.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RestaurantApiService {
    @POST("api/restaurant/register") // 确保路径与后端一致
    Call<RestaurantResponse> registerRestaurant(@Body Restaurant restaurant);

    @POST("api/restaurant/login")
    Call<LoginResponse> loginRestaurant(@Body LoginRequest request);

}