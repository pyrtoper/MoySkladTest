package com.moysklad.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import com.moysklad.demo.dto.ProductDocumentDTO;
import com.moysklad.demo.entity.Product;
import com.moysklad.demo.repository.ProductRepository;
import com.moysklad.demo.service.impl.UpdatePurchasePriceService;
import com.moysklad.demo.service.impl.UpdateSalePriceService;
import java.math.BigDecimal;
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
public class UpdatePricesServiceTests {
  @Autowired
  private UpdatePurchasePriceService updatePurchasePriceService;
  @Autowired
  private UpdateSalePriceService updateSalePriceService;
  @MockBean
  private ProductRepository productRepository;

  @BeforeEach
  void setup() {
    Product product = new Product("1", "printer");
    Mockito.when(productRepository.findByVendorCode(Mockito.any())).thenReturn(Optional.of(product));
  }
  @Test
  void shouldSetProductFieldCorrectly_Purchase() {
    ProductDocumentDTO productDocumentDTO = new ProductDocumentDTO(
        "1", 1L, new BigDecimal(100)
    );
    List<Product> actualProducts = updatePurchasePriceService.updatePrices(Set.of(productDocumentDTO));
    assertEquals(1, actualProducts.size());
    assertEquals(new BigDecimal(100), actualProducts.get(0).getLastPurchasePrice());
  }

  @Test
  void shouldSetProductFieldCorrectly_Sale() {
    ProductDocumentDTO productDocumentDTO = new ProductDocumentDTO(
        "1", 1L, new BigDecimal(100)
    );
    List<Product> actualProducts = updateSalePriceService.updatePrices(Set.of(productDocumentDTO));
    assertEquals(1, actualProducts.size());
    assertEquals(new BigDecimal(100), actualProducts.get(0).getLastSalePrice());
  }
}
