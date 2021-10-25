package com.smartmarket.code.util;

import com.smartmarket.code.annotation.ValidDate;
import com.smartmarket.code.annotation.ValidTypeField;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ValidTypeFieldValidator implements ConstraintValidator<ValidTypeField, Object> {

    private Boolean isOptional;
    Class<?> typeField;

    @Override
    public void initialize(ValidTypeField validTypeField) {
        this.isOptional = validTypeField.optional();
        this.typeField = validTypeField.typeField() ;
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        boolean validTypeField = isValidFormat(typeField, value);

        return isOptional ? (validTypeField) : validTypeField;
    }

    private static boolean isValidFormat(Class<?> typeField, Object value) {

        try {
            if (!StringUtils.isEmpty(value)) {

                if (value.getClass() == typeField) {
                    return true ;
                }
            }

        } catch (Exception ex) {
        }
        return false;
    }
}