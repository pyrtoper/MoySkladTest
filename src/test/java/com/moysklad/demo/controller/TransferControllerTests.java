package com.moysklad.demo.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moysklad.demo.Utils;
import com.moysklad.demo.dto.ProductDocumentDTO;
import com.moysklad.demo.dto.ProductTransferDTO;
import com.moysklad.demo.entity.Product;
import com.moysklad.demo.entity.Purchase;
import com.moysklad.demo.entity.Storehouse;
import com.moysklad.demo.entity.StorehouseProduct;
import com.moysklad.demo.entity.StorehouseProductId;
import com.moysklad.demo.entity.Transfer;
import com.moysklad.demo.repository.ProductRepository;
import com.moysklad.demo.repository.PurchaseRepository;
import com.moysklad.demo.repository.StorehouseProductRepository;
import com.moysklad.demo.repository.StorehouseRepository;
import com.moysklad.demo.repository.TransferRepository;
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
public class TransferControllerTests {

  @LocalServerPort
  private int port;
  @Autowired
  private MockMvc mockMvc;

  private final String endpoint = "/transfers";
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private ProductRepository productRepository;
  @Autowired
  private StorehouseRepository storehouseRepository;
  @Autowired
  private StorehouseProductRepository storehouseProductRepository;
  @Autowired
  private TransferRepository transferRepository;
  @Autowired
  private Utils utils;
  private Storehouse storehouse1;
  private Storehouse storehouse2;
  private Product product1;
  private Product product2;

  @BeforeEach
  void setup() {
    storehouse1 = new Storehouse( "luxury_storehouse");
    storehouse2 = new Storehouse("even_more_luxury_storehouse");
    product1 = new Product("1", "laptop");
    product2 = new Product("2", "printer", new BigDecimal(10), new BigDecimal(20));
    product1.addStorehouse(storehouse1, 2L);
    product1.addStorehouse(storehouse2, 5L);
    product2.addStorehouse(storehouse2, 10L);
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
  void validTransfer_ShouldNotUpdatePrices() throws Exception {
    Set<ProductTransferDTO> productsSet = Set.of(
        new ProductTransferDTO("1", 5L),
        new ProductTransferDTO("2", 10L)
    );
    Transfer transfer = new Transfer();
    transfer.setStorehouseFromId(storehouse2.getId());
    transfer.setStorehouseToId(storehouse1.getId());
    transfer.setProductSet(productsSet);
    String putToJson = objectMapper.writeValueAsString(transfer);
    mockMvc.perform(post(endpoint)
            .contentType(MediaType.APPLICATION_JSON)
            .content(putToJson))
        .andExpect(status().isOk());
    Product actualProduct1 = productRepository.findByVendorCode(product1.getVendorCode()).orElseThrow();
    Product actualProduct2 = productRepository.findByVendorCode(product2.getVendorCode()).orElseThrow();
    assertEquals(7L, utils.getQuantity(storehouse1, actualProduct1));
    assertEquals(10L, utils.getQuantity(storehouse1, actualProduct2));
    assertEquals(0, utils.getQuantity(storehouse2, actualProduct1));
    assertEquals(0, utils.getQuantity(storehouse2,actualProduct2));
    assertEquals(1, transferRepository.findAll().size());
    assertEquals(product1.getLastSalePrice(), actualProduct1.getLastSalePrice());
    assertEquals(product2.getLastPurchasePrice(), actualProduct2.getLastPurchasePrice());
  }

  @Test
  void invalidTransfer_NotEnoughProducts() throws Exception {
    Set<ProductTransferDTO> productsSet = Set.of(
        new ProductTransferDTO("1", 5L),
        new ProductTransferDTO("2", 1000L)
    );
    Transfer transfer = new Transfer();
    transfer.setStorehouseFromId(storehouse2.getId());
    transfer.setStorehouseToId(storehouse1.getId());
    transfer.setProductSet(productsSet);
    String putToJson = objectMapper.writeValueAsString(transfer);
    mockMvc.perform(post(endpoint)
            .contentType(MediaType.APPLICATION_JSON)
            .content(putToJson))
        .andExpect(status().is4xxClientError());
    Product actualProduct1 = productRepository.findByVendorCode(product1.getVendorCode()).orElseThrow();
    Product actualProduct2 = productRepository.findByVendorCode(product2.getVendorCode()).orElseThrow();
    assertEquals(2L, utils.getQuantity(storehouse1, actualProduct1));
    assertEquals(5L, utils.getQuantity(storehouse2, actualProduct1));
    assertEquals(10L, utils.getQuantity(storehouse2, actualProduct2));
    assertTrue(transferRepository.findAll().isEmpty());
  }

  @Test
  void transferWithSameProduct_validTransfer() throws Exception {
    Set<ProductTransferDTO> productsSet = Set.of(
        new ProductTransferDTO("1", 3L),
        new ProductTransferDTO("1", 2L)
    );
    Transfer transfer = new Transfer();
    transfer.setStorehouseFromId(storehouse2.getId());
    transfer.setStorehouseToId(storehouse1.getId());
    transfer.setProductSet(productsSet);
    String putToJson = objectMapper.writeValueAsString(transfer);
    mockMvc.perform(post(endpoint)
            .contentType(MediaType.APPLICATION_JSON)
            .content(putToJson))
        .andExpect(status().isOk());
    Product actualProduct1 = productRepository.findByVendorCode(product1.getVendorCode()).orElseThrow();
    assertEquals(7L, utils.getQuantity(storehouse1, actualProduct1));
    assertEquals(1, transferRepository.findAll().size());
  }

  @AfterEach
  void deleteAll() {
    transferRepository.deleteAll();
    productRepository.deleteAll();
    storehouseRepository.deleteAll();
  }
}
