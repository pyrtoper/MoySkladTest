package com.moysklad.demo.dto;

import com.moysklad.demo.validation.annotation.ProductExistsConstraint;
import java.math.BigDecimal;
import java.util.Objects;
import javax.persistence.Embeddable;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Embeddable
@Table(name = "purchase_products")
public class ProductDocumentDTO implements ProductVendorQuantity {
  @NotNull
  @ProductExistsConstraint
  private String vendorCode;
  @NotNull
  @Min(value = 1, message = "Quantity should be more than 1")
  private Long quantity;
  @DecimalMin(value = "0", message = "Price should not be negative")
  @NotNull
  private BigDecimal price;

  public ProductDocumentDTO() {
  }

  public ProductDocumentDTO(String vendorCode, Long quantity, BigDecimal price){
    this.vendorCode = vendorCode;
    this.quantity = quantity;
    this.price = price;
  }

  @Override
  public String getVendorCode() {
    return vendorCode;
  }

  @Override
  public void setVendorCode(String vendorCode) {
    this.vendorCode = vendorCode;
  }

  @Override
  public Long getQuantity() {
    return quantity;
  }

  @Override
  public void setQuantity(Long quantity) {
    this.quantity = quantity;
  }


  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProductDocumentDTO that = (ProductDocumentDTO) o;
    return vendorCode.equals(that.vendorCode) && Objects.equals(quantity, that.quantity)
        && Objects.equals(price, that.price);
  }

  @Override
  public int hashCode() {
    return Objects.hash(vendorCode);
  }
}
