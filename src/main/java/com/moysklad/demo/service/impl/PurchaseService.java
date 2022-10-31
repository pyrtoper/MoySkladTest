package com.moysklad.demo.service.impl;

import com.moysklad.demo.entity.Purchase;
import com.moysklad.demo.repository.PurchaseRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PurchaseService{

  private final TransferToService transferToService;
  private final UpdatePurchasePriceService updatePurchasePriceService;
  private final PurchaseRepository purchaseRepository;

  public PurchaseService(TransferToService transferToService,
      UpdatePurchasePriceService updatePurchasePriceService,
      PurchaseRepository purchaseRepository) {
    this.transferToService = transferToService;
    this.updatePurchasePriceService = updatePurchasePriceService;
    this.purchaseRepository = purchaseRepository;
  }

  @Transactional(isolation = Isolation.REPEATABLE_READ)
  public Purchase process(Purchase purchase) {
    transferToService.transfer(purchase.getStorehouseId(), purchase.getProductSet());
    updatePurchasePriceService.updatePrices(purchase.getProductSet());
    purchaseRepository.save(purchase);
    return purchase;
  }

  @Transactional
  public List<Purchase> getPurchases() {
    return purchaseRepository.findAll();
  }

  @Transactional
  public Optional<Purchase> findById(Long purchaseId) {
    return purchaseRepository.findById(purchaseId);
  }
  @Transactional
  public void deleteById(Long purchaseId) {
    purchaseRepository.deleteById(purchaseId);
  }
  @Transactional
  public void deleteAll() {
    purchaseRepository.deleteAll();
  }
  @Transactional
  public Purchase save(Purchase purchase) {
    return purchaseRepository.save(purchase);
  }
}
