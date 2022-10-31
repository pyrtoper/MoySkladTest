package com.moysklad.demo.entity;

import java.io.Serializable;

public class StorehouseProductId implements Serializable {
  private Long storehouse;
  private Long product;

  public Long getStorehouse() {
    return storehouse;
  }

  public void setStorehouse(Long storehouse) {
    this.storehouse = storehouse;
  }

  public Long getProduct() {
    return product;
  }

  public void setProduct(Long product) {
    this.product = product;
  }

  public StorehouseProductId() {
  }

  public StorehouseProductId(Long storehouse, Long product) {
    this.storehouse = storehouse;
    this.product = product;
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof StorehouseProductId)) {
      return false;
    }
    StorehouseProductId otherId = (StorehouseProductId) obj;
    return storehouse != null && product != null &&
        otherId.storehouse != null && otherId.product != null &&
        this.storehouse.equals(otherId.storehouse) && this.product.equals(otherId.product);
  }
}
