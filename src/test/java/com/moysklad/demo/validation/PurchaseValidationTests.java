package com.moysklad.demo.validation;

import static org.junit.jupiter.api.Assertions.*;
import com.moysklad.demo.dto.ProductDocumentDTO;
import com.moysklad.demo.entity.Product;
import com.moysklad.demo.entity.Purchase;
import com.moysklad.demo.entity.Storehouse;
import com.moysklad.demo.repository.ProductRepository;
import com.moysklad.demo.repository.StorehouseRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class PurchaseValidationTests {

  @Autowired
  private Validator validator;
  @MockBean
  private StorehouseRepository storehouseRepository;
  @MockBean
  private ProductRepository productRepository;

  private Storehouse storehouse = new Storehouse(1L, "luxury_storehouse");
  private Product product = new Product("1", "laptop");
  private Purchase purchase = new Purchase();

  @BeforeEach
  void setup() {
    Mockito.when(storehouseRepository.findById(1L)).thenReturn(Optional.of(storehouse));
    Mockito.when(productRepository.findByVendorCode("1")).thenReturn(Optional.of(product));
  }

  @Test
  void notExistingStorehouse_shouldThrowStorehouseDoesNotExist() {
    ProductDocumentDTO productDocumentDTO = new ProductDocumentDTO("1", 1L, new BigDecimal("1"));
    purchase.setProductSet(Set.of(productDocumentDTO));
    purchase.setStorehouseId(2L);
    List<ConstraintViolation<Purchase>> violations = new ArrayList<>(validator.validate(purchase));
    assertEquals(1, violations.size());
    assertEquals("Entered storehouse does not exist", violations.get(0).getMessage());
  }

  @Test
  void notExistingProduct_shouldThrowProductDoesNotExist() {
    ProductDocumentDTO productDocumentDTO = new ProductDocumentDTO("1000", 1L, new BigDecimal("1"));
    purchase.setProductSet(Set.of(productDocumentDTO));
    purchase.setStorehouseId(1L);
    List<ConstraintViolation<Purchase>> violations = new ArrayList<>(validator.validate(purchase));
    assertEquals(1, violations.size());
    assertEquals("Entered product does not exist", violations.get(0).getMessage());
  }
  @Test
  void negativeProductQuantity_shouldThrowException() {
    ProductDocumentDTO productDocumentDTO = new ProductDocumentDTO("1", -1000L, new BigDecimal("1"));
    purchase.setProductSet(Set.of(productDocumentDTO));
    purchase.setStorehouseId(1L);
    List<ConstraintViolation<Purchase>> violations = new ArrayList<>(validator.validate(purchase));
    assertEquals(1, violations.size());
    assertEquals("Quantity should not be negative", violations.get(0).getMessage());
  }

  @Test
  void negativeProductPrice_shouldThrowException() {
    ProductDocumentDTO productDocumentDTO = new ProductDocumentDTO("1", 1L, new BigDecimal("-1000"));
    purchase.setProductSet(Set.of(productDocumentDTO));
    purchase.setStorehouseId(1L);
    List<ConstraintViolation<Purchase>> violations = new ArrayList<>(validator.validate(purchase));
    assertEquals(1, violations.size());
    assertEquals("Price should not be negative", violations.get(0).getMessage());
  }

  @Test
  void validPurchase() {
    ProductDocumentDTO productDocumentDTO = new ProductDocumentDTO("1", 1L, new BigDecimal("1"));
    purchase.setProductSet(Set.of(productDocumentDTO));
    purchase.setStorehouseId(1L);
    List<ConstraintViolation<Purchase>> violations = new ArrayList<>(validator.validate(purchase));
    assertTrue(violations.isEmpty());
  }
}
