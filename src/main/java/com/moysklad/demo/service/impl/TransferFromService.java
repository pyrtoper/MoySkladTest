package com.moysklad.demo.service.impl;

import com.moysklad.demo.dto.ProductVendorQuantity;
import com.moysklad.demo.entity.Product;
import com.moysklad.demo.entity.Storehouse;
import com.moysklad.demo.entity.StorehouseProduct;
import com.moysklad.demo.entity.StorehouseProductId;
import com.moysklad.demo.exception.NotEnoughProductsException;
import com.moysklad.demo.exception.ProductDoesNotExist;
import com.moysklad.demo.exception.StorehouseDoesNotExistException;
import com.moysklad.demo.exception.StorehouseProductException;
import com.moysklad.demo.repository.ProductRepository;
import com.moysklad.demo.repository.StorehouseProductRepository;
import com.moysklad.demo.repository.StorehouseRepository;
import com.moysklad.demo.service.TransferService;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransferFromService implements TransferService {

  private final StorehouseRepository storehouseRepository;
  private final ProductRepository productRepository;
  private final StorehouseProductRepository storehouseProductRepository;

  public TransferFromService(StorehouseRepository storehouseRepository,
      ProductRepository productRepository,
      StorehouseProductRepository storehouseProductRepository) {
    this.storehouseRepository = storehouseRepository;
    this.productRepository = productRepository;
    this.storehouseProductRepository = storehouseProductRepository;
  }

  @Override
  @Transactional(isolation = Isolation.REPEATABLE_READ)
  public List<StorehouseProduct> transfer(Long storehouseId,
      Set<? extends ProductVendorQuantity> productDocumentDTOSet) {
    Storehouse storehouse = storehouseRepository.findById(storehouseId)
        .orElseThrow(StorehouseDoesNotExistException::new);
    List<StorehouseProduct> existingStorehouseProducts = new ArrayList<>();
    for (ProductVendorQuantity productDocumentDTO: productDocumentDTOSet) {
      Product product = productRepository.findByVendorCode(productDocumentDTO.getVendorCode())
          .orElseThrow(ProductDoesNotExist::new);
      StorehouseProduct storehouseProduct =
          storehouseProductRepository.findById(new StorehouseProductId(storehouse.getId(),
              product.getId())).orElseThrow(() ->
              new StorehouseProductException("Product in such storehouse does not exist")
          );
      Long currQuantity = storehouseProduct.getProductQuantity();
      Long quantityToDeduct = productDocumentDTO.getQuantity();
      if (currQuantity < quantityToDeduct) {
        throw new NotEnoughProductsException();
      }
      storehouseProduct.setProductQuantity(currQuantity - quantityToDeduct);
      existingStorehouseProducts.add(storehouseProduct);
    }
    storehouseProductRepository.saveAll(existingStorehouseProducts);
    return existingStorehouseProducts;
  }
}
