package com.moysklad.demo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moysklad.demo.entity.Product;
import com.moysklad.demo.entity.Storehouse;
import com.moysklad.demo.repository.ProductRepository;
import com.moysklad.demo.repository.StorehouseRepository;
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
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
public class StorehouseControllerTests {

  @LocalServerPort
  private int port;
  @Autowired
  private MockMvc mockMvc;

  private final String endpoint = "/storehouses";
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private StorehouseRepository storehouseRepository;

  @Test
  void getRequest_ShouldReturnEmptyList() throws Exception {
    mockMvc.perform(get(endpoint))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(content().string("[]"));
  }

  @Test
  void shouldCreateStorehouse() throws Exception {
    Storehouse storehouse = new Storehouse("luxury_storehouse");
    String storehouseToJson = objectMapper.writeValueAsString(storehouse);
    mockMvc.perform(post(endpoint)
            .contentType(MediaType.APPLICATION_JSON)
            .content(storehouseToJson))
        .andExpect(status().isOk());
    assertTrue(storehouseRepository.findByName("luxury_storehouse").isPresent());
  }

  @Test
  void createInvalidStorehouse_ShouldThrowException() throws Exception {
    System.out.println(storehouseRepository.findAll());
    Storehouse storehouse = new Storehouse(null, "");
    String storehouseToJson = objectMapper.writeValueAsString(storehouse);
    mockMvc.perform(post(endpoint)
            .contentType(MediaType.APPLICATION_JSON)
            .content(storehouseToJson))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void updateExistingStorehouse() throws Exception {
    Storehouse storehouse = new Storehouse("luxury_storehouse");
    storehouseRepository.saveAndFlush(storehouse);
    Storehouse storehouseToUpdate = new Storehouse(storehouse.getId(),"even_more_luxury_storehouse");
    String putToJson = objectMapper.writeValueAsString(storehouseToUpdate);
    mockMvc.perform(put(endpoint)
            .contentType(MediaType.APPLICATION_JSON)
            .content(putToJson))
        .andExpect(status().isOk());
    assertEquals("even_more_luxury_storehouse",
        storehouseRepository.findByName("even_more_luxury_storehouse").get().getName());
  }
  @Test
  void updateNotExistingStorehouse_ShouldThrowException() throws Exception {
    Storehouse storehouse = new Storehouse("luxury_storehouse");
    storehouseRepository.saveAndFlush(storehouse);
    Storehouse storehouseToUpdate = new Storehouse(1L, "printer");
    String putToJson = objectMapper.writeValueAsString(storehouseToUpdate);
    mockMvc.perform(put(endpoint)
            .contentType(MediaType.APPLICATION_JSON)
            .content(putToJson))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void shouldDeleteExistingStorehouse() throws Exception {
    Storehouse storehouse = new Storehouse( "luxury_storehouse");
    Storehouse existingStorehouse = storehouseRepository.saveAndFlush(storehouse);
    mockMvc.perform(delete(endpoint + "/" + existingStorehouse.getId()))
        .andExpect(status().isOk());
    assertEquals(List.of(), storehouseRepository.findAll());
  }

  @Test
  void deleteNotExistingStorehouse_ShouldThrowException() throws Exception {
    mockMvc.perform(delete(endpoint + "/1"))
        .andExpect(status().is4xxClientError());
  }

  @AfterEach
  void deleteStorehouses() {
    storehouseRepository.deleteAll();
  }
}
