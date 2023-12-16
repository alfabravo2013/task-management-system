package io.github.alfabravo2013.taskmanagement.common.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.List;

public class OneOfValidator implements ConstraintValidator<OneOf, Integer> {
    private List<Integer> permittedValues;
    @Override
    public void initialize(OneOf constraintAnnotation) {
        permittedValues = Arrays.stream(constraintAnnotation.value()).boxed().toList();
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return permittedValues.contains(value);
    }
}
