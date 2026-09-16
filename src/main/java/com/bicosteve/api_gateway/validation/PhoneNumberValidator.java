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
        boolean isValid = cleanedValue.matches("^(01|07)\\d{8}$|^\\+?254(1|7)\\d{8}$");

        if(!isValid){
            context.disableDefaultConstraintViolation();
            context.
                    buildConstraintViolationWithTemplate("Phone number must be valid Kenyan number e.g 0712345678 or +254712345678")
                    .addPropertyNode(("phoneNumber"))
                    .addConstraintViolation();
        }
        return isValid;
    }
}
