package com.moysklad.demo.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.moysklad.demo.dto.ProductDocumentDTO;
import com.moysklad.demo.entity.Product;
import com.moysklad.demo.entity.Purchase;
import com.moysklad.demo.entity.Sale;
import com.moysklad.demo.entity.Storehouse;
import com.moysklad.demo.repository.ProductRepository;
import com.moysklad.demo.repository.StorehouseRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class SaleValidationTests {
  @Autowired
  private Validator validator;
  @MockBean
  private StorehouseRepository storehouseRepository;
  @MockBean
  private ProductRepository productRepository;

  private Storehouse storehouse = new Storehouse(1L, "luxury_storehouse");
  private Product product = new Product("1", "laptop");
  private Sale sale = new Sale();

  @BeforeEach
  void setup() {
    Mockito.when(storehouseRepository.findById(1L)).thenReturn(Optional.of(storehouse));
    Mockito.when(productRepository.findByVendorCode("1")).thenReturn(Optional.of(product));
  }

  @Test
  void notExistingStorehouse_shouldThrowStorehouseDoesNotExist() {
    ProductDocumentDTO productDocumentDTO = new ProductDocumentDTO("1", 1L, new BigDecimal("1"));
    sale.setProductSet(Set.of(productDocumentDTO));
    sale.setStorehouseId(2L);
    List<ConstraintViolation<Sale>> violations = new ArrayList<>(validator.validate(sale));
    assertEquals(1, violations.size());
    assertEquals("Entered storehouse does not exist", violations.get(0).getMessage());
  }

  @Test
  void notExistingProduct_shouldThrowProductDoesNotExist() {
    ProductDocumentDTO productDocumentDTO = new ProductDocumentDTO("1000", 1L, new BigDecimal("1"));
    sale.setProductSet(Set.of(productDocumentDTO));
    sale.setStorehouseId(1L);
    List<ConstraintViolation<Sale>> violations = new ArrayList<>(validator.validate(sale));
    assertEquals(1, violations.size());
    assertEquals("Entered product does not exist", violations.get(0).getMessage());
  }
  @Test
  void negativeProductQuantity_shouldThrowException() {
    ProductDocumentDTO productDocumentDTO = new ProductDocumentDTO("1", -1000L, new BigDecimal("1"));
    sale.setProductSet(Set.of(productDocumentDTO));
    sale.setStorehouseId(1L);
    List<ConstraintViolation<Sale>> violations = new ArrayList<>(validator.validate(sale));
    assertEquals(1, violations.size());
    assertEquals("Quantity should be more than 1", violations.get(0).getMessage());
  }

  @Test
  void negativeProductPrice_shouldThrowException() {
    ProductDocumentDTO productDocumentDTO = new ProductDocumentDTO("1", 1L, new BigDecimal("-1000"));
    sale.setProductSet(Set.of(productDocumentDTO));
    sale.setStorehouseId(1L);
    List<ConstraintViolation<Sale>> violations = new ArrayList<>(validator.validate(sale));
    assertEquals(1, violations.size());
    assertEquals("Price should not be negative", violations.get(0).getMessage());
  }

  @Test
  void validSale() {
    ProductDocumentDTO productDocumentDTO = new ProductDocumentDTO("1", 1L, new BigDecimal("1"));
    sale.setProductSet(Set.of(productDocumentDTO));
    sale.setStorehouseId(1L);
    List<ConstraintViolation<Sale>> violations = new ArrayList<>(validator.validate(sale));
    assertTrue(violations.isEmpty());
  }
}
