package com.supplyr.supplyr.offer;

import com.supplyr.supplyr.controller.OfferController;
import com.supplyr.supplyr.domain.*;
import com.supplyr.supplyr.exception.BadRequestException;
import com.supplyr.supplyr.service.OfferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OfferControllerUnitTests {

    @Mock
    private OfferService offerService;

    private OfferController offerController;

    private OrganisationalUnit it;
    private Asset cpuHours;

    @BeforeEach
    public void setUp() {

        offerController = new OfferController(offerService);

        it = new OrganisationalUnit();
        it.setId(1L);
        it.setCredits(10);
        it.setName("IT");

        cpuHours = new Asset();
        cpuHours.setAssetId(1L);
        cpuHours.setName("CPU Hours");
    }

    @Test
    public void addBuyOffer() {

        LocalDateTime timestamp = LocalDateTime.now();
        OfferRequest buyOffer = new OfferRequest();
        buyOffer.setAsset("CPU Hours");
        buyOffer.setOrganisationalUnit("IT");
        buyOffer.setPrice(1);
        buyOffer.setQuantity(5);

        Offer approvedOffer = new Offer();
        approvedOffer.setPrice(1);
        approvedOffer.setQuantity(5);
        approvedOffer.setAsset(cpuHours);
        approvedOffer.setId(1L);
        approvedOffer.setOrganisationalUnit(it);
        approvedOffer.setFulfilled(false);
        approvedOffer.setTimestamp(timestamp);
        approvedOffer.setType(OfferType.BUY);

        when(offerService.addBuyOffer(any())).thenReturn(approvedOffer);
        Offer result = offerController.addBuyOffer(buyOffer);

        assertEquals(approvedOffer.getAsset().getName(), result.getAsset().getName());

    }

    @Test
    public void addOfferWithNotEnoughCredits() throws Exception {
        LocalDateTime timestamp = LocalDateTime.now();
        OfferRequest buyOffer = new OfferRequest();
        buyOffer.setAsset("CPU Hours");
        buyOffer.setOrganisationalUnit("IT");
        buyOffer.setPrice(1);
        buyOffer.setQuantity(5);

        Offer approvedOffer = new Offer();
        approvedOffer.setPrice(1);
        approvedOffer.setQuantity(11);
        approvedOffer.setAsset(cpuHours);
        approvedOffer.setId(1L);
        approvedOffer.setOrganisationalUnit(it);
        approvedOffer.setFulfilled(false);
        approvedOffer.setTimestamp(timestamp);
        approvedOffer.setType(OfferType.BUY);

        when(offerService.addBuyOffer(any(OfferRequest.class)))
                .thenThrow(new BadRequestException("Insufficient funds to complete BUY offer"));


        assertThrows(BadRequestException.class, () -> {
            offerController.addBuyOffer(buyOffer);
        });

    }

    @Test
    public void addOfferWithNotEnoughAssets() throws Exception {
        LocalDateTime timestamp = LocalDateTime.now();
        OfferRequest buyOffer = new OfferRequest();
        buyOffer.setAsset("CPU Hours");
        buyOffer.setOrganisationalUnit("IT");
        buyOffer.setPrice(1);
        buyOffer.setQuantity(20);

        Offer approvedOffer = new Offer();
        approvedOffer.setPrice(1);
        approvedOffer.setQuantity(11);
        approvedOffer.setAsset(cpuHours);
        approvedOffer.setId(1L);
        approvedOffer.setOrganisationalUnit(it);
        approvedOffer.setFulfilled(false);
        approvedOffer.setTimestamp(timestamp);
        approvedOffer.setType(OfferType.SELL);

        when(offerService.addSellOffer(any(OfferRequest.class))).thenThrow(new BadRequestException("Not enough CPU Hours to complete the offer request"));

        assertThrows(BadRequestException.class, () -> {
            offerController.addSellOffer(buyOffer);
        });

    }

    @Test
    public void addOfferWithNegativePrice() throws Exception {
        LocalDateTime timestamp = LocalDateTime.now();
        OfferRequest buyOffer = new OfferRequest();
        buyOffer.setAsset("CPU Hours");
        buyOffer.setOrganisationalUnit("IT");
        buyOffer.setPrice(-1);
        buyOffer.setQuantity(20);

        Offer approvedOffer = new Offer();
        approvedOffer.setPrice(1);
        approvedOffer.setQuantity(11);
        approvedOffer.setAsset(cpuHours);
        approvedOffer.setId(1L);
        approvedOffer.setOrganisationalUnit(it);
        approvedOffer.setFulfilled(false);
        approvedOffer.setTimestamp(timestamp);
        approvedOffer.setType(OfferType.SELL);

        when(offerService.addSellOffer(any(OfferRequest.class))).thenThrow(new BadRequestException("Cannot place an offer for a negative amount"));

        assertThrows(BadRequestException.class, () -> {
            offerController.addSellOffer(buyOffer);
        });

    }

    @Test
    public void addSellOffer() throws Exception {
        LocalDateTime timestamp = LocalDateTime.now();
        OfferRequest sellOffer = new OfferRequest();
        sellOffer.setAsset("CPU Hours");
        sellOffer.setOrganisationalUnit("IT");
        sellOffer.setPrice(1);
        sellOffer.setQuantity(5);

        Offer approvedOffer = new Offer();
        approvedOffer.setPrice(1);
        approvedOffer.setQuantity(5);
        approvedOffer.setAsset(cpuHours);
        approvedOffer.setId(1L);
        approvedOffer.setOrganisationalUnit(it);
        approvedOffer.setFulfilled(false);
        approvedOffer.setTimestamp(timestamp);
        approvedOffer.setType(OfferType.SELL);

        when(offerService.addSellOffer(any(OfferRequest.class))).thenReturn(approvedOffer);

        Offer offer = offerController.addSellOffer(sellOffer);

        assertEquals(approvedOffer.getAsset().getName(), offer.getAsset().getName());
        assertEquals(approvedOffer.getPrice(), offer.getPrice());
        assertEquals(approvedOffer.getOrganisationalUnit().getName(), offer.getOrganisationalUnit().getName());
        assertEquals(approvedOffer.getQuantity(), offer.getQuantity());
        assertEquals(OfferType.SELL, offer.getType());
    }


    @Test
    public void deleteOffer() throws Exception {

        doNothing().when(offerService).deleteOfferById(1L);

        offerController.deleteOffer(1L);

    }


}
