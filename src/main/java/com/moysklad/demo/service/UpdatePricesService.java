package com.moysklad.demo.service;

import com.moysklad.demo.dto.ProductDocumentDTO;
import com.moysklad.demo.entity.Product;
import java.util.List;
import java.util.Set;

public interface UpdatePricesService {
  List<Product> updatePrices(Set<ProductDocumentDTO> productDocumentDTOSet);
}
