package com.example.foodMateFrontend.menu_activities;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface MenuItemApiService {
    @GET("api/menu-items")
    Call<List<MenuItem>> getAllMenuItems();

    @GET("api/menu-items/category/{category}")
    Call<List<MenuItem>> getMenuItemsByCategory(@Path("category") String category);

    @POST("api/menu-items/add")
    Call<MenuItem> addMenuItem(@Body MenuItem menuItem);

    @PUT("api/menu-items/{id}")
    Call<MenuItem> updateMenuItem(@Path("id") Integer id, @Body MenuItem menuItem);

    @DELETE("api/menu-items/{id}")
    Call<Void> deleteMenuItem(@Path("id") Integer id);
    @POST("/api/menu-items/generateCombos")
    Call<String> generateCombo(@Body Map<String, Object> requestBody);
    @Multipart
    @POST("/api/menu-items/importData")
    Call<String> uploadMockData(@Part MultipartBody.Part file);

}
