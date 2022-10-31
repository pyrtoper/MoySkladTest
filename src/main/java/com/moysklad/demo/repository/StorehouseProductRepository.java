package com.moysklad.demo.repository;

import com.moysklad.demo.entity.StorehouseProduct;
import com.moysklad.demo.entity.StorehouseProductId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StorehouseProductRepository extends
    JpaRepository<StorehouseProduct, StorehouseProductId>  {

}
