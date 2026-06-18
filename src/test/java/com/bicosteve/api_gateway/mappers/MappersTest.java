package com.bicosteve.api_gateway.mappers;

import com.bicosteve.api_gateway.dto.response.BetResponse;
import com.bicosteve.api_gateway.dto.response.DepositResponse;
import com.bicosteve.api_gateway.dto.response.EventResponse;
import com.bicosteve.api_gateway.dto.response.ProfileResponse;
import com.bicosteve.api_gateway.mappers.dtomappers.BetDtoMapper;
import com.bicosteve.api_gateway.mappers.dtomappers.DepositDtoMapper;
import com.bicosteve.api_gateway.mappers.dtomappers.EventDtoMapper;
import com.bicosteve.api_gateway.mappers.dtomappers.ProfileDtoMapper;
import com.bicosteve.api_gateway.models.Bet;
import com.bicosteve.api_gateway.models.Deposit;
import com.bicosteve.api_gateway.models.Event;
import com.bicosteve.api_gateway.models.Profile;
import com.bicosteve.api_gateway.models.ProfileSettings;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MappersTest {

    @Test
    void profileDtoMapperToDto() {
        Profile p = Profile.builder()
                .profileId(5L)
                .phoneNumber("254701234567")
                .createdAt(LocalDateTime.now())
                .profileSettings(ProfileSettings.builder()
                        .status(1).isVerified(1).isDeleted(0).build())
                .build();
        ProfileDtoMapper mapper = new ProfileDtoMapper();
        ProfileResponse r = mapper.toDto(p);
        assertEquals(5L, r.getProfileId());
        assertEquals("254701234567", r.getPhoneNumber());
        assertNotNull(r.getProfileSettings());
        assertEquals(1, r.getProfileSettings().getStatus());
    }

    @Test
    void profileDtoMapperToDtoHandlesNull() {
        ProfileDtoMapper mapper = new ProfileDtoMapper();
        assertNull(mapper.toDto(null));
    }

    @Test
    void profileDtoMapperToDtoWithoutSettings() {
        Profile p = Profile.builder()
                .profileId(1L)
                .phoneNumber("x")
                .build();
        ProfileDtoMapper mapper = new ProfileDtoMapper();
        ProfileResponse r = mapper.toDto(p);
        assertEquals(1L, r.getProfileId());
        assertNull(r.getProfileSettings());
    }

    @Test
    void betDtoMapperToDto() {
        Bet b = Bet.builder()
                .betId(10L)
                .profileId(1L)
                .stake(BigDecimal.valueOf(50))
                .totalOdds(BigDecimal.valueOf(2.5))
                .possibleWin(BigDecimal.valueOf(125))
                .isBonus(0)
                .status(1)
                .createdAt(LocalDateTime.now())
                .build();
        BetDtoMapper mapper = new BetDtoMapper();
        BetResponse r = mapper.toDto(b);
        assertEquals(10L, r.getBetId());
        assertEquals(BigDecimal.valueOf(50), r.getStake());
        assertEquals(0, BigDecimal.valueOf(125).compareTo(r.getPossibleWin()));
        assertEquals(0, BigDecimal.valueOf(2.5).compareTo(r.getTotalOdds()));
        assertEquals(1, r.getStatus());
    }

    @Test
    void betDtoMapperToDtoHandlesNull() {
        BetDtoMapper mapper = new BetDtoMapper();
        assertNull(mapper.toDto(null));
    }

    @Test
    void depositDtoMapperToDto() {
        Deposit d = Deposit.builder()
                .profileId(2L)
                .trxRef("TRX-1")
                .amount(BigDecimal.valueOf(100))
                .currency("KES")
                .checkoutUrl("https://x")
                .status(0)
                .build();
        DepositDtoMapper mapper = new DepositDtoMapper();
        DepositResponse r = mapper.toDto(d);
        assertEquals("TRX-1", r.getTrxRef());
        assertEquals(BigDecimal.valueOf(100), r.getAmount());
        assertEquals("KES", r.getCurrency());
        assertEquals("https://x", r.getCheckoutUrl());
        assertEquals(2L, r.getProfileId());
    }

    @Test
    void depositDtoMapperToDtoHandlesNull() {
        DepositDtoMapper mapper = new DepositDtoMapper();
        assertNull(mapper.toDto(null));
    }

    @Test
    void eventDtoMapperToDto() {
        Event e = Event.builder()
                .eventId("e1")
                .sportId(19)
                .eventName("Match")
                .eventDate(OffsetDateTime.now())
                .build();
        EventDtoMapper mapper = new EventDtoMapper();
        EventResponse r = mapper.toDto(e);
        assertEquals("e1", r.getEventId());
        assertEquals(19, r.getSportId());
        assertEquals("Match", r.getEventName());
        assertNotNull(r.getTeams());
        assertNotNull(r.getMarkets());
    }

    @Test
    void eventDtoMapperToDtoHandlesNull() {
        EventDtoMapper mapper = new EventDtoMapper();
        assertNull(mapper.toDto(null));
    }
}
