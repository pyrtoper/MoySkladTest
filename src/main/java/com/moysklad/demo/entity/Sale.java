package com.moysklad.demo.entity;

import com.moysklad.demo.dto.ProductDocumentDTO;
import com.moysklad.demo.validation.annotation.StorehouseExistsConstraint;
import java.util.Set;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "sales")
public class Sale {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "storehouse_id")
  @NotNull
  @StorehouseExistsConstraint
  private Long storehouseId;
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "sale_products")
  @NotNull
  @Valid
  private Set<ProductDocumentDTO> productSet;

  public Sale() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getStorehouseId() {
    return storehouseId;
  }
  public void setStorehouseId(Long storehouse) {
    this.storehouseId = storehouse;
  }
  public Set<ProductDocumentDTO> getProductSet() {
    return productSet;
  }

  public void setProductSet(Set<ProductDocumentDTO> productList) {
    this.productSet = productList;
  }
}
