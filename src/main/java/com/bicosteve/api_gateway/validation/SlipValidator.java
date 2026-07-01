package com.bicosteve.api_gateway.validation;

import com.bicosteve.api_gateway.dto.requests.BetRequest;
import com.bicosteve.api_gateway.dto.requests.SlipRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.HashSet;
import java.util.Set;

public class SlipValidator implements ConstraintValidator<UniqueSlip, BetRequest>{
    @Override
    public boolean isValid(BetRequest request, ConstraintValidatorContext context){
        // Null/empty slips are handled by @NotNull/@Size on the field; skip here to avoid NPE
        // and to let those dedicated constraints report the proper message.
        if(request == null || request.getSlips() == null){
            return true;
        }

        Set<String> seen = new HashSet<>();
        for(SlipRequest slip : request.getSlips()){
            String key = slip.getEventId() + "-" + slip.getTeamId();

            if(!seen.add(key)){
                return false; // when duplicate
            }
        }
        return true;
    }
}
