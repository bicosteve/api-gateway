package com.bicosteve.api_gateway.models;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ModelsTest {

    // ---- Profile ----
    @Test
    void profileBuilderAndGettersSetters() {
        LocalDateTime now = LocalDateTime.now();
        ProfileSettings settings = ProfileSettings.builder().status(1).build();
        Profile p = Profile.builder()
                .profileId(10L)
                .phoneNumber("254701234567")
                .password("secret")
                .createdAt(now)
                .modifiedAt(now)
                .profileSettings(settings)
                .build();

        assertEquals(10L, p.getProfileId());
        assertEquals("254701234567", p.getPhoneNumber());
        assertEquals("secret", p.getPassword());
        assertEquals(now, p.getCreatedAt());
        assertEquals(now, p.getModifiedAt());
        assertSame(settings, p.getProfileSettings());

        p.setPhoneNumber("254700000000");
        assertEquals("254700000000", p.getPhoneNumber());
    }

    @Test
    void profileNoArgsConstructor() {
        Profile p = new Profile();
        assertNull(p.getPhoneNumber());
    }

    // ---- ProfileSettings ----
    @Test
    void profileSettingsBuilder() {
        LocalDateTime t = LocalDateTime.now();
        ProfileSettings s = ProfileSettings.builder()
                .id(1L).status(1).isVerified(0).isDeleted(0)
                .profileId(10L).createAt(t).modifiedAt(t)
                .build();
        assertEquals(1, s.getStatus());
        assertEquals(0, s.getIsDeleted());
        assertEquals(10L, s.getProfileId());
    }

    @Test
    void profileSettingsNoArgsConstructor() {
        ProfileSettings s = new ProfileSettings();
        s.setStatus(2);
        assertEquals(2, s.getStatus());
    }

    // ---- Bet ----
    @Test
    void betBuilderAndSetters() {
        LocalDateTime now = LocalDateTime.now();
        Slip slip = Slip.builder().betSlipId(1).eventId("e1").odds(BigDecimal.valueOf(2.5)).build();
        Bet b = Bet.builder()
                .betId(99L).profileId(1L)
                .stake(BigDecimal.TEN).isBonus(0).status(1)
                .totalOdds(BigDecimal.valueOf(2.5))
                .possibleWin(BigDecimal.valueOf(25))
                .createdAt(now).updatedAt(now)
                .slips(List.of(slip))
                .build();

        assertEquals(99L, b.getBetId());
        assertEquals(1L, b.getProfileId());
        assertEquals(BigDecimal.TEN, b.getStake());
        assertEquals(0, b.getIsBonus());
        assertEquals(1, b.getStatus());
        assertEquals(1, b.getSlips().size());

        b.setStake(BigDecimal.ONE);
        assertEquals(BigDecimal.ONE, b.getStake());
    }

    @Test
    void betNoArgsConstructor() {
        Bet b = new Bet();
        assertNull(b.getStake());
    }

    // ---- Event ----
    @Test
    void eventBuilder() {
        OffsetDateTime now = OffsetDateTime.now();
        Team t = Team.builder().teamId(1L).name("Team A").build();
        Event e = Event.builder()
                .id(1L).eventId("e1").eventUuid("uuid").sportId(19)
                .eventDate(now).seasonType("REG").seasonYear(2026)
                .eventName("Match").eventHeadline("h").eventStatus(1)
                .teams(List.of(t))
                .build();
        assertEquals("e1", e.getEventId());
        assertEquals(19, e.getSportId());
        assertEquals(1, e.getTeams().size());
    }

    @Test
    void eventNoArgsConstructor() {
        Event e = new Event();
        e.setEventName("x");
        assertEquals("x", e.getEventName());
    }

    // ---- Market ----
    @Test
    void marketBuilder() {
        Market m = Market.builder()
                .id(1L).marketRundownId(2).marketTypeId(3).periodId(0)
                .name("moneyline").description("desc").eventId("e1")
                .build();
        assertEquals("moneyline", m.getName());
        assertEquals("e1", m.getEventId());
    }

    @Test
    void marketNoArgsConstructor() {
        Market m = new Market();
        m.setName("spread");
        assertEquals("spread", m.getName());
    }

    // ---- Participant ----
    @Test
    void participantBuilder() {
        Price price = Price.builder().priceId(1).odds(BigDecimal.valueOf(2.0)).build();
        Participant p = Participant.builder()
                .participantId(1L).rundownId(2).type("home").name("Chelsea")
                .marketId(1L).prices(List.of(price))
                .build();
        assertEquals("Chelsea", p.getName());
        assertEquals(1, p.getPrices().size());
    }

    @Test
    void participantNoArgsConstructor() {
        Participant p = new Participant();
        p.setName("Liverpool");
        assertEquals("Liverpool", p.getName());
    }

    // ---- Price ----
    @Test
    void priceBuilder() {
        OffsetDateTime closed = OffsetDateTime.now();
        Price p = Price.builder()
                .priceId(1).price(BigDecimal.valueOf(100))
                .priceDelta(BigDecimal.valueOf(0.5))
                .isMainLine(1).odds(BigDecimal.valueOf(1.5))
                .participantId(2).bookmakerId(3)
                .handicapValue("hcp=2.5").lineId("L1").closedAt(closed)
                .build();
        assertEquals(1, p.getPriceId());
        assertEquals(BigDecimal.valueOf(1.5), p.getOdds());
        assertEquals("hcp=2.5", p.getHandicapValue());
    }

    @Test
    void priceNoArgsConstructor() {
        Price p = new Price();
        p.setLineId("X1");
        assertEquals("X1", p.getLineId());
    }

    // ---- Score ----
    @Test
    void scoreBuilder() {
        Score s = Score.builder()
                .id(1L).eventId("e1").eventStatus(1)
                .teamIdHome(2).teamIdAway(3)
                .scoreHome(2).scoreAway(1)
                .gameClock(60).gamePeriod(2)
                .broadcast("Sky").venueName("Stadium").venueLocation("Nairobi")
                .build();
        assertEquals(2, s.getScoreHome());
        assertEquals("Sky", s.getBroadcast());
    }

    @Test
    void scoreNoArgsConstructor() {
        Score s = new Score();
        s.setBroadcast("BT");
        assertEquals("BT", s.getBroadcast());
    }

    // ---- Slip ----
    @Test
    void slipBuilder() {
        Slip s = Slip.builder()
                .betSlipId(1).betId(99).eventId("e1").sportId(19)
                .teamId(1).marketId(2).marketName("moneyline")
                .participantName("Chelsea").odds(BigDecimal.valueOf(2.5))
                .specialBetValue("").status(1)
                .build();
        assertEquals("Chelsea", s.getParticipantName());
        assertEquals(BigDecimal.valueOf(2.5), s.getOdds());
    }

    @Test
    void slipNoArgsConstructor() {
        Slip s = new Slip();
        s.setMarketName("spread");
        assertEquals("spread", s.getMarketName());
    }

    // ---- Team ----
    @Test
    void teamBuilder() {
        Team t = Team.builder()
                .id(1L).teamId(10L).eventId("e1").name("Chelsea")
                .mascot("Blue").abbreviation("CHE")
                .isHome(1).isAway(0)
                .record("10-2-1").conferenceId(1).divisionId(1)
                .ranking(2).leagueName("EPL")
                .build();
        assertEquals("Chelsea", t.getName());
        assertEquals(1, t.getIsHome());
    }

    @Test
    void teamNoArgsConstructor() {
        Team t = new Team();
        t.setName("Arsenal");
        assertEquals("Arsenal", t.getName());
    }

    // ---- Deposit ----
    @Test
    void depositBuilder() {
        Deposit d = Deposit.builder()
                .profileId(1L).trxRef("TRX-1")
                .amount(BigDecimal.valueOf(500))
                .currency("KES")
                .checkoutUrl("https://example.com/checkout")
                .status(0)
                .build();
        assertEquals(1L, d.getProfileId());
        assertEquals("TRX-1", d.getTrxRef());
        assertEquals(BigDecimal.valueOf(500), d.getAmount());
        assertEquals("KES", d.getCurrency());
        assertEquals(0, d.getStatus());
    }

    @Test
    void depositNoArgsConstructor() {
        Deposit d = new Deposit();
        d.setTrxRef("abc");
        assertEquals("abc", d.getTrxRef());
    }

    // ---- Transaction ----
    @Test
    void transactionBuilder() {
        LocalDateTime now = LocalDateTime.now();
        Transaction t = Transaction.builder()
                .id(1).profileId(1L).reference("REF").type(1)
                .amount(BigDecimal.valueOf(100)).status(1)
                .createdBy("system").createdAt(now).updatedAt(now)
                .build();
        assertEquals("REF", t.getReference());
        assertEquals(BigDecimal.valueOf(100), t.getAmount());
    }

    @Test
    void transactionNoArgsConstructor() {
        Transaction t = new Transaction();
        t.setReference("X");
        assertEquals("X", t.getReference());
    }

    // ---- Wallet ----
    @Test
    void walletBuilder() {
        LocalDateTime now = LocalDateTime.now();
        Wallet w = Wallet.builder()
                .id(1).profileId(1)
                .balance(BigDecimal.valueOf(1000))
                .bonus(BigDecimal.valueOf(50))
                .createdBy("system")
                .createdAt(now).updatedAt(now)
                .build();
        assertEquals(1, w.getId());
        assertEquals(BigDecimal.valueOf(1000), w.getBalance());
        assertEquals(BigDecimal.valueOf(50), w.getBonus());
    }

    @Test
    void walletNoArgsConstructor() {
        Wallet w = new Wallet();
        w.setCreatedBy("admin");
        assertEquals("admin", w.getCreatedBy());
    }
}
