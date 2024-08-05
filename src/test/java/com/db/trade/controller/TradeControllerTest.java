package com.db.trade.controller;

import com.db.trade.repository.TradeRepository;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TradeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TradeRepository tradeRepository;

    @BeforeEach
    public void setUp() {
        tradeRepository.deleteAll();
    }

    @Test
    public void testAddTrade_Success() throws Exception {
        String tradeJson = "{\"tradeId\":\"T1\",\"version\":1,\"counterPartyId\":\"CP-1\",\"bookId\":\"B1\",\"maturityDate\":\"2025-05-20\",\"createdDate\":\"" + LocalDate.now() + "\",\"expired\":\"N\"}";

        mockMvc.perform(post("/api/v1/trades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tradeJson))
                .andExpect(status().isOk())
                .andExpect(content().string("Trade added successfully"));
    }

    @Test
    public void testAddTrade_RejectLowerVersion() throws Exception {
        String trade1Json = "{\"tradeId\":\"T1\",\"version\":2,\"counterPartyId\":\"CP-1\",\"bookId\":\"B1\",\"maturityDate\":\"2025-05-20\",\"createdDate\":\"" + LocalDate.now() + "\",\"expired\":\"N\"}";
        String trade2Json = "{\"tradeId\":\"T1\",\"version\":1,\"counterPartyId\":\"CP-1\",\"bookId\":\"B1\",\"maturityDate\":\"2025-05-20\",\"createdDate\":\"" + LocalDate.now() + "\",\"expired\":\"N\"}";
        try {
            mockMvc.perform(post("/api/v1/trades")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(trade1Json))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Trade added successfully"));

            mockMvc.perform(post("/api/v1/trades")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(trade2Json))
                    .andExpect(status().isBadRequest());
        } catch (Exception e) {
            assertInstanceOf(ServletException.class, e);
            System.out.println(e.getMessage());
            assert (e.getMessage().contains("Received trade version is lower than existing version"));
            // }
        }
    }

    @Test
    public void testAddTrade_SameVersionOverrides() throws Exception {
        String trade1Json = "{\"tradeId\":\"T1\",\"version\":1,\"counterPartyId\":\"CP-1\",\"bookId\":\"B1\",\"maturityDate\":\"2025-05-20\",\"createdDate\":\"" + LocalDate.now() + "\",\"expired\":\"N\"}";
        //String trade2Json = "{\"tradeId\":\"T1\",\"version\":1,\"counterPartyId\":\"CP-1\",\"bookId\":\"B1\",\"maturityDate\":\"2025-05-21\",\"createdDate\":\"" + LocalDate.now() + "\",\"expired
        String trade2Json = "{\"tradeId\":\"T1\",\"version\":1,\"counterPartyId\":\"CP-1\",\"bookId\":\"B1\",\"maturityDate\":\"2025-05-21\",\"createdDate\":\"" + LocalDate.now() + "\",\"expired\":\"N\"}";

        mockMvc.perform(post("/api/v1/trades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(trade1Json))
                .andExpect(status().isOk())
                .andExpect(content().string("Trade added successfully"));

        mockMvc.perform(post("/api/v1/trades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(trade2Json))
                .andExpect(status().isOk())
                .andExpect(content().string("Trade added successfully"));

        mockMvc.perform(get("/api/v1/trades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].maturityDate").value("2025-05-21"));
    }

    @Test
    public void testAddTrade_RejectPastMaturityDate() throws Exception {
        String tradeJson = "{\"tradeId\":\"T1\",\"version\":1,\"counterPartyId\":\"CP-1\",\"bookId\":\"B1\",\"maturityDate\":\"2020-05-20\",\"createdDate\":\"" + LocalDate.now() + "\",\"expired\":\"N\"}";
        try {
            mockMvc.perform(post("/api/v1/trades")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(tradeJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Maturity date is before today's date"));
        } catch (Exception e) {
            assertInstanceOf(ServletException.class, e);
            assert (e.getMessage().contains("Maturity date is before today's date"));
            // }
        }
    }

    @Test
    public void testUpdateExpiredFlag() throws Exception {
        String tradeJson = "{\"tradeId\":\"T1\",\"version\":1,\"counterPartyId\":\"CP-1\",\"bookId\":\"B1\",\"maturityDate\":\"2025-05-20\",\"createdDate\":\"" + LocalDate.now() + "\",\"expired\":\"N\"}";

        mockMvc.perform(post("/api/v1/trades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tradeJson))
                .andExpect(status().isOk())
                .andExpect(content().string("Trade added successfully"));
        //tradeRepository.save()

        String tradeJson2 = "{\"tradeId\":\"T1\",\"version\":1,\"counterPartyId\":\"CP-1\",\"bookId\":\"B1\",\"maturityDate\":\"2020-05-20\",\"createdDate\":\"" + LocalDate.now() + "\",\"expired\":\"N\"}";
        mockMvc.perform(post("/api/v1/trades/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tradeJson2))
                .andExpect(status().isOk())
                .andExpect(content().string("Trade updated successfully"));

        mockMvc.perform(put("/api/v1/trades/expire"))
                .andExpect(status().isOk())
                .andExpect(content().string("Expired flags updated successfully"));

        mockMvc.perform(get("/api/v1/trades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].expired").value("Y"));
    }
}
