package com.moysklad.demo.dto;

import java.util.Objects;

public class ProductGeneralInfoDTO extends ProductTransferDTO{

  private final String productName;

  public ProductGeneralInfoDTO(String vendorCode, Long quantity, String productName) {
    super(vendorCode, quantity);
    this.productName = productName;
  }

  @Override
  public String toString() {
    return "ProductGeneralInfoDTO{" +
        "vendorCode" + getVendorCode() + '\'' +
        "productName='" + productName + '\'' +
        "quantity" + getQuantity() + '\'' +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProductGeneralInfoDTO that = (ProductGeneralInfoDTO) o;
    return getVendorCode().equals(that.getVendorCode());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getVendorCode());
  }
}
