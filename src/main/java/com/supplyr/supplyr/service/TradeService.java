package com.supplyr.supplyr.service;

import com.supplyr.supplyr.domain.Offer;
import com.supplyr.supplyr.domain.Trade;
import com.supplyr.supplyr.repository.TradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TradeService {

    @Autowired
    TradeRepository tradeRepository;

    public void addTrade(Offer offerToBeTraded, double quantityToBeTraded) {
        Trade trade = new Trade();
        trade.setAsset(offerToBeTraded.getAsset());
        trade.setQuantity(quantityToBeTraded);
        trade.setFulfilled(true);
        trade.setPrice(offerToBeTraded.getPrice());
        trade.setTimestamp(LocalDateTime.now());
        trade.setOrganisationalUnit(offerToBeTraded.getOrganisationalUnit());
        trade.setType(offerToBeTraded.getType());
        tradeRepository.save(trade);
    }
}
