package com.example.demo.service;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class MenuItemServiceTest {
    @Autowired
    private MenuItemService menuItemService;

    public MenuItemServiceTest() {
    }

    @Test
    public void testGenerateComboFromChatGPT() {
        Map<String, Object> request = new HashMap();
        request.put("model", "gpt-4");
        String response = this.menuItemService.generateComboFromChatGPT(request);
        Assertions.assertNotNull(response);
        System.out.println("Response: " + response);
    }
}
