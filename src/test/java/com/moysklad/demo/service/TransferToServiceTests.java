package com.moysklad.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import com.moysklad.demo.dto.ProductTransferDTO;
import com.moysklad.demo.entity.Product;
import com.moysklad.demo.entity.Storehouse;
import com.moysklad.demo.entity.StorehouseProduct;
import com.moysklad.demo.repository.ProductRepository;
import com.moysklad.demo.repository.StorehouseProductRepository;
import com.moysklad.demo.repository.StorehouseRepository;
import com.moysklad.demo.service.impl.TransferToService;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
public class TransferToServiceTests {

  private static Storehouse storehouse = new Storehouse(1L, "sklad");;
  private static Product product = new Product("1111", "laptop");

  @MockBean
  private StorehouseRepository storehouseRepository;
  @MockBean
  private ProductRepository productRepository;
  @MockBean
  private StorehouseProductRepository storehouseProductRepository;
  @Autowired
  private TransferToService transferToService;



  @BeforeEach
  void setup() {
    Mockito.when(storehouseRepository.findById(Mockito.any())).thenReturn(Optional.of(storehouse));
    Mockito.when(productRepository.findByVendorCode(Mockito.any())).thenReturn(Optional.of(product));
//    Mockito.when(storehouseProductRepository.saveAll(Mockito.any()));
  }

  @Test
  void productDoesNotExistInStorehouse_ShouldCreateAssociationWithZeroQuantity() {
    ProductTransferDTO desiredProduct = new ProductTransferDTO(product.getVendorCode(), 1L);
    List<StorehouseProduct> actualAssociation = transferToService.transfer(storehouse.getId(),
        Set.of(desiredProduct));
    assertEquals(1, actualAssociation.size());
    assertEquals(1L, actualAssociation.get(0).getProductQuantity());
  }

  @Test
  void productExistInStorehouse_ShouldAddDesiredQuantity() {
    ProductTransferDTO desiredProduct = new ProductTransferDTO(product.getVendorCode(), 1L);
    StorehouseProduct existingStorehouseProduct = new StorehouseProduct(
        storehouse, product, 1L
    );
    Mockito.when(storehouseProductRepository.findById(Mockito.any())).thenReturn(Optional.of(existingStorehouseProduct));
    List<StorehouseProduct> actualAssociation = transferToService.transfer(storehouse.getId(),
        Set.of(desiredProduct));
    assertEquals(1, actualAssociation.size());
    assertEquals(2L, actualAssociation.get(0).getProductQuantity());
  }
}
