package com.db.trade.repository;

import com.db.trade.model.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TradeRepository extends JpaRepository<Trade, String> {

    @Query("SELECT t FROM Trade t WHERE t.tradeId = :tradeId AND t.version = :version")
    Optional<Trade> findByTradeIdAndVersion(@Param("tradeId") String tradeId, @Param("version") int version);

    @Query("SELECT t FROM Trade t WHERE t.tradeId = :tradeId ORDER BY t.version DESC")
    List<Trade> findAllByTradeIdOrderByVersionDesc(@Param("tradeId") String tradeId);
}
