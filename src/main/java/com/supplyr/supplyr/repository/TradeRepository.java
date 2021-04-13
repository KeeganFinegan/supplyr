package com.supplyr.supplyr.repository;

import com.supplyr.supplyr.domain.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeRepository extends JpaRepository<Trade, Long> {
}
