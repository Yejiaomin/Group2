package com.example.foodMateFrontend.favorite_activities;

public class FavoriteCombo {
    private Long id; // 新增ID字段
    private String combo_name;
    private String dishes;
    private double total_price;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FavoriteCombo(String combo_name, String dishes, double total_price) {
        this.combo_name = combo_name;
        this.dishes = dishes;
        this.total_price = total_price;
    }

    // Getter和Setter
    public String getCombo_name() {
        return combo_name;
    }

    public void setCombo_name(String combo_name) {
        this.combo_name = combo_name;
    }

    public String getDishes() {
        return dishes;
    }

    public void setDishes(String dishes) {
        this.dishes = dishes;
    }

    public double getTotal_price() {
        return total_price;
    }

    public void setTotal_price(double total_price) {
        this.total_price = total_price;
    }
}
