package com.moysklad.demo.controller;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moysklad.demo.dto.ProductGeneralInfoDTO;
import com.moysklad.demo.entity.Product;
import com.moysklad.demo.entity.Storehouse;
import com.moysklad.demo.repository.ProductRepository;
import com.moysklad.demo.repository.StorehouseRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
public class ReportControllerTests {

  @LocalServerPort
  private int port;
  @Autowired
  private MockMvc mockMvc;
  private final String productsEndpoint = "/reports/products";
  private final String remediesEndpoint = "/reports/remedies";
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private ProductRepository productRepository;
  @Autowired
  private StorehouseRepository storehouseRepository;
  private Storehouse storehouse1;
  private Storehouse storehouse2;
  private Product product1;
  private Product product2;
  private List<ProductGeneralInfoDTO> productsInStorehouse1;
  private List<ProductGeneralInfoDTO> productsInStorehouse2;

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
    productsInStorehouse1 = List.of(
        new ProductGeneralInfoDTO("1", 2L, "laptop")
    );
    productsInStorehouse2 = List.of(
        new ProductGeneralInfoDTO("1", 5L, "laptop"),
        new ProductGeneralInfoDTO("2", 10L, "printer")
    );
  }

  @Test
  void shouldReturnAllProductsReport() throws Exception {
    MvcResult result = mockMvc.perform(get(productsEndpoint))
        .andExpect(status().isOk())
        .andReturn();

    List<Product> products = objectMapper.readValue(result.getResponse().getContentAsString(),
        new TypeReference<>() {});
    assertEquals(List.of(product1, product2), products);
  }

  @Test
  void shouldReturnProductsReportByName() throws Exception {
    MvcResult result = mockMvc.perform(get(productsEndpoint)
            .param("productName", "laptop"))
        .andExpect(status().isOk())
        .andReturn();
    List<Product> products = objectMapper.readValue(result.getResponse().getContentAsString(),
        new TypeReference<>() {});
    assertEquals(1, products.size());
    assertEquals(List.of(product1), products);
  }

  @Test
  void shouldReturnAllStorehouseRemedies() throws Exception {
    MvcResult result = mockMvc.perform(get(remediesEndpoint))
        .andExpect(status().isOk())
        .andReturn();

    Map<Long, List<ProductGeneralInfoDTO>> remedies = objectMapper.readValue(
        result.getResponse().getContentAsString(),
        new TypeReference<>() {}
    );
    assertEquals(2, remedies.size());
    assertEquals(Map.of(storehouse1.getId(), productsInStorehouse1,
        storehouse2.getId(), productsInStorehouse2), remedies);
  }

  @Test
  void shouldReturnSingleStorehouseRemedies() throws Exception {
    MvcResult result = mockMvc.perform(get(remediesEndpoint)
            .param("storehouseId", storehouse2.getId().toString()))
        .andExpect(status().isOk())
        .andReturn();

    Map<Long, List<ProductGeneralInfoDTO>> remedies = objectMapper.readValue(
        result.getResponse().getContentAsString(),
        new TypeReference<>() {}
    );
    assertEquals(1, remedies.size());
    assertEquals(Map.of(storehouse2.getId(), productsInStorehouse2),
        remedies);
  }

  @AfterEach
  void deleteAll() {
    productRepository.deleteAll();
    storehouseRepository.deleteAll();
  }

}
