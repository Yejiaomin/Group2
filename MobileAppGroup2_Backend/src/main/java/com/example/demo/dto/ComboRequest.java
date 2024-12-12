package com.example.demo.dto;

public class ComboRequest {
    private int appetizerNum;
    private int entreeNum;
    private int dessertNum;
    private int drinkNum;
    private int minPrice;
    private int maxPrice;
    private int comboNum;

    // Getters and Setters
    public int getAppetizerNum() {
        return appetizerNum;
    }

    public void setAppetizerNum(int appetizerNum) {
        this.appetizerNum = appetizerNum;
    }

    public int getEntreeNum() {
        return entreeNum;
    }

    public void setEntreeNum(int entreeNum) {
        this.entreeNum = entreeNum;
    }

    public int getDessertNum() {
        return dessertNum;
    }

    public void setDessertNum(int dessertNum) {
        this.dessertNum = dessertNum;
    }

    public int getDrinkNum() {
        return drinkNum;
    }

    public void setDrinkNum(int drinkNum) {
        this.drinkNum = drinkNum;
    }

    public int getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(int minPrice) {
        this.minPrice = minPrice;
    }

    public int getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(int maxPrice) {
        this.maxPrice = maxPrice;
    }

    public int getComboNum() {
        return comboNum;
    }

    public void setComboNum(int comboNum) {
        this.comboNum = comboNum;
    }

    @Override
    public String toString() {
        return "ComboRequest{" +
                "appetizerNum=" + appetizerNum +
                ", entreeNum=" + entreeNum +
                ", dessertNum=" + dessertNum +
                ", drinkNum=" + drinkNum +
                ", minPrice=" + minPrice +
                ", maxPrice=" + maxPrice +
                ", comboNum=" + comboNum +
                '}';
    }
}
