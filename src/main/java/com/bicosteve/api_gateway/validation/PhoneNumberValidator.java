package com.bicosteve.api_gateway.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber,String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context){
        if(value == null || value.isBlank()){
            return true;
        }

        String cleanedValue = value.replaceAll("[\\s-]","");
        boolean isValid = cleanedValue
                .matches("^(07|01)\\d{8}") || cleanedValue.matches("^(\\+?254)(7|1)\\d{8}$");

        if(!isValid){
            context.disableDefaultConstraintViolation();
            context.
                    buildConstraintViolationWithTemplate("Phone number must be valid Kenyan number eg 0712345678 or +254712345678")
                    .addPropertyNode(("phoneNumber"))
                    .addConstraintViolation();
        }
        return isValid;
    }
}
