package com.moysklad.demo.service.impl;

import com.moysklad.demo.entity.Product;
import com.moysklad.demo.exception.ProductDoesNotExist;
import com.moysklad.demo.repository.ProductRepository;
import com.moysklad.demo.repository.StorehouseProductRepository;
import com.moysklad.demo.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductServiceImpl implements ProductService {

  private final ProductRepository productRepository;
  private final StorehouseProductRepository storehouseProductRepository;

  public ProductServiceImpl(ProductRepository productRepository,
      StorehouseProductRepository storehouseProductRepository) {
    this.productRepository = productRepository;
    this.storehouseProductRepository = storehouseProductRepository;
  }

  @Override
  @Transactional
  public void deleteByVendorCode(String vendorCode) {
    Product product = productRepository.findByVendorCode(vendorCode).orElseThrow(
        ProductDoesNotExist::new
    );
    storehouseProductRepository.deleteAll(product.getStorehouseProductsAssociation());
    productRepository.deleteById(product.getId());
  }

  @Override
  @Transactional
  public void deleteAll() {
    productRepository.findAll().forEach((product) ->
        deleteByVendorCode(product.getVendorCode()));
  }
}
