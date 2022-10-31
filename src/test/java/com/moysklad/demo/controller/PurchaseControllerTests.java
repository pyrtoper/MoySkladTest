package com.moysklad.demo.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moysklad.demo.Utils;
import com.moysklad.demo.dto.ProductDocumentDTO;
import com.moysklad.demo.entity.Product;
import com.moysklad.demo.entity.Purchase;
import com.moysklad.demo.entity.Storehouse;
import com.moysklad.demo.entity.StorehouseProduct;
import com.moysklad.demo.entity.StorehouseProductId;
import com.moysklad.demo.repository.ProductRepository;
import com.moysklad.demo.repository.PurchaseRepository;
import com.moysklad.demo.repository.StorehouseProductRepository;
import com.moysklad.demo.repository.StorehouseRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
public class PurchaseControllerTests {

  @LocalServerPort
  private int port;
  @Autowired
  private MockMvc mockMvc;

  private final String endpoint = "/purchases";
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private ProductRepository productRepository;
  @Autowired
  private StorehouseRepository storehouseRepository;
  @Autowired
  private StorehouseProductRepository storehouseProductRepository;
  @Autowired
  private PurchaseRepository purchaseRepository;

  @Autowired
  private Utils utils;

  private Storehouse storehouse;
  private Product product1;
  private Product product2;


  @BeforeEach
  void setup() {
    storehouse = new Storehouse( "luxury_storehouse");
    product1 = new Product("1", "laptop");
    product2 = new Product("2", "printer", new BigDecimal(10), new BigDecimal(20));
    product1.addStorehouse(storehouse, 5L);
    product2.addStorehouse(storehouse, 10L);
    productRepository.saveAllAndFlush(List.of(product1, product2));
  }

  @Test
  void getRequest_ShouldReturnEmptyList() throws Exception {
    mockMvc.perform(get(endpoint))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(content().string("[]"));
  }
  @Test
  void createPurchase_validPurchase() throws Exception {
    Set<ProductDocumentDTO> productsSet = Set.of(
        new ProductDocumentDTO("1", 3L, new BigDecimal(100)),
        new ProductDocumentDTO("2", 100L, new BigDecimal(1000))
    );
    Purchase purchase = new Purchase();
    purchase.setStorehouseId(storehouse.getId());
    purchase.setProductSet(productsSet);
    String putToJson = objectMapper.writeValueAsString(purchase);
    mockMvc.perform(post(endpoint)
          .contentType(MediaType.APPLICATION_JSON)
          .content(putToJson))
        .andExpect(status().isOk());
    Product actualProduct1 = productRepository.findByVendorCode(product1.getVendorCode()).orElseThrow();
    Product actualProduct2 = productRepository.findByVendorCode(product2.getVendorCode()).orElseThrow();
    assertEquals(8L, utils.getQuantity(storehouse, actualProduct1));
    assertEquals(110L, utils.getQuantity(storehouse, actualProduct2));
    assertEquals(new BigDecimal(100), actualProduct1.getLastPurchasePrice());
    assertEquals(new BigDecimal(1000), actualProduct2.getLastPurchasePrice());
    assertEquals(1, purchaseRepository.findAll().size());
  }

  @Test
  void createPurchaseWithSameProduct_validPurchase() throws Exception {
    Set<ProductDocumentDTO> productsSet = Set.of(
        new ProductDocumentDTO("1", 3L, new BigDecimal(100)),
        new ProductDocumentDTO("1", 10L, new BigDecimal(1000))
    );
    Purchase purchase = new Purchase();
    purchase.setStorehouseId(storehouse.getId());
    purchase.setProductSet(productsSet);
    String putToJson = objectMapper.writeValueAsString(purchase);
    mockMvc.perform(post(endpoint)
            .contentType(MediaType.APPLICATION_JSON)
            .content(putToJson))
        .andExpect(status().isOk());
    Product actualProduct1 = productRepository.findByVendorCode(product1.getVendorCode()).orElseThrow();

    assertEquals(18L, utils.getQuantity(storehouse, product1));
    assertTrue(Set.of(new BigDecimal(100), new BigDecimal(1000)).contains(actualProduct1.getLastPurchasePrice()));
    assertEquals(1, purchaseRepository.findAll().size());

  }

  @Test
  void invalidPurchase_shouldNotChangeInitialState() throws Exception {
    Set<ProductDocumentDTO> productsSet = Set.of(
        new ProductDocumentDTO("1", 3L, new BigDecimal(100)),
        new ProductDocumentDTO("", 10L, new BigDecimal(1000))
    );
    Purchase purchase = new Purchase();
    purchase.setStorehouseId(storehouse.getId());
    purchase.setProductSet(productsSet);
    String putToJson = objectMapper.writeValueAsString(purchase);
    mockMvc.perform(post(endpoint)
            .contentType(MediaType.APPLICATION_JSON)
            .content(putToJson))
        .andExpect(status().is4xxClientError());
    Product actualProduct1 = productRepository.findByVendorCode(product1.getVendorCode()).orElseThrow();
    assertEquals(5L, utils.getQuantity(storehouse, product1));
    assertNull(actualProduct1.getLastPurchasePrice());
    assertTrue(purchaseRepository.findAll().isEmpty());
  }
  @Test
  void updatePurchase_ValidPurchase() throws Exception {
    Set<ProductDocumentDTO> productsSet = Set.of(
        new ProductDocumentDTO("1", 3L, new BigDecimal(100)),
        new ProductDocumentDTO("2", 10L, new BigDecimal(1000))
    );
    Purchase purchase = new Purchase();
    purchase.setStorehouseId(storehouse.getId());
    purchase.setProductSet(productsSet);
    purchaseRepository.save(purchase);
    Set<ProductDocumentDTO> productsSetUpdated = Set.of(
        new ProductDocumentDTO("1", 1L, new BigDecimal(10)),
        new ProductDocumentDTO("2", 1L, new BigDecimal(10))
    );
    Purchase purchaseUpdated = new Purchase();
    purchaseUpdated.setStorehouseId(storehouse.getId());
    purchaseUpdated.setProductSet(productsSetUpdated);
    purchaseUpdated.setId(purchase.getId());

    String putToJson = objectMapper.writeValueAsString(purchaseUpdated);

    mockMvc.perform(put(endpoint)
            .contentType(MediaType.APPLICATION_JSON)
            .content(putToJson))
        .andExpect(status().isOk());
    List<Purchase> actualPurchases = purchaseRepository.findAll();
    assertEquals(1, actualPurchases.size());
    assertEquals(purchaseUpdated.getId(), actualPurchases.get(0).getId());
    assertEquals(purchaseUpdated.getStorehouseId(), actualPurchases.get(0).getStorehouseId());
    assertEquals(purchaseUpdated.getProductSet(), actualPurchases.get(0).getProductSet());
  }

  @Test
  void updatePurchase_InvalidPurchase() throws Exception {
    Set<ProductDocumentDTO> productsSet = Set.of(
        new ProductDocumentDTO("1", 3L, new BigDecimal(100)),
        new ProductDocumentDTO("2", 10L, new BigDecimal(1000))
    );
    Purchase purchase = new Purchase();
    purchase.setStorehouseId(storehouse.getId());
    purchase.setProductSet(productsSet);
    purchaseRepository.save(purchase);
    Set<ProductDocumentDTO> productsSetUpdated = Set.of(
        new ProductDocumentDTO("1", 1L, new BigDecimal(10)),
        new ProductDocumentDTO("2", 1L, new BigDecimal(10))
    );
    Purchase purchaseUpdated = new Purchase();
    purchaseUpdated.setStorehouseId(storehouse.getId());
    purchaseUpdated.setProductSet(productsSetUpdated);

    String putToJson = objectMapper.writeValueAsString(purchaseUpdated);

    mockMvc.perform(put(endpoint)
            .contentType(MediaType.APPLICATION_JSON)
            .content(putToJson))
        .andExpect(status().is4xxClientError());
    List<Purchase> actualPurchases = purchaseRepository.findAll();
    assertEquals(1, actualPurchases.size());
    assertEquals(purchase.getId(), actualPurchases.get(0).getId());
    assertEquals(purchase.getStorehouseId(), actualPurchases.get(0).getStorehouseId());
    assertEquals(purchase.getProductSet(), actualPurchases.get(0).getProductSet());
  }
  @Test
  void shouldDeleteById() throws Exception{
    Set<ProductDocumentDTO> productsSet = Set.of(
        new ProductDocumentDTO("1", 3L, new BigDecimal(100)),
        new ProductDocumentDTO("2", 10L, new BigDecimal(1000))
    );
    Purchase purchase = new Purchase();
    purchase.setStorehouseId(storehouse.getId());
    purchase.setProductSet(productsSet);
    purchaseRepository.saveAndFlush(purchase);
    mockMvc.perform(delete(endpoint)
            .param("purchaseId", purchase.getId().toString()))
        .andExpect(status().isOk());
    assertEquals(0, purchaseRepository.findAll().size());
  }


  @AfterEach
  void deleteAll() {
    purchaseRepository.deleteAll();
    productRepository.deleteAll();
    storehouseRepository.deleteAll();
  }
}
