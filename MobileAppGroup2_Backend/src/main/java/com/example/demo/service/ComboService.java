package com.example.demo.service;

import com.example.demo.entity.Combo;
import com.example.demo.repository.ComboRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComboService {

    @Autowired
    private ComboRepository comboRepository;

    public Combo createCombo(String name, Double price) {
        Combo combo = new Combo();
        combo.setName(name);
        combo.setPrice(price);
        return comboRepository.save(combo);
    }

    public Combo markAsFavorite(Long comboId) {
        Combo combo = comboRepository.findById(comboId)
                .orElseThrow(() -> new RuntimeException("Combo not found with ID: " + comboId));
        combo.setFavorite(true);
        return comboRepository.save(combo);
    }

    public List<Combo> getFavoriteCombos() {
        return comboRepository.findByIsFavoriteTrue();
    }
}
