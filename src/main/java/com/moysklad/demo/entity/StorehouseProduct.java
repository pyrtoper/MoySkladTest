package com.moysklad.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "storehouse_product")
@IdClass(StorehouseProductId.class)
public class StorehouseProduct {

  @Id
  @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE,
      CascadeType.REFRESH, CascadeType.PERSIST},
      fetch = FetchType.EAGER)
  @JoinColumn(name = "storehouse_id", referencedColumnName = "id")
  private Storehouse storehouse;

  @Id
  @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE,
      CascadeType.REFRESH, CascadeType.PERSIST},
      fetch = FetchType.EAGER)
  @JoinColumn(name = "product_id", referencedColumnName = "id")
  private Product product;

  @Column(name = "product_quantity")
  private Long productQuantity;

  public StorehouseProduct() {
  }

  public StorehouseProduct(Storehouse storehouse, Product product) {
    this.storehouse = storehouse;
    this.product = product;
    this.productQuantity = 0L;
  }

  public StorehouseProduct(Storehouse storehouse, Product product, Long productQuantity) {
    this.storehouse = storehouse;
    this.product = product;
    this.productQuantity = productQuantity;
  }

  public Storehouse getStorehouse() {
    return storehouse;
  }

  public Product getProduct() {
    return product;
  }

  public Long getProductQuantity() {
    return productQuantity;
  }

  public void setProductQuantity(Long productQuantity) {
    this.productQuantity = productQuantity;
  }

  @JsonIgnore
  public StorehouseProductId getStorehouseProductId() {
    return new StorehouseProductId(storehouse.getId(), product.getId());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    StorehouseProduct that = (StorehouseProduct) o;
    return storehouse.getId() != null && product.getVendorCode() != null &&
        storehouse.getId().equals(that.storehouse.getId())
        && product.getVendorCode().equals(that.product.getVendorCode());
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public String toString() {
    return "StorehouseId= " + storehouse.getId() + '\n' +
        "ProductId=" + product.getId();
  }
}
