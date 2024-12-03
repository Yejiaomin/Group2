package com.example.foodMateFrontend.favorite;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodMateFrontend.R;
import com.example.foodMateFrontend.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteActivity extends AppCompatActivity {

    private RecyclerView favoriteRecyclerView;
    private FavoriteAdapter adapter;
    private List<FavoriteCombo> favoriteCombos;
    private FavoriteComboApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        // Initialize RecyclerView
        favoriteRecyclerView = findViewById(R.id.favorite_recycler_view);
        favoriteRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Adapter and Data
        favoriteCombos = new ArrayList<>();
        adapter = new FavoriteAdapter(this, favoriteCombos);
        favoriteRecyclerView.setAdapter(adapter);

        // Initialize Retrofit API Service
        apiService = RetrofitClient.getRetrofitInstance().create(FavoriteComboApiService.class);

        // Fetch Favorites
        fetchFavorites();
    }

    private void fetchFavorites() {
        apiService.getAllFavoriteCombos().enqueue(new Callback<List<FavoriteCombo>>() {
            @Override
            public void onResponse(Call<List<FavoriteCombo>> call, Response<List<FavoriteCombo>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    favoriteCombos.clear();
                    favoriteCombos.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(FavoriteActivity.this, "Failed to load favorites", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<FavoriteCombo>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(FavoriteActivity.this, "Error fetching favorites", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
