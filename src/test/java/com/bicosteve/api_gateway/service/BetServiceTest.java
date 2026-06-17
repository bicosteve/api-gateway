package com.bicosteve.api_gateway.service;

import com.bicosteve.api_gateway.dto.requests.BetRequest;
import com.bicosteve.api_gateway.dto.requests.SlipRequest;
import com.bicosteve.api_gateway.dto.response.BetResponse;
import com.bicosteve.api_gateway.dto.response.PageResponse;
import com.bicosteve.api_gateway.exceptions.BetNotFoundException;
import com.bicosteve.api_gateway.exceptions.ExpiredEventException;
import com.bicosteve.api_gateway.exceptions.IllegalArgumentException;
import com.bicosteve.api_gateway.mappers.dtomappers.BetDtoMapper;
import com.bicosteve.api_gateway.models.Bet;
import com.bicosteve.api_gateway.models.Event;
import com.bicosteve.api_gateway.repository.BetRepository;
import com.bicosteve.api_gateway.repository.EventRepository;
import com.bicosteve.api_gateway.security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BetServiceTest {

    private BetRepository betRepository;
    private BetDtoMapper betDtoMapper;
    private EventRepository eventRepository;
    private BetService service;

    @BeforeEach
    void setUp() {
        betRepository = mock(BetRepository.class);
        betDtoMapper = new BetDtoMapper();
        eventRepository = mock(EventRepository.class);
        service = new BetService(betRepository, betDtoMapper, eventRepository);
    }

    private Authentication auth(ProfileIdHolder p) {
        CustomUserDetails cud = new CustomUserDetails(
                p.profileId, "254701234567", 1, 1, 0, "x", List.of());
        return new UsernamePasswordAuthenticationToken(cud, null, List.of());
    }

    private record ProfileIdHolder(Long profileId) {}

    private SlipRequest slip(String eventId, int teamId, String marketName, double odds, String specialBetValue) {
        return new SlipRequest(eventId, 19, teamId, 1, marketName, "Chelsea", odds, specialBetValue);
    }

    private Event event(String id) {
        return Event.builder()
                .eventId(id)
                .sportId(19)
                .eventDate(OffsetDateTime.now().plusDays(1))
                .build();
    }

    @Test
    void placeBetSucceeds() {
        SlipRequest s1 = slip("e1", 1, "moneyline", 2.0, "");
        BetRequest req = new BetRequest();
        req.setStake(10.0);
        req.setIsBonus(0);
        req.setSlips(List.of(s1));

        when(eventRepository.fetchOneEvent("e1")).thenReturn(event("e1"));
        when(betRepository.addBet(any(), anyDouble())).thenReturn(99L);

        BetResponse r = service.placeBet(req, auth(new ProfileIdHolder(7L)));

        assertEquals(99L, r.getBetId());
        assertEquals(7L, r.getProfileId());
        assertNotNull(r.getTotalOdds());
    }

    @Test
    void placeBetThrowsOnDuplicateEvent() {
        BetRequest req = new BetRequest();
        req.setStake(10.0);
        req.setIsBonus(0);
        req.setSlips(List.of(
                slip("e1", 1, "moneyline", 2.0, ""),
                slip("e1", 2, "moneyline", 3.0, "")
        ));

        assertThrows(IllegalArgumentException.class,
                () -> service.placeBet(req, auth(new ProfileIdHolder(7L))));
    }

    @Test
    void placeBetThrowsWhenEventNotFound() {
        BetRequest req = new BetRequest();
        req.setStake(10.0);
        req.setIsBonus(0);
        req.setSlips(List.of(slip("missing", 1, "moneyline", 2.0, "")));

        when(eventRepository.fetchOneEvent("missing")).thenReturn(null);

        assertThrows(IllegalArgumentException.class,
                () -> service.placeBet(req, auth(new ProfileIdHolder(7L))));
    }

    @Test
    void placeBetThrowsWhenEventExpired() {
        BetRequest req = new BetRequest();
        req.setStake(10.0);
        req.setIsBonus(0);
        req.setSlips(List.of(slip("e1", 1, "moneyline", 2.0, "")));

        Event e = Event.builder().eventId("e1").sportId(19)
                .eventDate(OffsetDateTime.now().minusDays(1)).build();
        when(eventRepository.fetchOneEvent("e1")).thenReturn(e);

        assertThrows(ExpiredEventException.class,
                () -> service.placeBet(req, auth(new ProfileIdHolder(7L))));
    }

    @Test
    void placeBetThrowsWhenTotalOddsTooLow() {
        BetRequest req = new BetRequest();
        req.setStake(10.0);
        req.setIsBonus(0);
        req.setSlips(List.of(slip("e1", 1, "moneyline", 1.0, "")));

        when(eventRepository.fetchOneEvent("e1")).thenReturn(event("e1"));

        assertThrows(IllegalArgumentException.class,
                () -> service.placeBet(req, auth(new ProfileIdHolder(7L))));
    }

    @Test
    void placeBetThrowsWhenRepoReturnsInvalidId() {
        BetRequest req = new BetRequest();
        req.setStake(10.0);
        req.setIsBonus(0);
        req.setSlips(List.of(slip("e1", 1, "moneyline", 2.0, "")));

        when(eventRepository.fetchOneEvent("e1")).thenReturn(event("e1"));
        when(betRepository.addBet(any(), anyDouble())).thenReturn(0L);

        assertThrows(IllegalArgumentException.class,
                () -> service.placeBet(req, auth(new ProfileIdHolder(7L))));
    }

    @Test
    void getBetsReturnsPageResponse() {
        Bet bet1 = Bet.builder().betId(1L).profileId(7L)
                .stake(BigDecimal.TEN).isBonus(0).status(1)
                .totalOdds(BigDecimal.valueOf(2.5))
                .possibleWin(BigDecimal.valueOf(25)).build();
        when(betRepository.fetchBets(7L, "all", 11, 0)).thenReturn(List.of(bet1));

        PageResponse<BetResponse> page = service.getBets("all", 0, 10, auth(new ProfileIdHolder(7L)));

        assertEquals(1, page.getData().size());
        assertEquals(0, page.getPage());
        assertEquals(10, page.getLimit());
        assertFalse(page.isHasNext());
        assertFalse(page.isHasPrevious());
    }

    @Test
    void getBetsDetectsHasNext() {
        List<Bet> bets = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            bets.add(Bet.builder().betId((long) i).profileId(7L)
                    .stake(BigDecimal.TEN).isBonus(0).status(1)
                    .totalOdds(BigDecimal.valueOf(2.5))
                    .possibleWin(BigDecimal.valueOf(25)).build());
        }
        when(betRepository.fetchBets(7L, "all", 11, 0)).thenReturn(bets);

        PageResponse<BetResponse> page = service.getBets("all", 0, 3, auth(new ProfileIdHolder(7L)));

        assertEquals(3, page.getData().size());
        assertTrue(page.isHasNext());
    }

    @Test
    void getBetReturnsResponse() {
        Bet bet = Bet.builder().betId(50L).profileId(7L)
                .stake(BigDecimal.TEN).isBonus(0).status(1)
                .totalOdds(BigDecimal.valueOf(2.5))
                .possibleWin(BigDecimal.valueOf(25)).build();
        when(betRepository.fetchABet(7L, 50L)).thenReturn(bet);

        BetResponse r = service.getBet(50L, auth(new ProfileIdHolder(7L)));

        assertEquals(50L, r.getBetId());
    }

    @Test
    void getBetThrowsWhenNotFound() {
        when(betRepository.fetchABet(7L, 99L)).thenReturn(null);

        assertThrows(BetNotFoundException.class,
                () -> service.getBet(99L, auth(new ProfileIdHolder(7L))));
    }
}
