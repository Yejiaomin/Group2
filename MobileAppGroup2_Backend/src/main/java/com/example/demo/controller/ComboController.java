package com.example.demo.controller;

import com.example.demo.entity.Combo;
import com.example.demo.service.ComboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/combos")
public class ComboController {

    @Autowired
    private ComboService comboService;

    @PostMapping
    public ResponseEntity<Combo> createCombo(@RequestBody Combo combo) {
        Combo createdCombo = comboService.createCombo(combo.getName(), combo.getPrice());
        return ResponseEntity.ok(createdCombo);
    }

    @PostMapping("/{id}/favorite")
    public ResponseEntity<Combo> markAsFavorite(@PathVariable Long id) {
        Combo combo = comboService.markAsFavorite(id);
        return ResponseEntity.ok(combo);
    }

    @GetMapping("/favorites")
    public ResponseEntity<List<Combo>> getFavoriteCombos() {
        List<Combo> favorites = comboService.getFavoriteCombos();
        return ResponseEntity.ok(favorites);
    }
}
