package com.moysklad.demo.validation;

import com.moysklad.demo.entity.Product;
import com.moysklad.demo.repository.ProductRepository;
import com.moysklad.demo.validation.annotation.ProductExistsConstraint;
import java.util.Optional;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class ProductExistsValidator implements
    ConstraintValidator<ProductExistsConstraint, String> {

  private final ProductRepository productRepository;

  public ProductExistsValidator(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  @Override
  public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
    Optional<Product> product = productRepository.findByVendorCode(s);
    return product.isPresent();
  }

  @Override
  public void initialize(ProductExistsConstraint constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
  }
}
