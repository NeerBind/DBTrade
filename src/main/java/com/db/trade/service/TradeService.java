package com.db.trade.service;

import com.db.trade.model.Trade;

import java.util.List;

public interface TradeService {
    void addTrade(Trade trade);

    void updateTrade(Trade trade);

    void updateExpiredFlag();

    List<Trade> getAllTrades();  // Add this method
}
