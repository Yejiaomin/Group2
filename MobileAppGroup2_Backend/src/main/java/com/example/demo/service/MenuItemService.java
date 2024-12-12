package com.example.demo.service;

import com.example.demo.dto.ComboRequest;
import com.example.demo.entity.MenuItem;
import com.example.demo.repository.MenuItemRepository;
import com.example.demo.entity.FavoriteCombo;
import com.example.demo.repository.FavoriteComboRepository;
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

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MenuItemService {

    private static final Logger logger = LoggerFactory.getLogger(MenuItemService.class);

    private final MenuItemRepository menuItemRepository;
    private final FavoriteComboRepository favoriteComboRepository;

    private String mockDataFilePath;

    public MenuItemService(MenuItemRepository menuItemRepository, FavoriteComboRepository favoriteComboRepository) {
        this.menuItemRepository = menuItemRepository;
        this.favoriteComboRepository = favoriteComboRepository;
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
            String cat = (String) result[0];
            Long cnt = (Long) result[1];
            counts.put(cat, cnt);
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

    /**
     * 新增方法：将传入的FavoriteCombo列表存入数据库
     */
    public void saveFavoriteCombos(List<FavoriteCombo> favoriteCombos) {
        logger.info("Received FavoriteCombos to save: {}", favoriteCombos);
        // 设置创建时间
        for (FavoriteCombo combo : favoriteCombos) {
            combo.setCreated_at(LocalDateTime.now());
        }
        List<FavoriteCombo> savedCombos = favoriteComboRepository.saveAll(favoriteCombos);
        logger.info("Successfully saved FavoriteCombos: {}", savedCombos);
        List<FavoriteCombo> allCombos = favoriteComboRepository.findAll();
        logger.info("All FavoriteCombos in DB after save: {}", allCombos);
    }

    /**
     * 新增方法：获取所有已保存的FavoriteCombo列表
     */
    public List<FavoriteCombo> getAllFavoriteCombos() {
        return favoriteComboRepository.findAll();
    }
    public void deleteFavoriteCombosByIds(List<Long> comboIds) {
        favoriteComboRepository.deleteAllById(comboIds);
    }


    public String generateComboWithParams(ComboRequest comboRequest) {
        if (comboRequest == null) {
            throw new IllegalArgumentException("ComboRequest is null");
        }
        logger.info("Received parameters: {}", comboRequest);

        // 准备用于生成 Prompt 的参数 Map
        Map<String, Object> request = new HashMap<>();
        request.put("appetizerNum", comboRequest.getAppetizerNum());
        request.put("entreeNum", comboRequest.getEntreeNum());
        request.put("dessertNum", comboRequest.getDessertNum());
        request.put("drinkNum", comboRequest.getDrinkNum());
        request.put("minPrice", comboRequest.getMinPrice());
        request.put("maxPrice", comboRequest.getMaxPrice());
        request.put("comboNum", comboRequest.getComboNum());
        request.put("comboRequest", comboRequest); // 保留原对象以便后续使用构建 Prompt

        return generateComboFromChatGPT(request);
    }

    public String generateComboFromChatGPT(Map<String, Object> chatGptRequest) {
        if (mockDataFilePath == null || mockDataFilePath.isEmpty()) {
            throw new RuntimeException("Mock data file path is not set. Please upload a file first.");
        }

        // 从 chatGptRequest 中获取 comboRequest 对象构建 Prompt
        ComboRequest comboRequest = (ComboRequest) chatGptRequest.get("comboRequest");

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        List<MenuItem> menuItems = this.menuItemRepository.findAll();
        String menuItemsContent = menuItems.stream()
                .map(item -> String.format("%s (%s) - $%.2f",
                        item.getName(),
                        item.getCategory() == null ? "No category" : item.getCategory(),
                        item.getPrice()))
                .collect(Collectors.joining("\n"));

        String mockDataContent;
        try {
            File mockDataFile = new File(mockDataFilePath);
            mockDataContent = new String(Files.readAllBytes(mockDataFile.toPath()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read mock data file", e);
        }

        String prompt = String.format(
                "Here are the available menu items from our database:\n\n%s\n\n" +
                        "Additionally, here is customer order data in JSON format:\n\n%s\n\n" +
                        "Using the above data, suggest %d combos. Each combo should satisfy the following criteria:\n" +
                        "- Include exactly %d appetizers, %d entrees, %d desserts, and %d drinks.\n" +
                        "- try your best to fit the total price of each combo falls within the suggest range of $%d to $%d.\n but it's not necessary" +
                        "- Each combo's dishes must be unique and should not overlap with dishes in other combos.\n" +
                        "Please format the output as follows:\n" +
                        "combo1: dish1, dish2, ... - Total Price: $XX.XX\n" +
                        "combo2: dish1, dish2, ... - Total Price: $XX.XX\n" +
                        "...\n" +
                        "Ensure there is no extra commentary or text, just the combo details in the format provided,no notes or other explanation.",
                menuItemsContent,
                mockDataContent,
                comboRequest.getComboNum(),
                comboRequest.getAppetizerNum(),
                comboRequest.getEntreeNum(),
                comboRequest.getDessertNum(),
                comboRequest.getDrinkNum(),
                comboRequest.getMinPrice(),
                comboRequest.getMaxPrice()
        );

        headers.set("Authorization", "Bearer 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");

        Map<String, Object> openAiRequest = new HashMap<>();
        openAiRequest.put("model", "gpt-4");
        openAiRequest.put("messages", List.of(
                Map.of("role", "user", "content", prompt)
        ));

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(openAiRequest, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://api.openai.com/v1/chat/completions",
                    requestEntity,
                    String.class
            );
            logger.info("ChatGPT Response: {}", response.getBody());

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            String content = rootNode.path("choices").get(0).path("message").path("content").asText();
            logger.info("Extracted Content: {}", content);

            return content;
        } catch (HttpClientErrorException e) {
            logger.error("HTTP Error: {}", e.getResponseBodyAsString());
            throw new RuntimeException("Failed to call ChatGPT", e);
        } catch (Exception e) {
            logger.error("Error calling ChatGPT: ", e);
            throw new RuntimeException("Failed to call ChatGPT", e);
        }
    }

    private Map<String, List<String>> parseCombosToJson(String content) {
        Map<String, List<String>> combos = new LinkedHashMap<>();
        try {
            String[] lines = content.split("\n");
            for (String line : lines) {
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
