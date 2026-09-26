package com.example.stockingestion.repository;
import com.example.stockingestion.entity.StockPriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
public interface StockRepository extends JpaRepository<StockPriceHistory,Long>{}