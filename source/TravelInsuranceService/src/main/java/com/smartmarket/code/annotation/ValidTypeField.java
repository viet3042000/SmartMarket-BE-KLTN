package com.smartmarket.code.annotation;

import com.smartmarket.code.util.ValidDateValidator;
import com.smartmarket.code.util.ValidTypeFieldValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Validate that the annotated string is in YYYY/MM/DD Date format
 */

@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = ValidTypeFieldValidator.class)
public @interface ValidTypeField {

    String message() default "invalid type field" ;

    boolean optional() default false;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    Class<?> typeField() default String.class;

}