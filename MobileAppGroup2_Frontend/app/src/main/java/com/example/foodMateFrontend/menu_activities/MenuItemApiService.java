package com.example.foodMateFrontend.menu_activities;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface MenuItemApiService {
    @GET("menu-items")
    Call<List<MenuItem>> getAllMenuItems();

    @POST("menu-items")
    Call<MenuItem> addMenuItem(@Body MenuItem menuItem);
}
