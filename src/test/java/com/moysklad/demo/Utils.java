package com.moysklad.demo;

import com.moysklad.demo.entity.Product;
import com.moysklad.demo.entity.Storehouse;
import com.moysklad.demo.entity.StorehouseProduct;
import com.moysklad.demo.entity.StorehouseProductId;
import com.moysklad.demo.repository.StorehouseProductRepository;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Utils {
  @Autowired
  private StorehouseProductRepository storehouseProductRepository;

  public Long getQuantity(Storehouse storehouse, Product product) {
    StorehouseProduct storehouseProduct = storehouseProductRepository.findById(
        new StorehouseProductId(storehouse.getId(), product.getId())
    ).orElseThrow();
    return storehouseProduct.getProductQuantity();
  }
}
