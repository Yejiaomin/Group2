package com.example.foodMateFrontend.menu_activities;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface MenuItemApiService {
    // 添加菜单项
    @POST("api/menu/add")
    Call<Void> addMenuItem(@Body MenuItem menuItem);

    // 获取所有菜单项
    @GET("api/menu/all")
    Call<List<MenuItem>> getAllMenuItems();

}
