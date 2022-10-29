package com.moysklad.demo.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.moysklad.demo.entity.Product;
import com.moysklad.demo.entity.Storehouse;
import java.util.ArrayList;
import java.util.List;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class StorehouseValidationTests {
  @Autowired
  private Validator validator;

  @Test
  void blankName_ShouldThrowException() {
    Storehouse storehouse = new Storehouse();
    List<ConstraintViolation<Storehouse>> violations =
        new ArrayList<>(validator.validate(storehouse));
    assertEquals(1, violations.size());
    assertEquals("не должно быть пустым", violations.get(0).getMessage());
  }

  @Test
  void validStorehouse() {
    Storehouse storehouse = new Storehouse();
    storehouse.setName("storehouse");
    List<ConstraintViolation<Storehouse>> violations = new ArrayList<>(validator.validate(storehouse));
    assertTrue(violations.isEmpty());
  }
}
