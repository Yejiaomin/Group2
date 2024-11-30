package com.example.foodMateFrontend.menu_activities;

public class MenuItem {
    private Integer id; // 可选字段，null 表示未设置
    private String name;
    private double price;
    private String category; // Added category field

    // 构造函数：用于从后端获取数据时
    public MenuItem(Integer id, String name, double price, String category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
    }

    // 构造函数：用于创建新菜单项时（不需要 id 和可选 imageUrl）
    public MenuItem(String name, double price, String category) {
        this.name = name;
        this.price = price;
        this.category = category;
    }

    public MenuItem() {

    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}