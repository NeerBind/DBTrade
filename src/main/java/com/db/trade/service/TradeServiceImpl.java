package com.db.trade.service;

import com.db.trade.model.Trade;
import com.db.trade.repository.TradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TradeServiceImpl implements TradeService {

    @Autowired
    private TradeRepository tradeRepository;

    @Override
    public void addTrade(Trade trade) {
        if (trade.getMaturityDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Maturity date is before today's date");
        }


        List<Trade> existingTrades = tradeRepository.findAllByTradeIdOrderByVersionDesc(trade.getTradeId());
        if (!existingTrades.isEmpty()) {
            Trade latestTrade = existingTrades.get(0);
            if (trade.getVersion() < latestTrade.getVersion()) {
                throw new IllegalArgumentException("Received trade version is lower than existing version");
            }
        }

        tradeRepository.save(trade);
    }

    @Override
    public void updateTrade(Trade trade) {

        List<Trade> existingTrades = tradeRepository.findAllByTradeIdOrderByVersionDesc(trade.getTradeId());
        if (!existingTrades.isEmpty()) {
            Trade latestTrade = existingTrades.get(0);
            if (trade.getVersion() < latestTrade.getVersion()) {
                throw new IllegalArgumentException("Received trade version is lower than existing version");
            }
        }
        tradeRepository.save(trade);
    }

    @Override
    public void updateExpiredFlag() {
        tradeRepository.findAll().forEach(trade -> {
            if (trade.getMaturityDate().isBefore(LocalDate.now())) {
                trade.setExpired("Y");
                tradeRepository.save(trade);
            }
        });
    }

    @Override
    public List<Trade> getAllTrades() {
        return tradeRepository.findAll();
    }
}
