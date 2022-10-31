package com.moysklad.demo.service.impl;

import com.moysklad.demo.entity.Sale;
import com.moysklad.demo.repository.SaleRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SaleService {

  private final TransferFromService transferFromService;
  private final UpdateSalePriceService updateSalePriceService;
  private final SaleRepository saleRepository;

  public SaleService(TransferFromService transferFromService,
      UpdateSalePriceService updateSalePriceService,
      SaleRepository saleRepository) {
    this.transferFromService = transferFromService;
    this.updateSalePriceService = updateSalePriceService;
    this.saleRepository = saleRepository;
  }

  @Transactional(isolation = Isolation.REPEATABLE_READ)
  public Sale process(Sale sale) {
    transferFromService.transfer(sale.getStorehouseId(), sale.getProductSet());
    updateSalePriceService.updatePrices(sale.getProductSet());
    return saleRepository.saveAndFlush(sale);
  }

  @Transactional
  public List<Sale> getSales() {
    return saleRepository.findAll();
  }

  public Optional<Sale> findById(Long saleId) {
    return saleRepository.findById(saleId);
  }
  @Transactional
  public void deleteById(Long saleId) {
    saleRepository.deleteById(saleId);
  }
  @Transactional
  public void deleteAll() {
    saleRepository.deleteAll();
  }

  @Transactional
  public Sale save(Sale sale) {
    return saleRepository.save(sale);
  }
}
