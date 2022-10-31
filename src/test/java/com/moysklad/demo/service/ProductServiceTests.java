package com.moysklad.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import com.moysklad.demo.entity.Product;
import com.moysklad.demo.entity.Storehouse;
import com.moysklad.demo.entity.StorehouseProduct;
import com.moysklad.demo.entity.StorehouseProductId;
import com.moysklad.demo.repository.ProductRepository;
import com.moysklad.demo.repository.StorehouseProductRepository;
import com.moysklad.demo.repository.StorehouseRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
public class ProductServiceTests {
  @Autowired
  private ProductService productService;
  @Autowired
  private ProductRepository productRepository;
  @Autowired
  private StorehouseRepository storehouseRepository;
  @Autowired
  private StorehouseProductRepository storehouseProductRepository;
  private Product product1;
  private Product product2;

  private Storehouse storehouse;

  @BeforeEach
  void setup() {
    product1 = new Product("1", "laptop");
    product2 = new Product("2", "printer");
    storehouse = new Storehouse("luxury storehouse");
    product1.addStorehouse(storehouse, 5L);
    product2.addStorehouse(storehouse, 10L);
    productRepository.saveAll(List.of(product1, product2));
    storehouseRepository.save(storehouse);
  }

  @Test
  void shouldDeleteProductAndAssociations() {
    productService.deleteByVendorCode(product1.getVendorCode());
    Optional<StorehouseProduct> storehouseProducts = storehouseProductRepository
        .findById(new StorehouseProductId(storehouse.getId(), product1.getId()));
    assertTrue(productRepository.findByVendorCode(product1.getVendorCode()).isEmpty());
    assertTrue(storehouseProducts.isEmpty());
    assertEquals(1, storehouseProductRepository.findAll().size());
    assertEquals(1, storehouseRepository.findAll().size());
  }

  @Test
  void shouldDeleteAllAssociations() {
    productService.deleteAll();
    assertTrue(productRepository.findAll().isEmpty());
    assertTrue(storehouseProductRepository.findAll().isEmpty());
    assertTrue(storehouseRepository.findByName(storehouse.getName()).isPresent());
  }

  @AfterEach
  void deleteAll() {
    storehouseRepository.deleteAll();
    productRepository.deleteAll();
    storehouseProductRepository.deleteAll();
  }
}
