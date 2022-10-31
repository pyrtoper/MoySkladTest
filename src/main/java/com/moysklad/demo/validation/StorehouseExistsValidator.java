package com.moysklad.demo.validation;

import com.moysklad.demo.entity.Storehouse;
import com.moysklad.demo.repository.StorehouseRepository;
import com.moysklad.demo.validation.annotation.StorehouseExistsConstraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class StorehouseExistsValidator implements
    ConstraintValidator<StorehouseExistsConstraint, Long> {

  private final StorehouseRepository storehouseRepository;

  public StorehouseExistsValidator(StorehouseRepository storehouseRepository) {
    this.storehouseRepository = storehouseRepository;
  }

  @Override
  public boolean isValid(Long l, ConstraintValidatorContext constraintValidatorContext) {
    return storehouseRepository.findById(l).isPresent();
  }

  @Override
  public void initialize(StorehouseExistsConstraint constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
  }
}
