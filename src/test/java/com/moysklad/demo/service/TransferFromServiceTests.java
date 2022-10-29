package com.moysklad.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import com.moysklad.demo.dto.ProductTransferDTO;
import com.moysklad.demo.entity.Product;
import com.moysklad.demo.entity.Storehouse;
import com.moysklad.demo.entity.StorehouseProduct;
import com.moysklad.demo.exception.NotEnoughProductsException;
import com.moysklad.demo.exception.StorehouseProductException;
import com.moysklad.demo.repository.ProductRepository;
import com.moysklad.demo.repository.StorehouseProductRepository;
import com.moysklad.demo.repository.StorehouseRepository;
import com.moysklad.demo.service.impl.TransferFromService;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class TransferFromServiceTests {

  private static Storehouse storehouse = new Storehouse(1L, "sklad");;
  private static Product product = new Product("1111", "laptop");

  @MockBean
  private StorehouseRepository storehouseRepository;
  @MockBean
  private ProductRepository productRepository;
  @MockBean
  private StorehouseProductRepository storehouseProductRepository;
  @Autowired
  private TransferFromService transferFromService;

  @BeforeEach
  void setup() {
    Mockito.when(storehouseRepository.findById(Mockito.any())).thenReturn(Optional.of(storehouse));
    Mockito.when(productRepository.findByVendorCode(Mockito.any())).thenReturn(Optional.of(product));
//    Mockito.when(storehouseProductRepository.saveAll(Mockito.any()));
  }

  @Test
  void transferFromStorehouseWithNoSuchProduct_ShouldThrowException() {
    ProductTransferDTO desiredProduct = new ProductTransferDTO(product.getVendorCode(), 1L);
//    List<StorehouseProduct> realStorehouseProduct = transferFromService.transfer(storehouse.getId(),
//        Set.of(desiredProduct));
    assertThrows(StorehouseProductException.class,() -> transferFromService.transfer(storehouse.getId(),
        Set.of(desiredProduct)));
  }

  @Test
  void transferFromStorehouseWithIncorrectProductQuantity_ShouldThrowException() {
    ProductTransferDTO desiredProduct = new ProductTransferDTO(product.getVendorCode(), 10L);
    StorehouseProduct existingStorehouseProduct = new StorehouseProduct(
        storehouse, product, 1L
    );
    Mockito.when(storehouseProductRepository.findById(Mockito.any()))
        .thenReturn(Optional.of(existingStorehouseProduct));
    assertThrows(NotEnoughProductsException.class, () ->
        transferFromService.transfer(storehouse.getId(), Set.of(desiredProduct)));
  }

  @Test
  void transferFromStorehouseWithCorrectProductQuantity_ShouldReturnCorrectQuantityAfter() {
    ProductTransferDTO desiredProduct = new ProductTransferDTO(product.getVendorCode(), 1L);
    StorehouseProduct existingStorehouseProduct = new StorehouseProduct(
        storehouse, product, 3L
    );
    Mockito.when(storehouseProductRepository.findById(Mockito.any()))
        .thenReturn(Optional.of(existingStorehouseProduct));
    List<StorehouseProduct> actualStorehouseProducts = transferFromService
        .transfer(storehouse.getId(), Set.of(desiredProduct));
    assertEquals(1, actualStorehouseProducts.size());
    assertEquals(2L, actualStorehouseProducts.get(0).getProductQuantity());
  }
}
