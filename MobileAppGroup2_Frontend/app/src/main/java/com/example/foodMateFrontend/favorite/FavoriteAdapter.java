package com.example.foodMateFrontend.favorite;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodMateFrontend.R;

import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {

    private Context context;
    private List<FavoriteCombo> favoriteCombos;

    public FavoriteAdapter(Context context, List<FavoriteCombo> favoriteCombos) {
        this.context = context;
        this.favoriteCombos = favoriteCombos;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favorite_combo, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        FavoriteCombo combo = favoriteCombos.get(position);
        holder.comboName.setText(combo.getName());
        holder.comboPrice.setText("$" + combo.getPrice());
        holder.comboDetails.setText(String.join("\n", combo.getItems()));
    }

    @Override
    public int getItemCount() {
        return favoriteCombos.size();
    }

    static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        TextView comboName, comboPrice, comboDetails;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            comboName = itemView.findViewById(R.id.combo_name);
            comboPrice = itemView.findViewById(R.id.combo_price);
            comboDetails = itemView.findViewById(R.id.combo_details);
        }
    }
}
