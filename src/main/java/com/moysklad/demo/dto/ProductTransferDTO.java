package com.moysklad.demo.dto;

import com.moysklad.demo.validation.annotation.ProductExistsConstraint;
import java.util.Objects;
import javax.persistence.Embeddable;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Embeddable
public class ProductTransferDTO implements ProductVendorQuantity {

  @NotNull
  @ProductExistsConstraint
  private String vendorCode;

  @NotNull
  @Min(value = 1, message = "Quantity should be more than 1")
  private Long quantity;

  public ProductTransferDTO() {
  }

  public ProductTransferDTO(String vendorCode, Long quantity) {
    this.vendorCode = vendorCode;
    this.quantity = quantity;
  }

  @Override
  public Long getQuantity() {
    return this.quantity;
  }

  @Override
  public String getVendorCode() {
    return this.vendorCode;
  }

  @Override
  public void setQuantity(Long quantity) {
    this.quantity = quantity;
  }

  @Override
  public void setVendorCode(String vendorCode) {
    this.vendorCode = vendorCode;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProductTransferDTO that = (ProductTransferDTO) o;
    return Objects.equals(vendorCode, that.vendorCode) && Objects.equals(quantity,
        that.quantity);
  }

  @Override
  public int hashCode() {
    return Objects.hash(vendorCode);
  }
}
