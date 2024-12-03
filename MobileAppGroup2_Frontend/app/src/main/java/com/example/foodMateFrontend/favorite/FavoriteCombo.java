package com.example.foodMateFrontend.favorite;

import java.util.List;

public class FavoriteCombo {
    private String name;
    private double price;
    private List<String> items;

    public FavoriteCombo(String name, double price, List<String> items) {
        this.name = name;
        this.price = price;
        this.items = items;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public List<String> getItems() {
        return items;
    }
}
