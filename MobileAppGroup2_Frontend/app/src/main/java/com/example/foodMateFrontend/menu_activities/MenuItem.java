package com.example.foodMateFrontend.menu_activities;

public class MenuItem {
    private Integer id; // 可选字段，null 表示未设置
    private String name;
    private double price;
    private String imageUrl; // 新增字段，用于存储图片 URL，可选
    private String category; // Added category field

    // 构造函数：用于从后端获取数据时
    public MenuItem(Integer id, String name, double price, String imageUrl) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    // 构造函数：用于创建新菜单项时（不需要 id 和可选 imageUrl）
    public MenuItem(String name, double price, String imageUrl) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
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

    public String getImageUrl() {
        return imageUrl;
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

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}