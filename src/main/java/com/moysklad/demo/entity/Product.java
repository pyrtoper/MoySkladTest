package com.moysklad.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import org.hibernate.annotations.NaturalId;

@Entity
@Table(name = "products")
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;
  @NaturalId
  @Column(name = "vendor_code")
  @NotBlank
  private String vendorCode;
  @Column(name = "name")
  @NotBlank
  private String name;
  @Column(name = "last_purchase_price")
  private BigDecimal lastPurchasePrice;
  @Column(name = "last_sale_price")
  private BigDecimal lastSalePrice;

  @OneToMany(mappedBy = "product", cascade = {CascadeType.DETACH, CascadeType.MERGE,
      CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.REMOVE},
      fetch = FetchType.LAZY)
  @JsonIgnore
  private Set<StorehouseProduct> storehouseProductsAssociation;


  public Product() {
  }

  public Product(String vendorCode, String name) {
    this.vendorCode = vendorCode;
    this.name = name;
  }

  public Product(String vendorCode, String name, BigDecimal lastPurchasePrice,
      BigDecimal lastSalePrice) {
    this.vendorCode = vendorCode;
    this.name = name;
    this.lastPurchasePrice = lastPurchasePrice;
    this.lastSalePrice = lastSalePrice;
  }

  public String getVendorCode() {
    return vendorCode;
  }

  public void setVendorCode(String vendorCode) {
    this.vendorCode = vendorCode;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public BigDecimal getLastPurchasePrice() {
    return lastPurchasePrice;
  }

  public void setLastPurchasePrice(BigDecimal lastPurchasePrice) {
    this.lastPurchasePrice = lastPurchasePrice;
  }

  public BigDecimal getLastSalePrice() {
    return lastSalePrice;
  }

  public void setLastSalePrice(BigDecimal lastSalePrice) {
    this.lastSalePrice = lastSalePrice;
  }

  public void addStorehouse(Storehouse storehouse, Long quantity) {
    if (this.storehouseProductsAssociation == null) {
      this.storehouseProductsAssociation = new HashSet<>();
    }
    this.storehouseProductsAssociation.add(new StorehouseProduct(storehouse, this, quantity));
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Set<StorehouseProduct> getStorehouseProductsAssociation() {
    return storehouseProductsAssociation;
  }

  @Override
  public String toString() {
    return "Product{" +
        "id='" + id + '\n' +
        "vendorCode='" + vendorCode + '\'' +
        ", name='" + name + '\'' +
        ", lastPurchasePrice=" + lastPurchasePrice +
        ", lastSalePrice=" + lastSalePrice +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Product)) {
      return false;
    }
    Product product = (Product) o;
    return Objects.equals(getVendorCode(), product.getVendorCode());
  }

  public Long getId() {
    return id;
  }

  @Override
  public int hashCode() {
    return Objects.hash(getVendorCode());
  }
}
