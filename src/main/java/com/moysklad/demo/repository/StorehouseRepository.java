package com.moysklad.demo.repository;

import com.moysklad.demo.entity.Storehouse;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StorehouseRepository extends JpaRepository<Storehouse, Long> {
  @Query(value = "select s from Storehouse s where s.name=:storehouseName")
  Optional<Storehouse> findByName(@Param("storehouseName") String storehouseName);
}
