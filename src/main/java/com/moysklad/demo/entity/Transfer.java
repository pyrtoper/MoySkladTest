package com.moysklad.demo.entity;

import com.moysklad.demo.dto.ProductTransferDTO;
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
@Table(name = "transfers")
public class Transfer {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;
  @Column(name = "storehouse_from_id")
  @NotNull
  @StorehouseExistsConstraint
  private Long storehouseFromId;
  @Column(name = "storehouse_to_id")
  @NotNull
  @StorehouseExistsConstraint
  private Long storehouseToId;
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "transfer_products")
  @Valid
  private Set<ProductTransferDTO> productSet;


  public Transfer() {
  }

  public Transfer(Long storehouseFromId, Long storehouseToId, Set<ProductTransferDTO> productSet) {
    this.storehouseFromId = storehouseFromId;
    this.storehouseToId = storehouseToId;
    this.productSet = productSet;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getStorehouseFromId() {
    return storehouseFromId;
  }

  public void setStorehouseFromId(Long storehouseFrom) {
    this.storehouseFromId = storehouseFrom;
  }

  public Long getStorehouseToId() {
    return storehouseToId;
  }

  public void setStorehouseToId(Long storehouseTo) {
    this.storehouseToId = storehouseTo;
  }

  public Set<ProductTransferDTO> getProductSet() {
    return productSet;
  }

  public void setProductSet(Set<ProductTransferDTO> productSet) {
    this.productSet = productSet;
  }
}
