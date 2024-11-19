package com.example.demo.service;

import com.example.demo.entity.Combo;
import com.example.demo.repository.ComboRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComboService {

    private final ComboRepository comboRepository;

    public ComboService(ComboRepository comboRepository) {
        this.comboRepository = comboRepository;
    }

    public List<Combo> getAllCombos() {
        return comboRepository.findAll();
    }

    public Combo createCombo(Combo combo) {
        return comboRepository.save(combo);
    }
}