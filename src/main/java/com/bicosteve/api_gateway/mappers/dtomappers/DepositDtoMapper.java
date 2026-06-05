package com.bicosteve.api_gateway.mappers.dtomappers;

import com.bicosteve.api_gateway.dto.response.DepositResponse;
import com.bicosteve.api_gateway.models.Deposit;
import org.springframework.stereotype.Component;

@Component
public class DepositDtoMapper {
    public DepositResponse toDto(Deposit deposit) {
        if (deposit == null) return null;
        DepositResponse dto = new DepositResponse();

        dto.setProfileId(deposit.getProfileId());
        dto.setAmount(deposit.getAmount());
        dto.setCurrency(deposit.getCurrency());
        dto.setCheckoutUrl(deposit.getCheckoutUrl());
        dto.setTrxRef(deposit.getTrxRef());

        return dto;
    }
}
