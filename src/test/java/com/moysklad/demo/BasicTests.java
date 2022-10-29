package com.moysklad.demo;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.moysklad.demo.rest.ProductController;
import com.moysklad.demo.rest.PurchaseController;
import com.moysklad.demo.rest.ReportController;
import com.moysklad.demo.rest.SaleController;
import com.moysklad.demo.rest.StorehouseController;
import com.moysklad.demo.rest.TransferController;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(value = {ProductController.class, StorehouseController.class, ReportController.class,
PurchaseController.class, SaleController.class, TransferController.class})
public class BasicTests{

  @Autowired
  MockMvc mockMvc;

  @MockBean
  List<JpaRepository<?, ?>> repositories;

  @ParameterizedTest
  @MethodSource("testEndpoints")
  void apiUrl_IsReachable(String endpoint) throws Exception {
    repositories.forEach((repository) -> Mockito.when(repository.findAll()).thenReturn(new ArrayList<>()));

    mockMvc.perform(MockMvcRequestBuilders
        .get(endpoint)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

  }

  public static Collection<Object> testEndpoints() {
    return Arrays.asList(new Object[][]{
        {"/products"},
        {"/storehouses"},
        {"/reports/products"},
        {"/reports/remedies"},
        {"/purchases"},
        {"/sales"}
    });
  }
}
