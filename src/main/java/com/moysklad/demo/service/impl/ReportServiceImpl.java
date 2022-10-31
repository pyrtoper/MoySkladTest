package com.moysklad.demo.service.impl;

import com.moysklad.demo.dto.ProductGeneralInfoDTO;
import com.moysklad.demo.entity.Product;
import com.moysklad.demo.entity.Storehouse;
import com.moysklad.demo.entity.StorehouseProduct;
import com.moysklad.demo.exception.StorehouseDoesNotExistException;
import com.moysklad.demo.repository.ProductRepository;
import com.moysklad.demo.repository.StorehouseProductRepository;
import com.moysklad.demo.repository.StorehouseRepository;
import com.moysklad.demo.service.ReportService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportServiceImpl implements ReportService {

  private final ProductRepository productRepository;
  private final StorehouseRepository storehouseRepository;
  private final StorehouseProductRepository storehouseProductRepository;

  public ReportServiceImpl(ProductRepository productRepository,
      StorehouseRepository storehouseRepository,
      StorehouseProductRepository storehouseProductRepository) {
    this.productRepository = productRepository;
    this.storehouseRepository = storehouseRepository;
    this.storehouseProductRepository = storehouseProductRepository;
  }

  @Override
  @Transactional
  public List<Product> getAllProductsInfo() {
    return productRepository.findAll();
  }

  @Override
  @Transactional
  public List<Product> getProductInfo(String productName) {
    return productRepository.findByName(productName);
  }

  @Override
  @Transactional
  public Map<Long, List<ProductGeneralInfoDTO>> getAllRemainders() {
    List<StorehouseProduct> storehouseProducts = storehouseProductRepository.findAll();
    Map<Long, List<ProductGeneralInfoDTO>> result = new HashMap<>();
    for (StorehouseProduct storehouseProduct: storehouseProducts) {
      Storehouse storehouse = storehouseProduct.getStorehouse();
      Product product = storehouseProduct.getProduct();
      Long productQuantity = storehouseProduct.getProductQuantity();
      ProductGeneralInfoDTO productGeneralInfoDTO = new ProductGeneralInfoDTO(
          product.getVendorCode(),
          productQuantity,
          product.getName()
      );
      result.computeIfPresent(storehouse.getId(), (k, v) ->
        {v.add(productGeneralInfoDTO);
        return v;});
      result.putIfAbsent(storehouse.getId(), new ArrayList<>(List.of(productGeneralInfoDTO)));
    }
    return result;
  }

  @Override
  @Transactional
  public Map<Long, List<ProductGeneralInfoDTO>> getStorehouseRemaindersInfo(Long storehouseId) {
    Storehouse storehouse = storehouseRepository.findById(storehouseId)
        .orElseThrow(StorehouseDoesNotExistException::new);
    Map<Long, List<ProductGeneralInfoDTO>> result = new HashMap<>();
    List<ProductGeneralInfoDTO> productGeneralInfoDTOList = new ArrayList<>();
    for (StorehouseProduct storehouseProduct: storehouse.getProductsAssociation()) {
      Product product = storehouseProduct.getProduct();
      ProductGeneralInfoDTO productGeneralInfoDTO = new ProductGeneralInfoDTO(
          product.getVendorCode(),
          storehouseProduct.getProductQuantity(),
          product.getName()
      );
      productGeneralInfoDTOList.add(productGeneralInfoDTO);
    }
    result.put(storehouse.getId(), productGeneralInfoDTOList);
    return result;
  }
}
