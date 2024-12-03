package com.example.foodMateFrontend.favorite;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.DELETE;
import retrofit2.http.Path;

public interface FavoriteComboApiService {
    @POST("api/favorite/add")
    Call<Void> addFavoriteCombo(@Body FavoriteCombo favoriteCombo);

    @GET("api/favorite/all")
    Call<List<FavoriteCombo>> getAllFavoriteCombos();

    @DELETE("api/favorite/{id}/remove")
    Call<Void> removeFavoriteCombo(@Path("id") Integer id);
}
