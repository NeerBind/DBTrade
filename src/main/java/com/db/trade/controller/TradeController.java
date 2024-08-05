package com.db.trade.controller;

import com.db.trade.model.Trade;
import com.db.trade.service.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/trades")
@Controller
public class TradeController {

    @Autowired
    private TradeService tradeService;

    @PostMapping
    public ResponseEntity<String> addTrade(@RequestBody Trade trade) {
        tradeService.addTrade(trade);
        return ResponseEntity.ok("Trade added successfully");
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateTrade(@RequestBody Trade trade) {
        tradeService.updateTrade(trade);
        return ResponseEntity.ok("Trade updated successfully");
    }

    @GetMapping
    public ResponseEntity<List<Trade>> getAllTrades() {
        return ResponseEntity.ok(tradeService.getAllTrades());
    }

    @PutMapping("/expire")
    public ResponseEntity<String> updateExpiredFlag() {
        tradeService.updateExpiredFlag();
        return ResponseEntity.ok("Expired flags updated successfully");
    }
}
