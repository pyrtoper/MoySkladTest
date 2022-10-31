package com.moysklad.demo.service;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.junit.jupiter.api.Assertions.*;

import com.moysklad.demo.dto.ProductGeneralInfoDTO;
import com.moysklad.demo.entity.Product;
import com.moysklad.demo.entity.Storehouse;
import com.moysklad.demo.entity.StorehouseProduct;
import com.moysklad.demo.repository.ProductRepository;
import com.moysklad.demo.repository.StorehouseProductRepository;
import com.moysklad.demo.repository.StorehouseRepository;
import com.moysklad.demo.service.impl.ReportServiceImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class ReportServiceTests {

  @Autowired
  private ReportServiceImpl reportService;
  @MockBean
  private StorehouseProductRepository storehouseProductRepository;
  @MockBean
  private StorehouseRepository storehouseRepository;
  @MockBean
  private ProductRepository productRepository;
  private static Storehouse storehouse1;
  private static Storehouse storehouse2;
  private static Product product1;
  private static Product product2;
  private static List<StorehouseProduct> initialInfoInDB = new ArrayList<>();
  private static List<ProductGeneralInfoDTO> firstStorehouseProducts;
  private static List<ProductGeneralInfoDTO> secondStorehouseProducts;


  @BeforeAll
  static void setup() {
    storehouse1 = new Storehouse(1L, "sklad1");
    storehouse2 = new Storehouse( 2L, "sklad2");
    product1 = new Product("1111", "laptop");
    product2 = new Product("2222", "laptop");
    product1.addStorehouse(storehouse1, 5L);
    product1.addStorehouse(storehouse2, 10L);
    product2.addStorehouse(storehouse2, 7L);
    storehouse1.addProduct(product1, 5L);
    storehouse2.addProduct(product1, 10L);
    storehouse2.addProduct(product2, 7L);
    initialInfoInDB.addAll(product1.getStorehouseProductsAssociation());
    initialInfoInDB.addAll(product2.getStorehouseProductsAssociation());
    firstStorehouseProducts = List.of(
        new ProductGeneralInfoDTO(product1.getVendorCode(), 5L, product1.getName())
    );
    secondStorehouseProducts = List.of(
        new ProductGeneralInfoDTO(product1.getVendorCode(), 10L, product1.getName()),
        new ProductGeneralInfoDTO(product2.getVendorCode(), 7L, product2.getName())
    );

  }

  @Test
  void shouldReturnAllRemediesReport() {
    Mockito.when(storehouseProductRepository.findAll()).thenReturn(initialInfoInDB);
    Map<Long, List<ProductGeneralInfoDTO>> actualReport =
        reportService.getAllRemainders();
    assertEquals(Map.of(storehouse1.getId(), firstStorehouseProducts,
        storehouse2.getId(), secondStorehouseProducts),
        actualReport);
  }

  @ParameterizedTest
  @MethodSource("testReportRemediesByStorehouse")
  void shouldReturnCorrectReportByStorehouseName(Storehouse storehouse,
      List<ProductGeneralInfoDTO> remedies) {
    Mockito.when(storehouseRepository.findById(Mockito.any())).thenReturn(Optional.of(storehouse1));
    Map<Long, List<ProductGeneralInfoDTO>> actualReport =
        reportService.getStorehouseRemaindersInfo(storehouse.getId());
    assertEquals(Map.of(storehouse1.getId(), firstStorehouseProducts), actualReport);
  }

  @Test
  void shouldReturnProductsReport() {
    Mockito.when(productRepository.findAll()).thenReturn(List.of(product1, product2));
    List<Product> actualReport = reportService.getAllProductsInfo();
    assertEquals(List.of(product1, product2), actualReport);
  }

  @Test
  void shouldReturnProductInfoByName() {
    Mockito.when(productRepository.findByName(Mockito.any())).thenReturn(List.of(product1, product2));
    List<Product> actualReport = reportService.getProductInfo("laptop");
    assertEquals(List.of(product1, product2), actualReport);
  }

  static Stream<Arguments> testReportRemediesByStorehouse() {
    return Stream.of(
        arguments(storehouse1, firstStorehouseProducts),
        arguments(storehouse2, secondStorehouseProducts)
    );
  }
}
