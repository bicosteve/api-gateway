package com.bicosteve.api_gateway.dto.response;

import com.bicosteve.api_gateway.dto.requests.MailRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ResponseDtosTest {

    @Test
    void badRequestResponseGettersAndSetters() {
        BadRequestResponse r = new BadRequestResponse();
        r.setMessage("Bad request");
        assertEquals("Bad request", r.getMessage());
    }

    @Test
    void existsResponseBuilder() {
        ExistsResponse r = ExistsResponse.builder().message("User exists").build();
        assertEquals("User exists", r.getMessage());
    }

    @Test
    void notFoundResponseBuilder() {
        NotFoundResponse r = NotFoundResponse.builder()
                .statusCode("404")
                .message("Not found")
                .build();
        assertEquals("404", r.getStatusCode());
        assertEquals("Not found", r.getMessage());
    }

    @Test
    void serverErrorResponseBuilder() {
        ServerErrorResponse r = ServerErrorResponse.builder()
                .message("Internal error")
                .build();
        assertEquals("Internal error", r.getMessage());
    }

    @Test
    void marketResponseGetters() {
        MarketResponse m = new MarketResponse();
        m.setMarketRundownId(1);
        m.setMarketTypeId(1);
        m.setPeriodId(0);
        m.setName("moneyline");
        assertEquals(1, m.getMarketRundownId());
        assertEquals(1, m.getMarketTypeId());
        assertEquals(0, m.getPeriodId());
        assertEquals("moneyline", m.getName());
        assertNotNull(m.getParticipants());
    }

    @Test
    void participantResponseGetters() {
        ParticipantResponse p = new ParticipantResponse();
        p.setParticipantId(10L);
        p.setRundownId(20);
        p.setType("TEAM");
        p.setName("Arsenal");
        p.setMarketId(30L);
        assertEquals(10L, p.getParticipantId());
        assertEquals(20, p.getRundownId());
        assertEquals("TEAM", p.getType());
        assertEquals("Arsenal", p.getName());
        assertEquals(30L, p.getMarketId());
        assertNotNull(p.getPrices());
    }

    @Test
    void priceResponseGetters() {
        PriceResponse p = new PriceResponse();
        p.setPriceId(99);
        p.setOdds(new BigDecimal("2.5"));
        p.setParticipantId(11);
        p.setHandicapValue("2.5");
        p.setLineId("abc");
        assertEquals(99, p.getPriceId());
        assertEquals(0, new BigDecimal("2.5").compareTo(p.getOdds()));
        assertEquals(11, p.getParticipantId());
        assertEquals("2.5", p.getHandicapValue());
        assertEquals("abc", p.getLineId());
    }

    @Test
    void scoreResponseGetters() {
        ScoreResponse s = new ScoreResponse();
        s.setId(1L);
        s.setEventStatus(2);
        s.setScoreAway(1);
        s.setScoreHome(3);
        s.setGameClock(120);
        s.setGamePeriod(2);
        assertEquals(1L, s.getId());
        assertEquals(2, s.getEventStatus());
        assertEquals(1, s.getScoreAway());
        assertEquals(3, s.getScoreHome());
        assertEquals(120, s.getGameClock());
        assertEquals(2, s.getGamePeriod());
    }

    @Test
    void teamResponseGetters() {
        TeamResponse t = new TeamResponse();
        t.setId(1L);
        t.setTeamId(60L);
        t.setName("Chelsea");
        t.setIsHome(1);
        t.setIsAway(0);
        t.setLeagueName("EPL");
        assertEquals(1L, t.getId());
        assertEquals(60L, t.getTeamId());
        assertEquals("Chelsea", t.getName());
        assertEquals(1, t.getIsHome());
        assertEquals(0, t.getIsAway());
        assertEquals("EPL", t.getLeagueName());
    }

    @Test
    void eventResponseGetters() {
        EventResponse e = new EventResponse();
        e.setEventId("e1");
        e.setSportId(19);
        e.setEventName("Match");
        assertEquals("e1", e.getEventId());
        assertEquals(19, e.getSportId());
        assertEquals("Match", e.getEventName());
        assertNotNull(e.getTeams());
        assertNotNull(e.getMarkets());
    }

    @Test
    void slipResponseBuilder() {
        SlipResponse s = SlipResponse.builder()
                .betSlipId(1L)
                .betId(2L)
                .eventId("e1")
                .odds(new BigDecimal("1.5"))
                .build();
        assertEquals(1L, s.getBetSlipId());
        assertEquals(2L, s.getBetId());
        assertEquals("e1", s.getEventId());
        assertEquals(0, new BigDecimal("1.5").compareTo(s.getOdds()));
    }

    @Test
    void depositResponseBuilder() {
        DepositResponse d = DepositResponse.builder()
                .trxRef("TRX").checkoutUrl("https://x")
                .amount(BigDecimal.valueOf(100))
                .currency("KES").profileId(5L).build();
        assertEquals("TRX", d.getTrxRef());
        assertEquals("https://x", d.getCheckoutUrl());
        assertEquals(0, BigDecimal.valueOf(100).compareTo(d.getAmount()));
        assertEquals("KES", d.getCurrency());
        assertEquals(5L, d.getProfileId());
    }

    @Test
    void betResponseBuilder() {
        BetResponse b = BetResponse.builder()
                .betId(1L).profileId(2L)
                .stake(BigDecimal.TEN)
                .possibleWin(BigDecimal.valueOf(20))
                .isBonus(0)
                .status(1)
                .totalOdds(new BigDecimal("2.0"))
                .slips(List.of())
                .build();
        assertEquals(1L, b.getBetId());
        assertEquals(2L, b.getProfileId());
        assertEquals(0, BigDecimal.TEN.compareTo(b.getStake()));
        assertEquals(0, BigDecimal.valueOf(20).compareTo(b.getPossibleWin()));
        assertEquals(0, b.getIsBonus());
        assertEquals(1, b.getStatus());
        assertEquals(0, new BigDecimal("2.0").compareTo(b.getTotalOdds()));
        assertNotNull(b.getSlips());
    }

    @Test
    void profileResponseGetters() {
        ProfileResponse p = new ProfileResponse();
        p.setProfileId(1L);
        p.setPhoneNumber("254701234567");
        p.setCreatedAt(LocalDateTime.now());
        p.setProfileSettings(new ProfileSettingsResponse());
        assertEquals(1L, p.getProfileId());
        assertEquals("254701234567", p.getPhoneNumber());
        assertNotNull(p.getProfileSettings());
    }

    @Test
    void profileSettingsResponseGetters() {
        ProfileSettingsResponse s = new ProfileSettingsResponse();
        s.setStatus(1);
        s.setIsVerified(1);
        s.setIsDeleted(0);
        assertEquals(1, s.getStatus());
        assertEquals(1, s.getIsVerified());
        assertEquals(0, s.getIsDeleted());
    }

    @Test
    void testResponseBuilder() {
        TestResponse t = TestResponse.builder()
                .message("OK")
                .timestamp(LocalDateTime.now())
                .build();
        assertEquals("OK", t.getMessage());
        assertNotNull(t.getTimestamp());
    }

    @Test
    void loginResponseBuilder() {
        LoginResponse l = LoginResponse.builder()
                .accessToken("a").refreshToken("r").build();
        assertEquals("a", l.getAccessToken());
        assertEquals("r", l.getRefreshToken());
    }

    @Test
    void registerResponseBuilder() {
        RegisterResponse r = RegisterResponse.builder()
                .message("ok").verificationCode("123456").build();
        assertEquals("ok", r.getMessage());
        assertEquals("123456", r.getVerificationCode());
    }

    @Test
    void verificationResponseBuilder() {
        VerificationResponse v = VerificationResponse.builder().message("ok").build();
        assertEquals("ok", v.getMessage());
    }

    @Test
    void pageResponseBuilder() {
        PageResponse<String> p = PageResponse.<String>builder()
                .data(List.of("a", "b"))
                .page(0).limit(10)
                .hasNext(false).hasPrevious(false)
                .build();
        assertEquals(2, p.getData().size());
        assertEquals(0, p.getPage());
        assertEquals(10, p.getLimit());
        assertFalse(p.isHasNext());
        assertFalse(p.isHasPrevious());
    }

    @Test
    void mailRequestBuilder() {
        MailRequest m = MailRequest.builder()
                .to("a@b.com").subject("S").body("B").purpose("P").from("c@d.com")
                .cc(List.of("e@f.com")).build();
        assertEquals("a@b.com", m.getTo());
        assertEquals("S", m.getSubject());
        assertEquals("B", m.getBody());
        assertEquals("P", m.getPurpose());
        assertEquals("c@d.com", m.getFrom());
        assertEquals(1, m.getCc().size());
    }
}
