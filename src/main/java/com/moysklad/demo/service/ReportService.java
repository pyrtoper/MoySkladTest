package com.moysklad.demo.service;

import com.moysklad.demo.dto.ProductGeneralInfoDTO;
import com.moysklad.demo.entity.Product;
import java.util.List;
import java.util.Map;

public interface ReportService {
  List<Product> getAllProductsInfo();

  List<Product> getProductInfo(String productName);

  Map<Long, List<ProductGeneralInfoDTO>> getAllRemainders();

  Map<Long, List<ProductGeneralInfoDTO>> getStorehouseRemaindersInfo(Long storehouseId);
}
