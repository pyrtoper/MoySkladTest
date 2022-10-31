package com.moysklad.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

@SpringBootTest
public class StorehouseServiceTests {

  @Autowired
  private StorehouseService storehouseService;
  @Autowired
  private ProductRepository productRepository;
  @Autowired
  private StorehouseRepository storehouseRepository;
  @Autowired
  private StorehouseProductRepository storehouseProductRepository;

  private Product product;

  private Storehouse storehouse1;
  private Storehouse storehouse2;

  @BeforeEach
  void setup() {
    product = new Product("1", "laptop");
    storehouse1 = new Storehouse( "luxury storehouse");
    storehouse2 = new Storehouse("even more luxury storehouse");
    product.addStorehouse(storehouse1, 5L);
    product.addStorehouse(storehouse2, 10L);
    productRepository.save(product);
    storehouseRepository.saveAll(List.of(storehouse1, storehouse2));
    System.out.println(productRepository.findAll());
    System.out.println(storehouseRepository.findAll());
  }

  @Test
  void shouldDeleteStorehouseAndAssociations() {
    storehouseService.deleteById(storehouse1.getId());
    Optional<StorehouseProduct> storehouseProducts = storehouseProductRepository
        .findById(new StorehouseProductId(storehouse1.getId(), product.getId()));
    assertTrue(storehouseRepository.findById(storehouse1.getId()).isEmpty());
    assertTrue(storehouseProducts.isEmpty());
    assertEquals(1, storehouseProductRepository.findAll().size());
    assertEquals(1, productRepository.findAll().size());
  }

  @Test
  void shouldDeleteAllAssociations() {
    storehouseService.deleteAll();
    assertTrue(storehouseRepository.findAll().isEmpty());
    assertTrue(storehouseProductRepository.findAll().isEmpty());
    assertTrue(productRepository.findByVendorCode(product.getVendorCode()).isPresent());
  }

  @AfterEach
  void deleteAll() {
    storehouseRepository.deleteAll();
    productRepository.deleteAll();
    storehouseProductRepository.deleteAll();
  }
}
