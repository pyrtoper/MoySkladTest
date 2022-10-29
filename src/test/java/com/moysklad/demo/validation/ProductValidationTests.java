package com.moysklad.demo.validation;

import static org.junit.jupiter.api.Assertions.*;
import com.moysklad.demo.entity.Product;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ProductValidationTests {

  @Autowired
  private Validator validator;

  @Test
  void blankName_shouldThrowException() {
    Product product = new Product();
    product.setVendorCode("1");
    List<ConstraintViolation<Product>> violations = new ArrayList<>(validator.validate(product));
    assertEquals(1, violations.size());
    assertEquals("не должно быть пустым", violations.get(0).getMessage());
  }

  @Test
  void blankVendorCode_shouldThrowException() {
    Product product = new Product();
    product.setName("laptop");
    List<ConstraintViolation<Product>> violations = new ArrayList<>(validator.validate(product));
    assertEquals(1, violations.size());
    assertEquals("не должно быть пустым", violations.get(0).getMessage());
  }

  @Test
  void validProduct() {
    Product product = new Product();
    product.setVendorCode("1");
    product.setName("laptop");
    List<ConstraintViolation<Product>> violations = new ArrayList<>(validator.validate(product));
    assertTrue(violations.isEmpty());
  }
}
