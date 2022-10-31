package com.moysklad.demo.validation.annotation;

import com.moysklad.demo.validation.ProductExistsValidator;
import com.moysklad.demo.validation.StorehouseExistsValidator;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = ProductExistsValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ProductExistsConstraint {
  String message() default "Entered product does not exist";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
