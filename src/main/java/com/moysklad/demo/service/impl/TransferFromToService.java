package com.moysklad.demo.service.impl;

import com.moysklad.demo.entity.Transfer;
import com.moysklad.demo.exception.StorehouseIdsAreEqualException;
import com.moysklad.demo.repository.TransferRepository;
import com.moysklad.demo.service.TransferService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransferFromToService {

  private final Map<String, TransferService> transferServiceMap;
  private final TransferRepository transferRepository;

  public TransferFromToService(Map<String, TransferService> transferServiceMap,
      TransferRepository transferRepository) {
    this.transferServiceMap = transferServiceMap;
    this.transferRepository = transferRepository;
  }

  @Transactional(isolation = Isolation.REPEATABLE_READ)
  public Transfer process(Transfer transfer) {
    if (transfer.getStorehouseFromId().equals(transfer.getStorehouseToId())) {
      throw new StorehouseIdsAreEqualException("Storehouses id are equal");
    }
    TransferService transferServiceFrom = transferServiceMap.get("transferFromService");
    TransferService transferServiceTo = transferServiceMap.get("transferToService");
    transferServiceFrom.transfer(transfer.getStorehouseFromId(), transfer.getProductSet());
    transferServiceTo.transfer(transfer.getStorehouseToId(), transfer.getProductSet());
    return transferRepository.saveAndFlush(transfer);
  }

  @Transactional
  public List<Transfer> getTransfers() {
    return transferRepository.findAll();
  }

  @Transactional
  public Optional<Transfer> findById(Long transferId) {
    return transferRepository.findById(transferId);
  }

  @Transactional
  public Transfer save(Transfer transfer) {
    return transferRepository.save(transfer);
  }

  @Transactional
  public void deleteById(Long transferId) {
    transferRepository.deleteById(transferId);
  }

  @Transactional
  public void deleteAll() {
    transferRepository.deleteAll();
  }
}
