package com.example.demo.controller;

import com.example.demo.entity.Combo;
import com.example.demo.service.ComboService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/combos")
public class ComboController {

    private final ComboService comboService;

    public ComboController(ComboService comboService) {
        this.comboService = comboService;
    }

    @GetMapping
    public List<Combo> getAllCombos() {
        return comboService.getAllCombos();
    }

    @PostMapping
    public ResponseEntity<Combo> createCombo(@RequestBody Combo combo) {
        return ResponseEntity.ok(comboService.createCombo(combo));
    }
}