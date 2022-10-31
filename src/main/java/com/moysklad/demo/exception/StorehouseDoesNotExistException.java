package com.moysklad.demo.exception;

public class StorehouseDoesNotExistException extends StorehouseProductException {
  public StorehouseDoesNotExistException() {
    super("Storehouse does not exist");
  }
}
