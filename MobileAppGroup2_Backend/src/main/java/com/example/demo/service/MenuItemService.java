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


import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

import static com.mysql.cj.conf.PropertyKey.logger;

@Service
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private static final Logger logger = LoggerFactory.getLogger(MenuItemService.class);
    private String mockDataFilePath;


    public MenuItemService(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }
    public void setMockDataFilePath(String mockDataFilePath) {
        this.mockDataFilePath = mockDataFilePath;
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
        if (mockDataFilePath == null || mockDataFilePath.isEmpty()) {
            throw new RuntimeException("Mock data file path is not set. Please upload a file first.");
        }
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        List<MenuItem> menuItems = this.menuItemRepository.findAll();
        System.out.println("Menu items: " + String.valueOf(menuItems));
        String menuItemsContent = (String)menuItems.stream().map((item) -> {
            return String.format("%s (%s) - $%.2f", item.getName(), item.getCategory() == null ? "No category" : item.getCategory(), item.getPrice());
        }).collect(Collectors.joining("\n"));
//        File mockDataFile = new File("C:\\Users\\wchli\\Documents\\WeChat Files\\wxid_06hy5oypkzx422\\FileStorage\\File\\2024-12\\MOCK_DATA(2).json");

        // 读取文件内容为字符串
        String mockDataContent;
        try {
            File mockDataFile = new File(mockDataFilePath);
            mockDataContent = new String(Files.readAllBytes(mockDataFile.toPath()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read mock data file", e);
        }
        String prompt = "Here are the available menu items from our database:\n\n" +
                menuItemsContent +
                "\n\nAdditionally, here is customer order data in JSON format:\n\n" +
                mockDataContent +
                "\n\n analyze the order data and suggest combo and only display the combo no extra talk before or after!i just want the result of combo, and in format of combo1：dish1,dish2...,combo2:dish1,dish2：and so on also shows each combo's total price,also ensure every combo's dish is unique.";

        headers.set("Authorization", "Bearer ");
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
            logger.info("Extracted Content: {}", content);
            // 返回 JSON 给前端
             return content;

        } catch (HttpClientErrorException var12) {
            logger.error("HTTP Error: {}", var12.getResponseBodyAsString());
            throw new RuntimeException("Failed to call ChatGPT", var12);
        } catch (Exception var13) {
            logger.error("Error calling ChatGPT: ", var13);
            throw new RuntimeException("Failed to call ChatGPT", var13);
        }
    }
    private Map<String, List<String>> parseCombosToJson(String content) {
        Map<String, List<String>> combos = new LinkedHashMap<>();
        try {
            String[] lines = content.split("\n");
            for (String line : lines) {
                // 解析格式: combo1: dish1, dish2
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String comboName = parts[0].trim();
                    String[] dishes = parts[1].split(",");
                    List<String> dishList = Arrays.stream(dishes)
                            .map(String::trim)
                            .collect(Collectors.toList());
                    combos.put(comboName, dishList);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse content to JSON", e);
        }
        return combos;
    }


}




