package com.example.demo.repository;

import com.example.demo.entity.Combo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComboRepository extends JpaRepository<Combo, Long> {
}