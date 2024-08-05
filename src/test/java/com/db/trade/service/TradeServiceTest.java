package com.db.trade.service;

import com.db.trade.model.Trade;
import com.db.trade.repository.TradeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
public class TradeServiceTest {

    @Autowired
    private TradeService tradeService;

    @Autowired
    private TradeRepository tradeRepository;

    @BeforeEach
    public void setUp() {
        tradeRepository.deleteAll();
    }

    @Test
    public void testAddTrade_Success() {
        Trade trade = new Trade("T1", 1, "CP-1", "B1", LocalDate.of(2025, 5, 20), LocalDate.now(), "N");
        tradeService.addTrade(trade);

        assertEquals(1, tradeRepository.findAll().size());
    }

    @Test
    public void testAddTrade_RejectLowerVersion() {
        Trade trade1 = new Trade("T1", 2, "CP-1", "B1", LocalDate.of(2025, 5, 20), LocalDate.now(), "N");
        Trade trade2 = new Trade("T1", 1, "CP-1", "B1", LocalDate.of(2025, 5, 20), LocalDate.now(), "N");

        tradeService.addTrade(trade1);

        assertThrows(IllegalArgumentException.class, () -> {
            tradeService.addTrade(trade2);
        });
    }

    @Test
    public void testAddTrade_SameVersionOverrides() {
        Trade trade1 = new Trade("T1", 1, "CP-1", "B1", LocalDate.of(2025, 5, 20), LocalDate.now(), "N");
        Trade trade2 = new Trade("T1", 1, "CP-1", "B1", LocalDate.of(2025, 5, 21), LocalDate.now(), "N");

        tradeService.addTrade(trade1);
        tradeService.addTrade(trade2);

        assertEquals(1, tradeRepository.findAll().size());
        assertEquals(LocalDate.of(2025, 5, 21), tradeRepository.findById("T1").get().getMaturityDate());
    }

    @Test
    public void testAddTrade_RejectPastMaturityDate() {
        Trade trade = new Trade("T1", 1, "CP-1", "B1", LocalDate.of(2020, 5, 20), LocalDate.now(), "N");

        assertThrows(IllegalArgumentException.class, () -> {
            tradeService.addTrade(trade);
        });
    }

    @Test
    public void testUpdateExpiredFlag() {
        Trade trade = new Trade("T1", 1, "CP-1", "B1", LocalDate.of(2025, 5, 20), LocalDate.now(), "N");
        tradeService.addTrade(trade);
        trade.setMaturityDate(LocalDate.of(2020, 5, 20));
        tradeRepository.save(trade);
        tradeService.updateExpiredFlag();

        assertEquals("Y", tradeRepository.findById("T1").get().getExpired());
    }
}
