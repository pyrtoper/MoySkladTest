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

@Service
public class UpdateSalePriceService implements UpdatePricesService {
  private final ProductRepository productRepository;

  public UpdateSalePriceService(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  @Override
  public List<Product> updatePrices(Set<ProductDocumentDTO> productDocumentDTOSet) {
    List<Product> products = new ArrayList<>();
    for (ProductDocumentDTO productDocumentDTO: productDocumentDTOSet) {
      Product product = productRepository.findByVendorCode(productDocumentDTO.getVendorCode())
          .orElseThrow(ProductDoesNotExist::new);
      product.setLastSalePrice(productDocumentDTO.getPrice());
      products.add(product);
    }
    return products;
  }
}
