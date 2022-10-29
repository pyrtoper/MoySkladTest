package com.moysklad.demo.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moysklad.demo.entity.Product;
import com.moysklad.demo.repository.ProductRepository;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
public class ProductControllerTests {

  @LocalServerPort
  private int port;
  @Autowired
  private MockMvc mockMvc;
  private final String endpoint = "/products";
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private ProductRepository productRepository;

  @Test
  void getRequest_ShouldReturnEmptyList() throws Exception {
    mockMvc.perform(get(endpoint))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(content().string("[]"));
  }

  @Test
  void shouldCreateProduct() throws Exception {
    Product product = new Product("1", "laptop");
    String productToJson = objectMapper.writeValueAsString(product);
    mockMvc.perform(post(endpoint)
          .contentType(MediaType.APPLICATION_JSON)
          .content(productToJson))
        .andExpect(status().isOk());
    assertTrue(productRepository.findByVendorCode("1").isPresent());
  }

  @Test
  void createInvalidProduct_ShouldThrowException() throws Exception {
    System.out.println(productRepository.findAll());
    Product product = new Product(null, "laptop");
    String productToJson = objectMapper.writeValueAsString(product);
    mockMvc.perform(post(endpoint)
            .contentType(MediaType.APPLICATION_JSON)
            .content(productToJson))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void updateExistingProduct() throws Exception {
    Product product = new Product("1", "laptop");
    productRepository.saveAndFlush(product);
    Product productToUpdate = new Product("1", "printer");
    String putToJson = objectMapper.writeValueAsString(productToUpdate);
    mockMvc.perform(put(endpoint)
          .contentType(MediaType.APPLICATION_JSON)
          .content(putToJson))
        .andExpect(status().isOk());
    assertEquals("printer", productRepository.findByVendorCode("1").get().getName());
  }
  @Test
  void updateNotExistingProduct_ShouldThrowException() throws Exception {
    Product product = new Product("1", "laptop");
    productRepository.saveAndFlush(product);
    Product productToUpdate = new Product("2", "printer");
    String putToJson = objectMapper.writeValueAsString(productToUpdate);
    mockMvc.perform(put(endpoint)
            .contentType(MediaType.APPLICATION_JSON)
            .content(putToJson))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void shouldDeleteExistingProduct() throws Exception {
    Product product = new Product("1", "laptop");
    productRepository.saveAndFlush(product);
    mockMvc.perform(delete(endpoint + "/1"))
        .andExpect(status().isOk());
    assertEquals(List.of(), productRepository.findAll());
  }

  @Test
  void deleteNotExistingProduct_ShouldThrowException() throws Exception {
    mockMvc.perform(delete(endpoint + "/1"))
        .andExpect(status().is4xxClientError());
  }


  @AfterEach
  void deleteProducts() {
    productRepository.deleteAll();
  }
}
