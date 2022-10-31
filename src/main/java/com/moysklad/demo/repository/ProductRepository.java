package com.moysklad.demo.repository;

import com.moysklad.demo.entity.Product;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ProductRepository extends JpaRepository<Product, Long> {

  @Query(value = "select p from Product p where p.vendorCode=:vendorCode")
  Optional<Product> findByVendorCode(@Param("vendorCode") String vendorCode);

  @Query(value = "select p from Product p where p.name=:name")
  List<Product> findByName(@Param("name") String name);

  @Query(value = "delete from Product p where p.vendorCode=:vendorCode")
  @Modifying
  @Transactional
  void deleteByVendorCode(@Param("vendorCode") String vendorCode);
}
