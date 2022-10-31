package com.moysklad.demo.service.impl;

import com.moysklad.demo.dto.ProductDocumentDTO;
import com.moysklad.demo.entity.Product;
import com.moysklad.demo.exception.ProductDoesNotExist;
import com.moysklad.demo.repository.ProductRepository;
import com.moysklad.demo.service.UpdatePricesService;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
@Service
public class UpdatePurchasePriceService implements UpdatePricesService {

  private final ProductRepository productRepository;

  public UpdatePurchasePriceService(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  @Override
  @Transactional(isolation = Isolation.REPEATABLE_READ)
  public List<Product> updatePrices(Set<ProductDocumentDTO> productDocumentDTOSet) {
    List<Product> products = new ArrayList<>();
    for (ProductDocumentDTO productDocumentDTO: productDocumentDTOSet) {
      Product product = productRepository.findByVendorCode(productDocumentDTO.getVendorCode())
          .orElseThrow(ProductDoesNotExist::new);
      product.setLastPurchasePrice(productDocumentDTO.getPrice());
      products.add(product);
    }
    return products;
  }
}
