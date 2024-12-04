package com.example.demo.dto;

public class ComboRequest {
    private int appetizers;
    private int entrees;
    private int desserts;
    private int drinks;

    // Getter 和 Setter 方法
    public int getAppetizers() {
        return appetizers;
    }

    public void setAppetizers(int appetizers) {
        this.appetizers = appetizers;
    }

    public int getEntrees() {
        return entrees;
    }

    public void setEntrees(int entrees) {
        this.entrees = entrees;
    }

    public int getDesserts() {
        return desserts;
    }

    public void setDesserts(int desserts) {
        this.desserts = desserts;
    }

    public int getDrinks() {
        return drinks;
    }

    public void setDrinks(int drinks) {
        this.drinks = drinks;
    }
}