package com.moysklad.demo.validation;

import static org.junit.jupiter.api.Assertions.*;
import com.moysklad.demo.dto.ProductTransferDTO;
import com.moysklad.demo.entity.Product;
import com.moysklad.demo.entity.Sale;
import com.moysklad.demo.entity.Storehouse;
import com.moysklad.demo.entity.Transfer;
import com.moysklad.demo.repository.ProductRepository;
import com.moysklad.demo.repository.StorehouseRepository;
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
public class TransferValidationTests {
  @Autowired
  private Validator validator;
  @MockBean
  private StorehouseRepository storehouseRepository;
  @MockBean
  private ProductRepository productRepository;

  private Storehouse storehouseFrom = new Storehouse(1L, "luxury_storehouse");

  private Storehouse storehouseTo = new Storehouse(3L, "even_more_luxury_storehouse");
  private Product product = new Product("1", "laptop");
  private Transfer transfer = new Transfer();

  @BeforeEach
  void setup() {
    Mockito.when(storehouseRepository.findById(1L)).thenReturn(Optional.of(storehouseFrom));
    Mockito.when(storehouseRepository.findById(3L)).thenReturn(Optional.of(storehouseTo));
    Mockito.when(productRepository.findByVendorCode("1")).thenReturn(Optional.of(product));
  }

  @Test
  void notExistingStorehouseFrom_shouldThrowException() {
    ProductTransferDTO productTransferDTO = new ProductTransferDTO("1", 100L);
    transfer.setStorehouseFromId(2L);
    transfer.setStorehouseToId(1L);
    transfer.setProductSet(Set.of(productTransferDTO));
    List<ConstraintViolation<Transfer>> violations = new ArrayList<>(validator.validate(transfer));
    assertEquals(1, violations.size());
    assertEquals("Entered storehouse does not exist", violations.get(0).getMessage());
  }

  @Test
  void notExistingStorehouseTo_shouldThrowException() {
    ProductTransferDTO productTransferDTO = new ProductTransferDTO("1", 100L);
    transfer.setStorehouseFromId(1L);
    transfer.setStorehouseToId(2L);
    transfer.setProductSet(Set.of(productTransferDTO));
    List<ConstraintViolation<Transfer>> violations = new ArrayList<>(validator.validate(transfer));
    assertEquals(1, violations.size());
    assertEquals("Entered storehouse does not exist", violations.get(0).getMessage());
  }
  @Test
  void blankVendorCode_ShouldThrowException() {
    ProductTransferDTO productTransferDTO = new ProductTransferDTO("", 100L);
    transfer.setStorehouseFromId(1L);
    transfer.setStorehouseToId(3L);
    transfer.setProductSet(Set.of(productTransferDTO));
    List<ConstraintViolation<Transfer>> violations = new ArrayList<>(validator.validate(transfer));
    assertEquals(1, violations.size());
    assertEquals("Entered product does not exist", violations.get(0).getMessage());
  }
  @Test
  void negativeQuantity_ShouldThrowException() {
    ProductTransferDTO productTransferDTO = new ProductTransferDTO("1", -1000L);
    transfer.setStorehouseFromId(1L);
    transfer.setStorehouseToId(3L);
    transfer.setProductSet(Set.of(productTransferDTO));
    List<ConstraintViolation<Transfer>> violations = new ArrayList<>(validator.validate(transfer));
    assertEquals(1, violations.size());
    assertEquals("Quantity should not be negative", violations.get(0).getMessage());
  }

  @Test
  void validTransfer() {
    ProductTransferDTO productTransferDTO = new ProductTransferDTO("1", 10L);
    transfer.setStorehouseFromId(1L);
    transfer.setStorehouseToId(3L);
    transfer.setProductSet(Set.of(productTransferDTO));
    List<ConstraintViolation<Transfer>> violations = new ArrayList<>(validator.validate(transfer));
    assertTrue(violations.isEmpty());
  }
}
