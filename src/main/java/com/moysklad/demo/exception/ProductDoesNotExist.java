package com.moysklad.demo.exception;

public class ProductDoesNotExist extends StorehouseProductException {
  public ProductDoesNotExist() {
    super("Product does not exist");
  }
}
