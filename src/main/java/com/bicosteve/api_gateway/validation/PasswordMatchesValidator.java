package com.bicosteve.api_gateway.validation;

import com.bicosteve.api_gateway.dto.requests.RegisterRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator
        implements ConstraintValidator<PasswordMatches, RegisterRequest> {

    @Override
    public boolean isValid(
            RegisterRequest request,
            ConstraintValidatorContext context
    ){
        if(request.getPassword() == null || request.getConfirmPassword() == null){
            return false;
        }

        boolean matches = request.getPassword().equals(request.getConfirmPassword());

        if(!matches){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Passwords do not match")
                    .addPropertyNode("confirmPassword")
                    .addConstraintViolation();
        }
        return matches;
    }
}
