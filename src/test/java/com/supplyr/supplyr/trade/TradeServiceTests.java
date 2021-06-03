package com.supplyr.supplyr.trade;

import com.supplyr.supplyr.domain.*;
import com.supplyr.supplyr.exception.BadRequestException;
import com.supplyr.supplyr.exception.NotFoundException;
import com.supplyr.supplyr.repository.TradeRepository;
import com.supplyr.supplyr.service.AssetService;
import com.supplyr.supplyr.service.OrganisationalUnitService;
import com.supplyr.supplyr.service.TradeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TradeServiceTests {

    @InjectMocks
    TradeService tradeService;

    @Mock
    TradeRepository tradeRepository;

    @Mock
    AssetService assetService;

    @Mock
    OrganisationalUnitService organisationalUnitService;

    private OrganisationalUnit it;
    private Asset cpuHours;

    @BeforeEach
    public void init(){
        MockitoAnnotations.openMocks(this);

        it = new OrganisationalUnit();
        it.setId(1L);
        it.setCredits(10);
        it.setName("IT");

        cpuHours = new Asset();
        cpuHours.setAssetId(1L);
        cpuHours.setName("CPU Hours");

    }


    @Test
    public void addTradeSuccess(){
        Offer offerToBeTraded = new Offer();
        offerToBeTraded.setAsset(cpuHours);
        offerToBeTraded.setOrganisationalUnit(it);
        offerToBeTraded.setPrice(5);
        offerToBeTraded.setTimestamp(LocalDateTime.now());
        offerToBeTraded.setType(OfferType.BUY);
        offerToBeTraded.setQuantity(10);

        Trade trade = new Trade();
        trade.setPrice(5);
        trade.setQuantity(5);
        trade.setType(OfferType.BUY);
        trade.setId(1L);
        trade.setAsset(cpuHours);
        trade.setOrganisationalUnit(it);

        when(tradeRepository.save(any())).thenReturn(trade);

        Trade returnedTrade = tradeService.addTrade(offerToBeTraded,10);

        assertEquals(5,returnedTrade.getPrice());
        assertEquals(5,returnedTrade.getQuantity());
        assertEquals("CPU Hours",returnedTrade.getAsset().getName());

    }

    @Test
    public void addTradeNegativeQuantity() {
        Offer offerToBeTraded = new Offer();
        offerToBeTraded.setAsset(cpuHours);
        offerToBeTraded.setOrganisationalUnit(it);
        offerToBeTraded.setPrice(5);
        offerToBeTraded.setTimestamp(LocalDateTime.now());
        offerToBeTraded.setType(OfferType.BUY);
        offerToBeTraded.setQuantity(10);


        assertThrows(BadRequestException.class, () -> {
            tradeService.addTrade(offerToBeTraded,-10);
        });

    }

    @Test
    public void getTradesByUnit() {

        Asset ramHours = new Asset();
        ramHours.setName("RAM Hours");
        ramHours.setAssetId(2L);

        Trade trade = new Trade();
        trade.setPrice(5);
        trade.setQuantity(5);
        trade.setType(OfferType.BUY);
        trade.setId(1L);
        trade.setAsset(cpuHours);
        trade.setOrganisationalUnit(it);

        Trade trade2 = new Trade();
        trade.setPrice(5);
        trade.setQuantity(10);
        trade.setType(OfferType.SELL);
        trade.setId(2L);
        trade.setAsset(ramHours);
        trade.setOrganisationalUnit(it);

        List<Trade> tradeList = new ArrayList<>();
        tradeList.add(trade);
        tradeList.add(trade2);
        when(organisationalUnitService.getOrganisationalUnitByName("IT")).thenReturn(it);
        when(tradeRepository.findTradesByOrganisationalUnit(it)).thenReturn(Optional.of(tradeList));

        List<Trade> retrievedTradeList = tradeService.getTradesByUnit("IT");

        assertEquals("RAM Hours",retrievedTradeList.get(0).getAsset().getName());


    }

    @Test
    public void getTradesByNonExistentUnit() {

        List<Trade> tradeList = new ArrayList<>();

        when(organisationalUnitService.getOrganisationalUnitByName("IT"))
                .thenThrow(new NotFoundException("Could not find Organisational Unit IT"));


        assertThrows(NotFoundException.class,() -> {
            tradeService.getTradesByUnit("IT");
        });


    }

    @Test
    public void getAssetTrades() {

        OrganisationalUnit finance = new OrganisationalUnit();
        finance.setName("Finance");
        finance.setId(2L);
        finance.setCredits(100);


        Trade trade = new Trade();
        trade.setPrice(5);
        trade.setQuantity(5);
        trade.setType(OfferType.BUY);
        trade.setId(1L);
        trade.setAsset(cpuHours);
        trade.setOrganisationalUnit(it);

        Trade trade2 = new Trade();
        trade.setPrice(5);
        trade.setQuantity(10);
        trade.setType(OfferType.SELL);
        trade.setId(2L);
        trade.setAsset(cpuHours);
        trade.setOrganisationalUnit(finance);

        List<Trade> tradeList = new ArrayList<>();
        tradeList.add(trade);
        tradeList.add(trade2);
        when(assetService.getAssetByName("CPU Hours")).thenReturn(cpuHours);
        when(tradeRepository.findTradesByAsset(cpuHours)).thenReturn(Optional.of(tradeList));

        List<Trade> retrievedTradeList = tradeService.getAssetTrades("CPU Hours");

        assertEquals("CPU Hours",retrievedTradeList.get(0).getAsset().getName());


    }


}
