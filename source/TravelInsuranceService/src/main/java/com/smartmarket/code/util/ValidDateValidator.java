package com.smartmarket.code.util;

import com.google.common.base.Strings;
import com.smartmarket.code.annotation.ValidDate;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ValidDateValidator implements ConstraintValidator<ValidDate, String> {

    private Boolean isOptional;
    private Boolean blank;
    private String formatDate;

    @Override
    public void initialize(ValidDate validDate) {
        this.isOptional = validDate.optional();
        this.formatDate = validDate.formatDate();
        this.blank = validDate.blank();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {

        boolean validDate = isValidFormat(formatDate, value, blank);

        return isOptional ? (validDate) : validDate;
    }

    private static boolean isValidFormat(String format, String value,Boolean blank) {
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            if (!StringUtils.isEmpty(value)) {
                date = sdf.parse(value);
                if (!value.equals(sdf.format(date))) {
                    date = null;
                }
            }else {
                if(blank){
                    return true ;
                }else {
                    return false ;
                }
            }

        } catch (ParseException ex) {
        }
        return date != null;
    }
}