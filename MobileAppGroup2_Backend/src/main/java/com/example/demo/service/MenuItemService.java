package com.example.demo.service;

import com.example.demo.entity.MenuItem;
import com.example.demo.repository.MenuItemRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.mysql.cj.conf.PropertyKey.logger;

@Service
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private static final Logger logger = LoggerFactory.getLogger(MenuItemService.class);


    public MenuItemService(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findAll();
    }

    public List<MenuItem> getMenuItemsByCategory(String category) {
        return menuItemRepository.findByCategory(category.toLowerCase());
    }

    public Map<String, Long> getCategoryCounts() {
        List<Object[]> results = menuItemRepository.getCategoryCounts();
        Map<String, Long> counts = new HashMap<>();
        for (Object[] result : results) {
            String category = (String) result[0];
            Long count = (Long) result[1];
            counts.put(category, count);
        }
        return counts;
    }

    public long getCategoryCount(String category) {
        return menuItemRepository.countByCategory(category.toLowerCase());
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
    public String generateComboFromChatGPT(Map<String, Object> chatGptRequest) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        List<MenuItem> menuItems = this.menuItemRepository.findAll();
        System.out.println("Menu items: " + String.valueOf(menuItems));
        String menuItemsContent = (String)menuItems.stream().map((item) -> {
            return String.format("%s (%s) - $%.2f", item.getName(), item.getCategory() == null ? "No category" : item.getCategory(), item.getPrice());
        }).collect(Collectors.joining("\n"));
        String prompt = "Here are the available menu items:\n\n" + menuItemsContent + "\n\nPlease create a combo and must only using these menu items and label the category and generate appropriate combo name for each combo.";
        headers.set("Authorization", "Bearer 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");
        if (!chatGptRequest.containsKey("model")) {
            chatGptRequest.put("model", "gpt-4");
        }

        if (!chatGptRequest.containsKey("messages")) {
            chatGptRequest.put("messages", List.of(Map.of("role", "user", "content", prompt)));
        }

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity(chatGptRequest, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity("https://api.openai.com/v1/chat/completions", requestEntity, String.class, new Object[0]);
            logger.info("ChatGPT Response: {}", response.getBody());
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree((String)response.getBody());
            String content = rootNode.path("choices").get(0).path("message").path("content").asText();
            return content;
        } catch (HttpClientErrorException var12) {
            logger.error("HTTP Error: {}", var12.getResponseBodyAsString());
            throw new RuntimeException("Failed to call ChatGPT", var12);
        } catch (Exception var13) {
            logger.error("Error calling ChatGPT: ", var13);
            throw new RuntimeException("Failed to call ChatGPT", var13);
        }
    }
    }




