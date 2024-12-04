package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data // Lombok 自动生成 Getter, Setter, toString, equals 等方法
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;  // 菜名
    private Double price; // 价格
    private String category;
    public MenuItem() {
    }

    // 参数化构造函数
    public MenuItem(Long id, String name, String category, Double price) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
    }
}
