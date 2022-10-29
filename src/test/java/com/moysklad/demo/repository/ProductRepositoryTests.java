package com.moysklad.demo.repository;

import static org.junit.jupiter.api.Assertions.*;
import com.moysklad.demo.entity.Product;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
public class ProductRepositoryTests {

  @Autowired
  private ProductRepository productRepository;

  private static List<Product> setupProducts = new ArrayList<>();

  @BeforeAll
  static void setupAll() {
    Product product1 = new Product("1111", "laptop");
    Product product2 = new Product("2222", "printer", new BigDecimal(100), new BigDecimal(200));
    setupProducts.addAll(List.of(product1, product2));
  }

  @BeforeEach
  void setupEach() {
    productRepository.saveAll(setupProducts);
  }

  @AfterEach
  void afterEach() {
    productRepository.deleteAll();
  }

  @Test
  void testCreating() {

    List<Product> realProducts = productRepository.findAll();
    assertEquals(setupProducts, realProducts);
  }

  @Test
  void shouldFindByName() {
    List<Product> product = productRepository.findByName("laptop");
    assertEquals("laptop", product.get(0).getName());
  }

  @Test
  void shouldFindByVendorCode() {
    Product product = productRepository.findByVendorCode("2222").get();
    assertEquals("printer", product.getName());
  }

  @Test
  void shouldDeleteByVendorCode() {
    productRepository.deleteByVendorCode("1111");
    List<Product> products = productRepository.findAll();
    assertEquals(List.of("printer"), products
        .stream()
        .map(Product::getName)
        .collect(Collectors.toList()));
  }
}
