package com.example.demo.service;

import com.example.demo.entity.MenuItem;
import com.example.demo.repository.MenuItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;

    public MenuItemService(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findAll();
    }

    public List<MenuItem> getMenuItemsByCategory(String category) {
        return menuItemRepository.findByCategory(category.toLowerCase());
    }

    public MenuItem addMenuItem(MenuItem menuItem) {
        menuItem.setCategory(menuItem.getCategory().toLowerCase());
        return menuItemRepository.save(menuItem);
    }

    @Transactional
    public MenuItem updateMenuItem(Integer id, MenuItem menuItem) {
        return menuItemRepository.findById(id)
                .map(existingItem -> {
                    existingItem.setName(menuItem.getName());
                    existingItem.setPrice(menuItem.getPrice());
                    existingItem.setCategory(menuItem.getCategory().toLowerCase());
                    return menuItemRepository.save(existingItem);
                })
                .orElse(null);
    }

    @Transactional
    public boolean deleteMenuItem(Integer id) {
        if (menuItemRepository.existsById(id)) {
            menuItemRepository.deleteById(id);
            return true;
        }
        return false;
    }
}