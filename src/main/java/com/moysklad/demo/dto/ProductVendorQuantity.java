package com.moysklad.demo.dto;

public interface ProductVendorQuantity {
  Long getQuantity();

  String getVendorCode();

  void setQuantity(Long quantity);

  void setVendorCode(String vendorCode);
}
