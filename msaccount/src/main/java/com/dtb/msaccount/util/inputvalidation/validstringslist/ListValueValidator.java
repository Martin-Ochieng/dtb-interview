package com.dtb.msaccount.util.inputvalidation.validstringslist;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ListValueValidator implements ConstraintValidator<ListValue, String> {

    private Set<String> allowedValueSet;

    @Override
    public void initialize(ListValue constraintAnnotation) {
        allowedValueSet = new HashSet<>(Arrays.asList(constraintAnnotation.allowedValues()));
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || allowedValueSet.contains(value);
    }



}
