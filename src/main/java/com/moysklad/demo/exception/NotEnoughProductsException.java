package com.moysklad.demo.exception;

public class NotEnoughProductsException extends StorehouseProductException {
  public NotEnoughProductsException() {
    super("Not enough products in chosen storehouse!");
  }
}
