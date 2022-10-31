package com.moysklad.demo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moysklad.demo.Utils;
import com.moysklad.demo.dto.ProductDocumentDTO;
import com.moysklad.demo.entity.Product;
import com.moysklad.demo.entity.Purchase;
import com.moysklad.demo.entity.Sale;
import com.moysklad.demo.entity.Storehouse;
import com.moysklad.demo.entity.StorehouseProduct;
import com.moysklad.demo.entity.StorehouseProductId;
import com.moysklad.demo.repository.ProductRepository;
import com.moysklad.demo.repository.PurchaseRepository;
import com.moysklad.demo.repository.SaleRepository;
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
public class SaleControllerTests {

  @LocalServerPort
  private int port;
  @Autowired
  private MockMvc mockMvc;

  private final String endpoint = "/sales";
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private ProductRepository productRepository;
  @Autowired
  private StorehouseRepository storehouseRepository;
  @Autowired
  private StorehouseProductRepository storehouseProductRepository;
  @Autowired
  private SaleRepository saleRepository;
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
  void createSale_validSale() throws Exception {
    Set<ProductDocumentDTO> productsSet = Set.of(
        new ProductDocumentDTO("1", 3L, new BigDecimal(100)),
        new ProductDocumentDTO("2", 10L, new BigDecimal(1000))
    );
    Sale sale = new Sale();
    sale.setStorehouseId(storehouse.getId());
    sale.setProductSet(productsSet);
    String putToJson = objectMapper.writeValueAsString(sale);
    mockMvc.perform(post(endpoint)
            .contentType(MediaType.APPLICATION_JSON)
            .content(putToJson))
        .andExpect(status().isOk());
    Product actualProduct1 = productRepository.findByVendorCode(product1.getVendorCode()).orElseThrow();
    Product actualProduct2 = productRepository.findByVendorCode(product2.getVendorCode()).orElseThrow();
    assertEquals(2L, utils.getQuantity(storehouse, actualProduct1));
    assertEquals(0, utils.getQuantity(storehouse, actualProduct2));
    assertEquals(new BigDecimal(100), actualProduct1.getLastSalePrice());
    assertEquals(new BigDecimal(1000), actualProduct2.getLastSalePrice());
    assertEquals(1, saleRepository.findAll().size());
  }

  @Test
  void createInvalidSale_ShouldNotChangeInitialState() throws Exception {
    Set<ProductDocumentDTO> productsSet = Set.of(
        new ProductDocumentDTO("1", 3L, new BigDecimal(100)),
        new ProductDocumentDTO("2", 1000L, new BigDecimal(1000))
    );
    Sale sale = new Sale();
    sale.setStorehouseId(storehouse.getId());
    sale.setProductSet(productsSet);
    String putToJson = objectMapper.writeValueAsString(sale);
    mockMvc.perform(post(endpoint)
            .contentType(MediaType.APPLICATION_JSON)
            .content(putToJson))
        .andExpect(status().isBadRequest());
    Product actualProduct1 = productRepository.findByVendorCode(product1.getVendorCode()).orElseThrow();
    Product actualProduct2 = productRepository.findByVendorCode(product2.getVendorCode()).orElseThrow();
    assertEquals(5L, utils.getQuantity(storehouse, actualProduct1));
    assertEquals(10L, utils.getQuantity(storehouse, actualProduct2));
    assertNull(actualProduct1.getLastSalePrice());
    assertEquals(new BigDecimal(20), actualProduct2.getLastSalePrice());
    assertEquals(0, saleRepository.findAll().size());
  }

  @Test
  void createSaleWithSameProducts_ValidSale() throws Exception {
    Set<ProductDocumentDTO> productsSet = Set.of(
        new ProductDocumentDTO("1", 3L, new BigDecimal(100)),
        new ProductDocumentDTO("1", 2L, new BigDecimal(1000))
    );
    Sale sale = new Sale();
    sale.setStorehouseId(storehouse.getId());
    sale.setProductSet(productsSet);
    String putToJson = objectMapper.writeValueAsString(sale);
    mockMvc.perform(post(endpoint)
            .contentType(MediaType.APPLICATION_JSON)
            .content(putToJson))
        .andExpect(status().isOk());
    Product actualProduct1 = productRepository.findByVendorCode(product1.getVendorCode()).orElseThrow();
    Product actualProduct2 = productRepository.findByVendorCode(product2.getVendorCode()).orElseThrow();
    assertEquals(0, utils.getQuantity(storehouse, actualProduct1));
    assertEquals(10L, utils.getQuantity(storehouse, actualProduct2));
    assertTrue(Set.of(new BigDecimal(100), new BigDecimal(1000))
        .contains(actualProduct1.getLastSalePrice()));
    assertEquals(new BigDecimal(20), actualProduct2.getLastSalePrice());
    assertEquals(1, saleRepository.findAll().size());
  }

  @Test
  void updateSale_ValidSale() throws Exception {
    Set<ProductDocumentDTO> productsSet = Set.of(
        new ProductDocumentDTO("1", 3L, new BigDecimal(100)),
        new ProductDocumentDTO("2", 10L, new BigDecimal(1000))
    );
    Sale sale = new Sale();
    sale.setStorehouseId(storehouse.getId());
    sale.setProductSet(productsSet);
    saleRepository.save(sale);
    Set<ProductDocumentDTO> productsSetUpdated = Set.of(
        new ProductDocumentDTO("1", 1L, new BigDecimal(10)),
        new ProductDocumentDTO("2", 1L, new BigDecimal(10))
    );
    Sale saleUpdated = new Sale();
    saleUpdated.setStorehouseId(storehouse.getId());
    saleUpdated.setProductSet(productsSetUpdated);
    saleUpdated.setId(sale.getId());

    String putToJson = objectMapper.writeValueAsString(saleUpdated);

    mockMvc.perform(put(endpoint)
            .contentType(MediaType.APPLICATION_JSON)
            .content(putToJson))
        .andExpect(status().isOk());
    List<Sale> actualSales = saleRepository.findAll();
    assertEquals(1, actualSales.size());
    assertEquals(saleUpdated.getId(), actualSales.get(0).getId());
    assertEquals(saleUpdated.getStorehouseId(), actualSales.get(0).getStorehouseId());
    assertEquals(saleUpdated.getProductSet(), actualSales.get(0).getProductSet());
  }

  @Test
  void shouldDeleteById() throws Exception{
    Set<ProductDocumentDTO> productsSet = Set.of(
        new ProductDocumentDTO("1", 3L, new BigDecimal(100)),
        new ProductDocumentDTO("2", 10L, new BigDecimal(1000))
    );
    Sale sale = new Sale();
    sale.setStorehouseId(storehouse.getId());
    sale.setProductSet(productsSet);
    saleRepository.saveAndFlush(sale);
    mockMvc.perform(delete(endpoint)
            .param("saleId", sale.getId().toString()))
        .andExpect(status().isOk());
    assertEquals(0, saleRepository.findAll().size());
  }

  @Test
  void updateSale_InvalidSale() throws Exception {
    Set<ProductDocumentDTO> productsSet = Set.of(
        new ProductDocumentDTO("1", 3L, new BigDecimal(100)),
        new ProductDocumentDTO("2", 10L, new BigDecimal(1000))
    );
    Sale sale = new Sale();
    sale.setStorehouseId(storehouse.getId());
    sale.setProductSet(productsSet);
    saleRepository.save(sale);
    Set<ProductDocumentDTO> productsSetUpdated = Set.of(
        new ProductDocumentDTO("1", 1L, new BigDecimal(10)),
        new ProductDocumentDTO("2", 1L, new BigDecimal(10))
    );
    Sale saleUpdated = new Sale();
    saleUpdated.setStorehouseId(storehouse.getId());
    saleUpdated.setProductSet(productsSetUpdated);

    String putToJson = objectMapper.writeValueAsString(saleUpdated);

    mockMvc.perform(put(endpoint)
            .contentType(MediaType.APPLICATION_JSON)
            .content(putToJson))
        .andExpect(status().is4xxClientError());
    List<Sale> actualSales = saleRepository.findAll();
    assertEquals(1, actualSales.size());
    assertEquals(sale.getId(), actualSales.get(0).getId());
    assertEquals(sale.getStorehouseId(), actualSales.get(0).getStorehouseId());
    assertEquals(sale.getProductSet(), actualSales.get(0).getProductSet());
  }

  @AfterEach
  void deleteAll() {
    saleRepository.deleteAll();
    productRepository.deleteAll();
    storehouseRepository.deleteAll();
  }
}
