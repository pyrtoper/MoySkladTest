package com.moysklad.demo.service;

import com.moysklad.demo.dto.ProductVendorQuantity;
import com.moysklad.demo.entity.StorehouseProduct;
import java.util.List;
import java.util.Set;

public interface TransferService {
  List<StorehouseProduct> transfer(Long storehouseId, Set<? extends ProductVendorQuantity> productSet);
}
