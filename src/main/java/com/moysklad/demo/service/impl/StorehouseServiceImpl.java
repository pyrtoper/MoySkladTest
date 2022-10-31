package com.moysklad.demo.service.impl;

import com.moysklad.demo.entity.Storehouse;
import com.moysklad.demo.exception.StorehouseDoesNotExistException;
import com.moysklad.demo.repository.StorehouseProductRepository;
import com.moysklad.demo.repository.StorehouseRepository;
import com.moysklad.demo.service.StorehouseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StorehouseServiceImpl implements StorehouseService {

  private final StorehouseRepository storehouseRepository;
  private final StorehouseProductRepository storehouseProductRepository;

  public StorehouseServiceImpl(StorehouseRepository storehouseRepository,
      StorehouseProductRepository storehouseProductRepository) {
    this.storehouseRepository = storehouseRepository;
    this.storehouseProductRepository = storehouseProductRepository;
  }

  @Override
  @Transactional
  public void deleteById(Long storehouseId) {
    Storehouse storehouse = storehouseRepository.findById(storehouseId).orElseThrow(
        StorehouseDoesNotExistException::new
    );
    storehouseProductRepository.deleteAll(storehouse.getProductsAssociation());
    storehouseRepository.deleteById(storehouse.getId());
  }

  @Override
  @Transactional
  public void deleteAll() {
    storehouseRepository.findAll().forEach((storehouse) -> deleteById(storehouse.getId()));
  }
}
