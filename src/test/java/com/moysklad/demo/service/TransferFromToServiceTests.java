package com.moysklad.demo.service;

import static org.junit.jupiter.api.Assertions.*;

import com.moysklad.demo.dto.ProductTransferDTO;
import com.moysklad.demo.entity.Product;
import com.moysklad.demo.entity.Storehouse;
import com.moysklad.demo.entity.StorehouseProduct;
import com.moysklad.demo.entity.StorehouseProductId;
import com.moysklad.demo.entity.Transfer;
import com.moysklad.demo.exception.NotEnoughProductsException;
import com.moysklad.demo.exception.StorehouseIdsAreEqualException;
import com.moysklad.demo.repository.ProductRepository;
import com.moysklad.demo.repository.StorehouseProductRepository;
import com.moysklad.demo.repository.StorehouseRepository;
import com.moysklad.demo.service.impl.TransferFromService;
import com.moysklad.demo.service.impl.TransferFromToService;
import com.moysklad.demo.service.impl.TransferToService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
public class TransferFromToServiceTests {

  private Storehouse storehouseFrom;
  private Storehouse storehouseTo;
  private Product product1;
  private Product product2;
  @Autowired
  private StorehouseRepository storehouseRepository;
  @Autowired
  private ProductRepository productRepository;
  @Autowired
  private StorehouseProductRepository storehouseProductRepository;
  @Autowired
  private TransferFromToService transferFromToService;
  @Autowired
  private TransferFromService transferFromService;
  @Autowired
  private TransferToService transferToService;

  @BeforeEach
  void setup() {
    storehouseFrom = new Storehouse("skladFrom");
    storehouseTo = new Storehouse( "skladTo");
    product1 = new Product("1111", "laptop");
    product2 = new Product("2222", "scaner");
    product1.addStorehouse(storehouseFrom, 5L);
    product2.addStorehouse(storehouseFrom, 7L);
    product1.addStorehouse(storehouseTo, 10L);
    productRepository.saveAll(List.of(product1, product2));
    System.out.println(productRepository.findAll());
    System.out.println(storehouseRepository.findAll());
    System.out.println(storehouseProductRepository.findAll());
  }

  @Test
  void shouldThrowNotEnoughProducts() {
    Set<ProductTransferDTO> productVendorQuantities = Set.of(
        new ProductTransferDTO(product1.getVendorCode(), 100L),
        new ProductTransferDTO(product2.getVendorCode(), 50L)
    );
    Transfer transfer = new Transfer(storehouseFrom.getId(), storehouseTo.getId(),
        productVendorQuantities);
//    transferFromToService.process(transfer);
    assertThrows(NotEnoughProductsException.class, () ->
        transferFromToService.process(transfer));
  }

  @Test
  void correctTransfer_ShouldReturnCorrectQuantities() {
    Set<ProductTransferDTO> productVendorQuantities = Set.of(
        new ProductTransferDTO(product1.getVendorCode(), 5L),
        new ProductTransferDTO(product2.getVendorCode(), 7L)
    );
    Transfer transfer = new Transfer(storehouseFrom.getId(), storehouseTo.getId(),
        productVendorQuantities);
    transferFromToService.process(transfer);
    StorehouseProduct storehouseFromProduct1 = storehouseProductRepository
        .findById(new StorehouseProductId(storehouseFrom.getId(), product1.getId())).get();
    StorehouseProduct storehouseFromProduct2 = storehouseProductRepository
        .findById(new StorehouseProductId(storehouseFrom.getId(), product2.getId())).get();
    StorehouseProduct storehouseToProduct1 = storehouseProductRepository
        .findById(new StorehouseProductId(storehouseTo.getId(), product1.getId())).get();
    StorehouseProduct storehouseToProduct2 = storehouseProductRepository
        .findById(new StorehouseProductId(storehouseTo.getId(), product2.getId())).get();
    assertEquals(0, storehouseFromProduct1.getProductQuantity());
    assertEquals(0, storehouseFromProduct2.getProductQuantity());
    assertEquals(15L, storehouseToProduct1.getProductQuantity());
    assertEquals(7L, storehouseToProduct2.getProductQuantity());
  }

  @Test
  void storehousesAreEqual_shouldThrowStorehousesAreEqualException() {
    Transfer transfer = new Transfer(1L, 1L,
        new HashSet<>());
    assertThrows(StorehouseIdsAreEqualException.class, () ->
        transferFromToService.process(transfer));
  }

  @AfterEach
  void deleteAll() {
    productRepository.deleteAll();
    storehouseRepository.deleteAll();
    System.out.println(productRepository.findAll());
    System.out.println(storehouseRepository.findAll());
    System.out.println(storehouseProductRepository.findAll());
  }
}
