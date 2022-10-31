package com.moysklad.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashSet;
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

@Entity
@Table(name = "storehouses")
public class Storehouse {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "name")
  @NotBlank
  private String name;

  @OneToMany(mappedBy = "storehouse", cascade = {CascadeType.DETACH, CascadeType.MERGE,
      CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.REMOVE},
      fetch = FetchType.LAZY)
  @JsonIgnore
  private Set<StorehouseProduct> productsAssociation;


  public Storehouse() {
  }

  public Storehouse(String name) {
    this.name = name;
  }

  public Storehouse(Long id, String name) {
    this.id = id;
    this.name = name;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Set<StorehouseProduct> getProductsAssociation() {
    return productsAssociation;
  }

  public void addProduct(Product product, Long quantity) {
    if (productsAssociation == null) {
      productsAssociation = new HashSet<>();
    }
    StorehouseProduct storehouseProduct = new StorehouseProduct(this, product, quantity);
    productsAssociation.add(storehouseProduct);
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Storehouse)) {
      return false;
    }
    Storehouse other = (Storehouse) o;
    return id != null &&
        id.equals(other.getId());
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

  @Override
  public String toString() {
    return "Storehouse{" +
        "id=" + id +
        ", name='" + name + '\'' +
        '}';
  }
}
