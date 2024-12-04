package com.example.demo.controller;

import com.example.demo.dto.ComboRequest;
import com.example.demo.entity.MenuItem;
import com.example.demo.service.MenuItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/menu-items")
@CrossOrigin(origins = "*")
public class MenuItemController {
    private static final Logger logger = LoggerFactory.getLogger(MenuItemController.class);

    private final MenuItemService menuItemService;

    @Autowired
    public MenuItemController(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

    @GetMapping
    public ResponseEntity<List<MenuItem>> getAllMenuItems() {
        try {
            logger.info("Fetching all menu items");
            List<MenuItem> items = menuItemService.getAllMenuItems();
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            logger.error("Error fetching all menu items: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<MenuItem>> getMenuItemsByCategory(@PathVariable String category) {
        try {
            logger.info("Fetching menu items for category: {}", category);
            List<MenuItem> items = menuItemService.getMenuItemsByCategory(category);
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            logger.error("Error fetching menu items for category {}: ", category, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping({"/generateCombos"})
    public ResponseEntity<String> generateCombos() {
        try {
            List<MenuItem> menuItems = this.menuItemService.getAllMenuItems();
            Map<String, Object> request = new HashMap();
            request.put("model", "gpt-4");
            String chatGptResponse = this.menuItemService.generateComboFromChatGPT(request);
            logger.info("ChatGPT Response: {}", chatGptResponse);
            return ResponseEntity.ok(chatGptResponse);
        } catch (Exception var4) {
            logger.error("Error generating combos: ", var4);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error generating combos: " + var4.getMessage());
        }
    }


    @GetMapping("/category-counts")
    public ResponseEntity<Map<String, Long>> getCategoryCounts() {
        try {
            logger.info("Fetching category counts");
            Map<String, Long> counts = menuItemService.getCategoryCounts();
            return ResponseEntity.ok(counts);
        } catch (Exception e) {
            logger.error("Error fetching category counts: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/category/{category}/count")
    public ResponseEntity<Long> getCategoryCount(@PathVariable String category) {
        try {
            logger.info("Fetching count for category: {}", category);
            long count = menuItemService.getCategoryCount(category);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            logger.error("Error fetching count for category {}: ", category, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> createMenuItem(@RequestBody MenuItem menuItem) {
        try {
            logger.info("Received request to create menu item: {}", menuItem);
            
            // Validate input
            if (!validateMenuItem(menuItem)) {
                return ResponseEntity.badRequest().body("Invalid menu item data");
            }

            MenuItem created = menuItemService.addMenuItem(menuItem);
            logger.info("Successfully created menu item: {}", created);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            logger.error("Error creating menu item: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating menu item: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMenuItem(@PathVariable Integer id, @RequestBody MenuItem menuItem) {
        try {
            logger.info("Received request to update menu item with id: {}", id);

            // Validate input
            if (!validateMenuItem(menuItem)) {
                return ResponseEntity.badRequest().body("Invalid menu item data");
            }

            MenuItem updated = menuItemService.updateMenuItem(id, menuItem);
            if (updated != null) {
                logger.info("Successfully updated menu item: {}", updated);
                return ResponseEntity.ok(updated);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Menu item not found");
            }
        } catch (Exception e) {
            logger.error("Error updating menu item: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating menu item: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMenuItem(@PathVariable Integer id) {
        try {
            logger.info("Received request to delete menu item with id: {}", id);
            boolean deleted = menuItemService.deleteMenuItem(id);
            if (deleted) {
                logger.info("Successfully deleted menu item with id: {}", id);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Menu item not found");
            }
        } catch (Exception e) {
            logger.error("Error deleting menu item: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting menu item: " + e.getMessage());
        }
    }

    private boolean validateMenuItem(MenuItem menuItem) {
        return menuItem.getName() != null && !menuItem.getName().trim().isEmpty() &&
               menuItem.getPrice() != null && menuItem.getPrice() > 0 &&
               menuItem.getCategory() != null && !menuItem.getCategory().trim().isEmpty();
    }
}