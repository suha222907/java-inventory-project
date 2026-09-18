package com.shivendra.inventory_api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class InventoryApiApplicationTests {

    @Autowired
    private ApplicationContext context;

    @Test
    void contextLoads() {
        assertThat(context).isNotNull();
    }

    @Test
    void testBeansLoaded() {
        assertThat(context.getBean("productController")).isNotNull();
        assertThat(context.getBean("productService")).isNotNull();
        assertThat(context.getBean("productRepository")).isNotNull();
        assertThat(context.getBean("transactionRepository")).isNotNull();
        assertThat(context.getBean("globalExceptionHandler")).isNotNull();
    }
}
