package com.smartmarket.code.annotation;

import com.smartmarket.code.util.ValidDateValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Validate that the annotated string is in YYYY/MM/DD Date format
 */

//@Target annotation describes where you can apply your custom annotation
@Target({FIELD, PARAMETER})
//@Retention annotation describes if the custom annotation should be available in the byte code
@Retention(RUNTIME)
//@Constraint annotation defined the class that is going to validate our field
@Constraint(validatedBy = ValidDateValidator.class)
public @interface ValidDate {

    String message() default "invalid date format" ;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    boolean optional() default false;

    String formatDate() default "yyyy-MM-dd'T'HH:ss:mm";

    boolean blank() default false;

}